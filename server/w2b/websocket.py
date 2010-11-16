#
# Copyright 2010 kk-electronic a/s. 
# 
# This file is part of KKPortal.
#
# KKPortal is free software: you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# KKPortal is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with KKPortal.  If not, see <http://www.gnu.org/licenses/>.
#
from __future__ import with_statement
import sys, uuid, threading
import simplejson as json

from twisted.web import resource, server
from twisted.internet import threads
from twisted.python import log
from w2b import features


class WebSocketRoot(resource.Resource):
    '''
    It is intended to provide a comet-like interface to web requests.
    POST to this or any other cometsocket returns the path of the
    cometsocket.
    TODO: Rename MessageBox -> Comet, addResponse -> sendFrame etc.
    '''
    def getChild(self, path, request):
        '''
        We handle calls to /some/unknown/path as / were called
        by return self
        '''
        return self
    def render_GET(self, request):
        '''
        GET is called probably on /someid so we return that the cometsocket
        has expired
        '''
        request.setResponseCode(410) #HTTP 410: Gone
        return "WebSocket is closed from server"
    def render_POST(self, request):
        '''
        When posting to the root a new cometsocket is created and requests
        are added to that instance. 
        '''
        #Disable client side cache of the response
        request.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0")
        m = MessageBox()
        self.putChild(str(m.boxid), m)
        m.addRequestsFromRequest(request)
        request.setResponseCode(201) #HTTP 201: Created
        return "/" + str(m.boxid) #We return the path of the new socket

class MessageBox(resource.Resource):
    '''
    This represents an open cometsocket on which frames can be sent and received.
    '''
    def __init__(self):
        self.boxid = uuid.uuid4()
        self.txRequest = None
        self.txQueue = []
        self.features = features.getInstance()
        self.lock = threading.Lock()
        log.msg("Client Connected: %s" % self.boxid)
    def _rpcCall(self, jsonRequest):
        '''
        Parse a single jsonRequest.
        Call the security function if any
        Call the method function and get the result
        Add the result as an jsonResponse frame that is sent to the client
        '''
        id = jsonRequest.get('id')
        params = jsonRequest.get('params')
        try:
            method = jsonRequest['method']
        except:
            return self.makeError(id, "Invalid Request", -32600)
        parts = method.split(".", 1)
        
        #All our methods must contain a . since that splits the featurename from the methodname
        if len(parts) != 2:
            return self.makeError(id, "Method not found", -32601)
        securityFunction, function = self.features.getFunctions(*parts)

        if function is None or not hasattr(function, "__call__"):
            return {"result":None, "error":{"code":-32601, "message":"Method not found"}, "id": jsonRequest['id']}
        
        try:
            if securityFunction is not None:
                self.user = params[1]
                rejection = securityFunction(method,params,id)
                if rejection is not True:
                    return self.makeError(id, "Rejected by security", -31301, rejection)
                    
            #If the method is not found or the method isn't a callable function
            if isinstance(params, list):
                result = function(self,*params)
            elif params is None:
                result = function(self)
            elif isinstance(params, dict):
                result = function(self,**params)
            else:
                return self.makeError(id, "Invalid params", -32602)
        except:
            #An error occurred during the RPC call so we format an exception printout that is sent to the client
            #TODO: Hide implementation details when not in debug mode 
            import traceback
            return self.makeError(id, str(sys.exc_info()[1]), -32500, traceback.format_exception(*sys.exc_info()))
        return self.makeResult(id, result)
    
    def makeError(self,id,msg,code,data=None):
        '''
        returns an negative rpcresponse that can be json encoded
        '''
        if id:
            response = {"error": {"message":msg,"code":code}, "id": id}
            if data:
                response['error']['data']=data
            return response
    
    def makeResult(self,id,result):
        '''
        returns an positive rpcresponse that can be json encoded
        '''
        if id:
            return {"result": result, "id": id}
    def makeNotification(self,method,params):
        '''
        returns an server side event
        '''
        return {"method": method,"params": params}
    def addResponse(self, response=None):
        '''
        Used for sending frames to the client
        '''
        if not response:
            return
        log.msg("Adding response to transmit queue %s" % response)
        self.txQueue.append(response)
        #If some client is listening for answers send this one
        if self.txRequest != None:
            self.txRequest.write(json.dumps(self.txQueue))
            self.txRequest.finish()
            self.txRequest = None
            self.txQueue = []
    def addRequestsFromRequest(self, request):
        '''
        Used for processing incoming frames from the client
        '''
        request.content.seek(0, 0)
        requestBody = request.content.read()
        jsonRpcBatch = json.loads(requestBody)
        for call in jsonRpcBatch:
            log.msg("Adding request to incomming queue %s" % call)
            #Procces each call in another thread
            d = threads.deferToThread(self._rpcCall, call)
            #Make each repsonse call addResponse when it is done (thread-safe)
            d.addCallback(self.addResponse)
        
    def render_POST(self, request):
        """Post accepts new requests and schedule them for processing"""
        self.addRequestsFromRequest(request)
        #Tell the client that the call were accepted
        request.setResponseCode(202)
        return "/" + str(self.boxid)
    
    def clientCancel(self, e):
        '''
        Called when the client closes the HTTP connection
        If e is not None the client has closed the connection before the portal
        server made a response.
        '''
        if e:
            self.txRequest = None
            log.msg("Connection closed clientside %s" % e)
    
    def render_GET(self, request):
        '''
        GET on an open connection returns any available frames, or if no such
        frame exist it delays the creation of a http response. until addResponse is called
        '''
        request.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0")
        #Only one should be listening on the messagebox, so we close the previous connection
        if self.txRequest != None:
            self.txRequest.finish()
        #prn(request)
        if self.txQueue:
            #prn("Sending",len(self.txQueue),"responses")
            retval = json.dumps(self.txQueue)
            self.txQueue = []
            return retval
        else:
            #prn("Hanging client")
            self.txRequest = request
            request.notifyFinish().addCallbacks(self.clientCancel, self.clientCancel)
            return server.NOT_DONE_YET

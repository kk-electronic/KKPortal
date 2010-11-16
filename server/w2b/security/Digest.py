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
'''
Created on Aug 3, 2010

@author: albatros
'''
import hashlib
import simplejson as json
from w2b.database.portal import users

def getUser(username):
    return users.select(users.c.name == username).execute().fetchone() #@UndefinedVariable

def getRequestSignature(method,params,id):
    return ",".join((str(id),method,json.dumps(params,separators=(',',':'),sort_keys=True)))

def process(method,params,id):
    if isinstance(params, list) and len(params) > 1:
        providedDigest,username,realparams = params
        requestSignature = getRequestSignature(method, realparams, id)
        user = getUser(username)
        if user is None:
            return False
        realDigest = unicode(hashlib.sha256(user.secret + ':' + requestSignature).hexdigest())
        if isinstance(providedDigest, basestring) and providedDigest == realDigest:
            params[:] = realparams
            return True
    return False
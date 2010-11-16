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
from w2b import features
import socket

'''
Created on Aug 2, 2010

@author: Jes Andersen
'''

def getServerFQDN(context):
    '''
    Return the fully qualified domain name for this server
    '''
    #We use the socket library to figure this one out
    return socket.gethostbyaddr(socket.gethostname())[0]

def getEncryptedSessionKey(context):
    pass

def getSecurityMap(context):
    '''
    Return a map from feature to security.
    The keyset is all available features on the server.
    When the value is not null it contains the security
    method used for this feature.
    
    See w2b.security.<securityname> for more information
    '''
    return features.getInstance().getSecurityMap()
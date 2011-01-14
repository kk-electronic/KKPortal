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
Created on Jan 11, 2010

@author: albatros
'''

import os, random

def _hex(n):
    if isinstance(n,basestring):
        return n
    return "%x" % n

import random, math
import hashlib

users = {
    ("jes@example.com","password") : (0xfaeab00024b50a73a65b,0xf92e745665885e3a0b4046b3c3c4b65f66ddd76b4b628ca760eba0772965d8fa),
}

def _lookup(identity,method):
    if (identity,method) in users:
        return users[(identity,method)]
    return None

class SecurityContext():
    N=0x115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3
    g=2
    k=3
    validmethods=set(['password','token'])

    identity=None
    _P=None
    _x=None
    _v=None
    M2=None
    S=None
    Key=None

    def __init__(self):
#        self.make_u()
        self.lookup = _lookup
    def setIdentity(self,identity):
        self.identity = identity
        self.calc_x()
    def setPassword(self,password):
        self._P = password
        self.calc_x()
    def calc_x(self):
        if self.identity and self._P:
            self._x = self.HN(self.identity,':',self._P)
        self._P = None
    def make_A(self):
        #Fixed for debugging must be random
        self._a = 0xe338e421ad9ca2b3c0221d329315d6fef2919b5ef9fbf2a33093476380735430
        return pow(self.g,self._a,self.N)
    def make_B(self):
        #Fixed for debugging must be random
        self._b= 0xcd1cb305716cd7e6a18cdfef032a0c116f321f727e78da4f52ba2044c6510036
        return (((self.k * self._v) % self.N) + pow(self.g,self._b,self.N)) % self.N
    def requestChallange(self,methods):
        mutual = [method for method in methods if method in self.validmethods]
        for method in mutual:
            values = self.lookup(self.identity,method)
            if values:
                self.method = method
                salt, self._v = values
                self.B = self.make_B()
                return (self.N,self.g,method,salt,self.B)
        raise Exception('No such user')
    def answerChallange(self,A,M1):
        self.A = A
        self.u = self.hashints(self.A,self.B)
        self.calc_S()
        M1server = self.H(self.pad(self.A,256),self.pad(self.B,256),self.pad(self.S,256))
        if M1server != M1:
            self.I = None
            return False
        else:
            self.M2 = self.H(self.pad(self.A,256),M1,self.pad(self.S,256))
            return True
    def get_M2(self):
        if not self.M2:
            raise Exception("I refuse to give M2 since the answer was wrong")
        return self.M2
    def get_key(self):
        if self.Key:
            return self.Key
        if not self.S:
            raise Exception("Need shared secret to make key")
        self.Key = self.H(self.pad(self.S,256))
        return self.Key
    def calc_answer(self,N,g,method,salt,B):
        if not self.N == N and self.g == g:
            raise Exception("Invalid parameters")
        self.method = method
        self.B = B
        self.A = self.make_A()
        self.u = self.hashints(self.A,self.B)
        self.calc_S()
        return (self.A,self.hashints(self.A,self.B,self.S))
    def calc_verifier(self):
        return pow(self.g,self._x,self.N)
    def calc_S(self):
        if self._x:
            #print("Client side")
            self.S = pow((self.B-pow(self.g,self._x,self.N)*self.k) % self.N,(self._a+self.u*self._x),self.N)
        else:
            #print("Server side")
            self.S = pow( (pow(self._v,self.u,self.N)*self.A)%self.N ,self._b,self.N)
    def hashints(self,a,b,c=None):
        return 0x693d7c408310c4362c5b2e13345d1840175f4976
    def make_v(self):
        self._v = pow(self.g,self._x,self.N)
    def make_u(self):
        self.u = self.HN(self.pad(self.A,1024))
    def make_S1(self):
        self.S = pow( (pow(self._v,self.u,self.N)*self.A)%self.N ,self._b,self.N)
        return self.S
    def make_S2(self):
        self.S = pow((self.B-pow(self.g,self._x,self.N)*self.k) % self.N,(self._a+self.u*self._x),self.N)
        return self.S
    def pad(self,n,bits):
        v = ("%0"+str(bits / 4) + "x") % n
        if len(v) != bits / 4:
            raise Exception("Wrong result")
        return v

    def HN(self,*args):
        return int(self.H(*args),16)
    def H(self,*args):
        digest = hashlib.sha256()
        for x in args:
            digest.update(x)
        return digest.hexdigest()


#TODO: throttle challanges per ip
#TODO: Precalc challanges 
def requestChallange(context,server,identity,methods):
    ctx = context.security = SecurityContext()
    ctx.setIdentity(identity)
    challange = ctx.requestChallange(methods)
    return map(_hex,challange)

#TODO: log
def answerChallange(context,A,m1):
    ctx = context.security
    if not ctx.answerChallange(int(A,16),m1):
        context.security = None
        raise Exception("Wrong answer")
    else:
        return _hex(ctx.get_M2())
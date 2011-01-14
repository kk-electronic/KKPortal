#!/usr/bin/python
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

	I=None
	_P=None
	_x=None
	_v=None

	def __init__(self):
#		self.make_u()
		self.lookup = _lookup
	def setIdentity(self,identity):
		self.I = identity
		self.calc_x()
	def setPassword(self,password):
		self._P = password
		self.calc_x()
	def calc_x(self):
		if self.I and self._P:
			self._x = self.H(self.I,':',self._P)
		self._P = None
	def make_A(self):
		#Fixed for debugging must be random
		self._a = 0xe338e421ad9ca2b3c0221d329315d6fef2919b5ef9fbf2a33093476380735430
		return pow(self.g,self._a,self.N)
	def make_B(self):
		#Fixed for debugging must be random
		self._b= 0xcd1cb305716cd7e6a18cdfef032a0c116f321f727e78da4f52ba2044c6510036
		return (((self.k * self._v) % self.N) + pow(self.g,self._b,self.N)) % self.N
	def requestChallange(self,identity,methods):
		mutual = [method for method in methods if method in self.validmethods]
		for method in mutual:
			values = self.lookup(identity,method)
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
			print("Client side")
			self.S = pow((self.B-pow(self.g,self._x,self.N)*self.k) % self.N,(self._a+self.u*self._x),self.N)
		else:
			print("Server side")
			self.S = pow( (pow(self._v,self.u,self.N)*self.A)%self.N ,self._b,self.N)
	def hashints(self,a,b,c=None):
		return 0x693d7c408310c4362c5b2e13345d1840175f4976
	def make_v(self):
		self._v = pow(self.g,self._x,self.N)
	def make_u(self):
		self.u = self.H(self.pad(self.A,1024))
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

	def H(self,*args):
		digest = hashlib.sha256()
		for x in args:
			digest.update(x)
		return int(digest.hexdigest(),16)

client = SecurityContext()
client.setIdentity('jes@example.com')
client.setPassword('1234')

server = SecurityContext()
challange = server.requestChallange(client.I,client.validmethods)

print hex(client.calc_verifier())
print hex(server._v)

answer = client.calc_answer(*challange)
print hex(client.S)

server.answerChallange(*answer)
print hex(server.S)

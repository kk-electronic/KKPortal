#!/usr/bin/python
import random
import hashlib
import struct
import security

N=int("00c60f5e3389c64eedefa8429469dd186aebfb1482ac360d671bb4725726ed150eab0843ee2e4ffc06d1d92c440b8a8d30fe70c19647cedd7241b914f0a9b3c495f6ef6d47931aaec291b61f5bd46522fd281f7155d899de090ba1af34fa6c7de02b34b38068fe2ae102413e20a07af1e75df90d03e4882be6ec4cb93ad08a573b",16)
g=2

def pad(n,size):
	v = ("%0"+str(size / 4) + "x") % n
	if len(v) > size / 4:
		print len(v)
		raise Exception("Too large value %s, %s",len(v),size/4)
	return v

def H(*args):
	digest = hashlib.sha256()
	for x in args:
		digest.update(x)
	
	return int(digest.hexdigest(),16)

salt = random.SystemRandom().getrandbits(64) % N
salt = 0x000000006deb013d
identity = raw_input("Identity: ")
password = raw_input("Password: ")

if not identity:
	identity = "jes@example.com"
if not password:
	password = "1234"

x = H(pad(salt,64),pad(H(identity,':',password),256))
v = pow(g,x,N)
print("H(I,':',P) = " + pad(H(identity,':',password),256))
print("x = %s" % hex(x))
print("s = %s" % pad(salt,64))
print("('%s','password'):(%s,%s) " % (identity,hex(salt),hex(v)))

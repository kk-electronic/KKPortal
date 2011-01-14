#!/usr/bin/python
from w2b.rpc.SRP import SecurityContext
import random
import hashlib
import struct
import security

s = SecurityContext()

salt = random.SystemRandom().getrandbits(64) % s.N
identity = raw_input("Identity: ")
password = raw_input("Password: ")

if not identity:
	identity = "jes@example.com"
if not password:
	password = "1234"

s.setIdentity(identity)
s.setPassword(password)
#s.setSalt()
v = s.calc_verifier()

#x = H(pad(salt,64),pad(H(identity,':',password),256))
print("('%s','password'):(%#x,%#x) " % (identity,salt,v))

'''
Created on Apr 6, 2011

@author: albatros
'''

import urllib2
from urllib2 import HTTPError

def fetch(context,url,body):
    try:
        r = urllib2.urlopen(url,body)
        return {'code':r.code,'body':r.read()}
    except HTTPError as e:
        return {'code':e.code,'body':e.msg}
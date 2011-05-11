from __future__ import print_function
import sys,os
import subprocess,datetime
import shutil, urllib2

from waflib import Configure,Utils,TaskGen,Node
from waflib.Build import BuildContext
from waflib.Configure import ConfigurationContext
from waflib.Errors import ConfigurationError
from waflib.Task import Task
from helper import *

class Copy(Task):
	color='BLUE'
	def run(self):
		shutil.copyfile(self.inputs[0].abspath(),self.outputs[0].abspath())

@TaskGen.feature('copy')
def ping(self):
	Utils.def_attrs(self,srcglob='**')
	srcdir=getattr(self,'srcdir',None)	
	if srcdir:
		if isinstance(srcdir,Node.Node):
			srcnode=srcdir
		else:
			srcnode=self.path.find_node(srcdir)
	else:
		self.bld.fatal("You must specifiy a source dir")
	if not srcnode:
		self.bld.fatal("Invalid source directory: %s" % srcdir)
	
	outdir=getattr(self,'outdir',None)	
	if outdir:
		if not isinstance(outdir,Node.Node):
			outdir=self.path.get_bld().make_node(self.outdir)
	else:
		outdir=self.path.get_bld()
	for input in srcnode.ant_glob(self.srcglob):
		tsk = self.create_task('Copy')
		output=outdir.find_or_declare(input.path_from(srcnode))
		tsk.set_inputs(input)
		tsk.set_outputs(output)

def copyfileobj(fsrc, fdst, length=16*1024, fun=None):
	"""copy data from file-like object fsrc to file-like object fdst"""
	while 1:
		buf = fsrc.read(length)
		if not buf:
			break
		if fun:
			fun()
		fdst.write(buf)

def printdot():
	print('.',end='')

def sh(*command):
	return subprocess.Popen(command).wait()
def download(url,target=None):
	print('Downloading',end='')
	req = urllib2.urlopen(url)
	with open(target, 'wb') as fp:
		copyfileobj(req, fp,fun=printdot)
	print('Done')

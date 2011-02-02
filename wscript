from __future__ import print_function
import sys,os
import subprocess,datetime

from waflib import Configure,Utils,TaskGen,Node
from waflib.Build import BuildContext
from waflib.Task import Task
Configure.autoconfig = 1

try:
	p=subprocess.Popen(['git','describe','--always','--tags'],stdout=subprocess.PIPE)
	p.wait()
	VERSION=p.stdout.read()[:-1]
except:
	VERSION='0.' + datetime.date.today().strftime("%Y.%m.%d")
APPNAME='KKPortal'

top = '.'
out = 'waf-build'

import shutil

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

def options(ctx):
	ctx.add_option('--force',action='store_true',default=False,help='Enables potentially unsafe commands')

def configure(config):
	config.load('python')
	try:
		config.find_program('git')
		config.check_python_module('configobj')
		config.check_python_module('twisted')
		config.check_python_module('sqlalchemy')
	except Configure.ConfigurationError:
		print("Try sudo apt-get install git-core python-configobj python-twisted python-sqlalchemy")
		raise
	config.load('java')
	try:
		config.find_program('dpkg')
	except Configure.ConfigurationError, e:
		pass

def build(bld):
	print('Building',APPNAME,VERSION)
	classpath = [path.abspath() for path in bld.path.ant_glob('lib/**/*.jar')]
	bld(
		name='compiler',
		features='javac',
		srcdir='src/',
		classpath=os.pathsep.join(classpath),
		sourcepath='src',
		outdir='classes'
	)
	bld.add_group()
	classpath = [os.path.join(bld.out_dir,'classes')] + classpath
	classpath = [bld.path.find_dir('src').abspath()] + classpath
#	print(os.pathsep.join(classpath))
	classpath = os.pathsep.join(classpath)
	command = ['java',
		'-Xmx256M',
		'-classpath',classpath,
		'com.google.gwt.dev.Compiler',
		'-localWorkers','2',
		'-war',os.path.join(bld.out_dir,'war'),
		'-gen',os.path.join(bld.out_dir,'generated'),
		'com.kk_electronic.kkportal.KKPortal']
#	print('Command:',' '.join(map(str,command)))
	import time
	def x(task):
		task.exec_command(command)
	bld(
		name='GWT Compile',
		rule=x,
		source=bld.path.find_node('src').ant_glob('**')
		#after=['javac']
	)
	bld(
		features=['copy'],
		srcglob='**',
		srcdir='war-src',
		outdir='war'
	)

def buildpackage(ctx):
	if not ctx.env.DPKG:
		ctx.fatal('Cannot build package without dpkg')
	print('Old version number:',VERSION)
	print('New version number: ',end='')
	old_version = VERSION
	new_version=raw_input()
	# Sanity check: The new version must be larger or equal to the current HEAD of git
	r=ctx.exec_command([ctx.env.DPKG,'--compare-versions',new_version,'ge',VERSION])
	if r and not ctx.options.force:
		ctx.fatal('will not degrade version number without --force')

class BuildPackage(BuildContext):
	cmd = 'buildpackage'
	fun = 'buildpackage'

def runserver(ctx):
	includedir = ctx.path.find_node('server').abspath()
	import subprocess,os,sys
	if 'PYTHONPATH' in os.environ:
		os.environ['PYTHONPATH'] += os.path.pathsep + includedir
	else:
		os.environ['PYTHONPATH'] = includedir
	command = ['python',
	           os.path.join(includedir,'w2b','server.py'),
	           '-c','debug.conf',
	           '-c','user.conf',
	          ]
	p = subprocess.Popen(command).wait()

def installwindowsdep(ctx):
	import distutils
	#print('.Net from http://www.microsoft.com/downloads/en/details.aspx?FamilyID=0856eacb-4362-4b0d-8edd-aab15c5e04f5&displaylang=en')
	#print('Java from http://javadl.sun.com/webapps/download/AutoDL?BundleId=45835')
	#print('Mingw32 from http://sourceforge.net/projects/mingw/files/Automated%20MinGW%20Installer/mingw-get-inst/mingw-get-inst-20101030/mingw-get-inst-20101030.exe/download')
	#PATH??
	print('ez_setup from http://peak.telecommunity.com/dist/ez_setup.py')
	distcfgfilepath = os.path.join(os.path.dirname(distutils.__file__),'distutils.cfg')
	s="[Build]\ncompiler=mingw32"
	print(s,'>',distcfgfilepath)
	print('easy_install configobj')
	print('easy_install twisted')
	print('easy_install sqlalchemy')

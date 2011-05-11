from __future__ import print_function
import sys,os
import subprocess,datetime

from waflib import Configure,Utils,TaskGen,Node
from waflib.Build import BuildContext
from waflib.Configure import ConfigurationContext
from waflib.Errors import ConfigurationError
from waflib.Task import Task
from helper import *
Configure.autoconfig = 1

try:
	p=subprocess.Popen(['git','describe','--always','--tags'],stdout=subprocess.PIPE,stderr=subprocess.PIPE)
	VERSION,_=p.communicate(None)
except OSError:
	VERSION='0.' + datetime.date.today().strftime("%Y.%m.%d")
APPNAME='KKPortal'

top = '.'
out = 'waf-build'

if sys.platform == 'win32':
	search_path = os.environ.get('PATH', '').split(os.pathsep)
	search_path += [os.path.join(os.environ.get('PROGRAMFILES'),'Git','bin')]
	search_path += [os.path.join(sys.prefix,'Scripts')]
	search_path += [os.path.join(os.environ.get('SYSTEMDRIVE') + os.path.sep,'MinGW','bin')]
	search_path += [os.path.join(os.environ.get('PROGRAMFILES'),'MinGW','bin')]
	search_path += [os.path.join(os.environ.get('PROGRAMFILES'),'Java','jdk1.6.0_25','bin')]
	print(search_path)
	os.environ['PATH']=os.pathsep.join(search_path)

def options(ctx):
	ctx.add_option('--force',action='store_true',default=False,help='Enables potentially unsafe commands')
	
def configure(config):
	config.load('python')
	config.find_program('git',mandatory=False)
	config.find_program('dpkg',mandatory=False)
	config.check_python_module('configobj')
	config.check_python_module('twisted')
	config.check_python_module('sqlalchemy')
	config.load('java')

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
		return task.exec_command(command)
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

class ConfigPackage(ConfigurationContext):
	cmd = 'installdep'
	fun = 'installdep'
	
def installdep(config):
	import distutils
	config.load('python')
	try:
		config.find_program('git')
	except ConfigurationError:
		download('http://msysgit.googlecode.com/files/Git-1.7.3.1-preview20101002.exe','git-install.exe')
		sh('git-install.exe','/SILENT','/norestart')
		pass
	try:
		config.find_program('gcc')
	except:
		download('http://sourceforge.net/projects/mingw/files/Automated%20MinGW%20Installer/mingw-get-inst/mingw-get-inst-20101030/mingw-get-inst-20101030.exe/download','mingw-get-inst.exe')
		sh('mingw-get-inst.exe','/SILENT','/norestart')
		pass
	try:
		config.find_program('easy_install')
	except:
		download('http://peak.telecommunity.com/dist/ez_setup.py','ez_setup.py')
		sh(sys.executable,os.path.abspath('ez_setup.py'))
		pass
	try:
		config.check_python_module('configobj')
	except:
		sh(os.path.join(sys.prefix,'Scripts','easy_install'),'configobj')
	try:
		config.check_python_module('twisted')
	except:
		distcfgfilepath = os.path.join(os.path.dirname(distutils.__file__),'distutils.cfg')
		if not os.path.exists(distcfgfilepath):
			print('Creating distutils.cfg')
			with open(distcfgfilepath,"w") as file:
				file.write("[build]\ncompiler=mingw32")
		sh(os.path.join(sys.prefix,'Scripts','easy_install'),'twisted')
	try:
		config.check_python_module('sqlalchemy')
	except:
		sh(os.path.join(sys.prefix,'Scripts','easy_install'),'sqlalchemy')
	try:
		config.check_python_module('simplejson')
	except:
		sh(os.path.join(sys.prefix,'Scripts','easy_install'),'simplejson')
	try:
		config.find_program('javac')
	except:
		download('http://download.oracle.com/otn-pub/java/jdk/6u25-b06/jdk-6u25-windows-i586.exe','jdk-6u25-windows-i586.exe')
		print('Installing java...please wait')
		sh('jdk-6u25-windows-i586.exe','/quiet')
		pass
	
	#We now run the original config command to ensure that the dependencies are now fulfilled
	configure(config)
	
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
	import distutils,urllib2,subprocess
	#print('.Net from http://www.microsoft.com/downloads/en/details.aspx?FamilyID=0856eacb-4362-4b0d-8edd-aab15c5e04f5&displaylang=en')
	#print('Java from http://javadl.sun.com/webapps/download/AutoDL?BundleId=45835')
	#print('Mingw32 from http://sourceforge.net/projects/mingw/files/Automated%20MinGW%20Installer/mingw-get-inst/mingw-get-inst-20101030/mingw-get-inst-20101030.exe/download')
	#PATH??
	print('ez_setup from http://peak.telecommunity.com/dist/ez_setup.py')
	distcfgfilepath = os.path.join(os.path.dirname(distutils.__file__),'distutils.cfg')
	if not os.path.exists(distcfgfilepath):
		print('Creating distutils.cfg')
		with open(distcfgfilepath,"w") as file:
			file.write("[build]\ncompiler=mingw32")
	download('http://peak.telecommunity.com/dist/ez_setup.py','ez_setup.py')
	sh(sys.executable,os.path.abspath('ez_setup.py'))
	sh(os.path.join(sys.prefix,'Scripts','easy_install'),'configobj')
	sh(os.path.join(sys.prefix,'Scripts','easy_install'),'twisted')
	sh(os.path.join(sys.prefix,'Scripts','easy_install'),'sqlalchemy')
	sh(os.path.join(sys.prefix,'Scripts','easy_install'),'simplejson')

def resetdb(ctx):
	includedir = ctx.path.find_node('server').abspath()
	import subprocess,os,sys
	if 'PYTHONPATH' in os.environ:
		os.environ['PYTHONPATH'] += os.path.pathsep + includedir
	else:
		os.environ['PYTHONPATH'] = includedir
	command = ['python',
		os.path.join(ctx.path.find_dir('tools').abspath(),'resetDatabase.py'),
	]
	p = subprocess.Popen(command).wait()

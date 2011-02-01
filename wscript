from __future__ import print_function
import sys,os
import subprocess,datetime

from waflib import Configure,Utils
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
	try:
		config.find_program('dpkg')
	except Configure.ConfigurationError, e:
		print(e)
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
	classpath = [os.path.join(bld.out_dir,'classes')] + classpath
	classpath = [bld.path.find_dir('src').abspath()] + classpath
#	print(os.pathsep.join(classpath))
	classpath = os.pathsep.join(classpath)
	command = ['java','-Xmx256M','-classpath',classpath,'com.google.gwt.dev.Compiler','-localWorkers','2','-war',bld.out_dir,'-gen','.generated','com.kk_electronic.kkportal.KKPortal']
#	print('Command:',' '.join(map(str,command)))
#	p=subprocess.Popen(command)
#	p.wait()
#	print(' '.join(command))
	import time
	def x(task):
		task.exec_command(['ls','-l',os.path.join(bld.out_dir,'classes','com','kk_electronic','gwt','rebind')])
		task.exec_command(command)
	bld(
		name='GWT Compile',
		rule=x,
		always=True,
		after='compiler'
	)
	pass

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

from waflib.Build import BuildContext
class BuildPackage(BuildContext):
	cmd = 'buildpackage'
	fun = 'buildpackage'

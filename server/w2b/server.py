#!/usr/bin/python
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

import sys,os,atexit

if not os.environ.has_key('NO_CLOSE'):
    for i in range(3,1024):
        try:
            os.close(i)
        except OSError:
            pass

from twisted.web.twcgi import FilteredScript

from w2b.util import prn
from w2b.settings import Settings, InvokeError



class PHP5Script(FilteredScript):
    """I am a FilteredScript that uses the PHP command on most systems.
    Sometimes, php wants the path to itself as argv[0]. This is that time.
    """

    filter = '/usr/bin/php5-cgi'

def daemonize():
    """
    do the UNIX double-fork magic, see Stevens' "Advanced
    Programming in the UNIX Environment" for details (ISBN 0201563177)
    http://www.erlenstar.demon.co.uk/unix/faq_2.html#SEC16
    """
    try:
        pid = os.fork()
        if pid > 0:
            # exit first parent
            os._exit(0)
    except OSError, e:
        sys.stderr.write("fork #1 failed: %d (%s)\n" % (e.errno, e.strerror))
        sys.exit(1)

    # decouple from parent environment
    #os.chdir("/")
    os.setsid()
    os.umask(0)
    # do second fork
    try:
        pid = os.fork()
        if pid > 0:
            # exit from second parent
            os._exit(0)
    except OSError, e:
        sys.stderr.write("fork #2 failed: %d (%s)\n" % (e.errno, e.strerror))
        sys.exit(1)

    # redirect standard file descriptors
    sys.stdout.flush()
    sys.stderr.flush()
    for i in range(0,3):
        try:
            os.close(i)
        except OSError:
            pass
#    si = file('/dev/null', 'r')
#    so = file('/var/log/w2b', 'a+')
#    se = file('/var/log/w2b', 'a+', 0)
    sys.stdin = file('/dev/null', 'r')
    sys.stdout = file('/var/log/w2b', 'a+')
    sys.stderr = file('/var/log/w2b', 'a+', 0)
#    os.dup2(si.fileno(), sys.stdin.fileno())
#    os.dup2(so.fileno(), sys.stdout.fileno())
#    os.dup2(se.fileno(), sys.stderr.fileno())

def delpid(pidfile):
    try:
        os.remove(pidfile)
    except:
        pass

def makepid(pidfile):
    # write pidfile
    atexit.register(delpid,pidfile)
    pid = str(os.getpid())
    file(pidfile,'w+').write("%s\n" % pid)

def main(argv=None):
    settings = Settings()
    try:
        settings.loadDefaults()
    except InvokeError, err:
        if err.message is not None:
            prn(err.message, file=sys.stderr)
            prn("for help use --help", file=sys.stderr)
            return 2
        return 0 #The case if --help is called
    if settings.as_bool('debug'):
        from w2b import debughelp
        debughelp.run()
    if settings.as_bool('daemon'):
        daemonize()
    if settings.has_key('pidfile'):
        makepid(settings['pidfile'])
    from twisted.internet import reactor
    from twisted.web import static, server
    from twisted.python import log
    from w2b.websocket import WebSocketRoot
    
    class CacheFile(static.File):
        def makeProducer(self, request, fileForReading):
            if request.uri.find('.nocache.') != -1:
                request.setHeader('Cache-Control','no-store, no-cache, must-revalidate, post-check=0, pre-check=0')
            elif request.uri.find('.cache.') != -1:
                request.setHeader('Cache-Control','max-age=290304000, public')
            return static.File.makeProducer(self,request,fileForReading)

    if settings.has_key('logfile'):
        file=open(settings['logfile'],"wa")
        log.startLogging(file,setStdout=False)
    else:
        log.startLogging(sys.stdout,setStdout=False)
    
    if settings.has_key('starturl'):
        log.msg('Open',settings['starturl'])
    if settings.has_key('jsonconfigfile'):
        file=open(settings['jsonconfigfile'],'w')
        import json
        json.dump(settings, file)
    log.msg(os.getcwd())
    log.msg(settings['rootdir'])
    root = CacheFile(settings['rootdir'])
    from twisted.web.proxy import ReverseProxyResource
    proxy = ReverseProxyResource('windpowerhub.com',80,'/~albatros/php')
    root.putChild("php",proxy)
    
    #phproot = static.File('src/com/kk_electronic/public/clsrv/')
    #phproot.processors = {
    #                      '.php':PHP5Script
    #                      }
    #root.putChild("messagebox", MessageBoxRoot())
    root.putChild("websocket",WebSocketRoot())
    #root.putChild("clsrv", phproot)
    site = server.Site(root)
    reactor.listenTCP(settings.as_int('port'), site) #@UndefinedVariable
    reactor.run() #@UndefinedVariable

if __name__ == "__main__":
    sys.exit(main())


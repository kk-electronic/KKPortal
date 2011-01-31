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
import sys, os
from configobj import ConfigObj #@UnresolvedImport
from optparse import OptionParser

settings = None

class Settings(ConfigObj):
    '''
    Class for holding runtime configuration
    Normal search order is:
     - Command line parameters
     - /etc/w2b/w2b.conf
     - defaults.conf
    '''
    def __init__(self, filename=None, argv=None, **kwargs):
        global settings
        ConfigObj.__init__(self)
        if settings is None:
            settings = self
        if filename is not None:
            self.loadFile(filename)
        if argv is not None:
            self.loadArgv(argv)
        if kwargs:
            self.setOption(**kwargs)
    def loadDefaults(self):
        self.loadFile(os.path.realpath(os.path.dirname(__file__)) + os.sep + "defaults.conf")
        self.loadFile("/etc/w2b/w2b.conf")
        self.loadArgv(None)
    def loadArgv(self, argv=None):
        if argv is None:
            argv = sys.argv
        parser = ExceptionOptionParser(add_help_option=False, version="2.0", usage="%prog [options]")
        parser.add_option("-?", "--help", action="help", help="show this help message and exit")
        parser.add_option("-r", "--root", dest="rootdir", metavar="DIR", help="root directory")
        parser.add_option("--pyroot", dest="pyroot", metavar="DIR", help="python root directory")
        parser.add_option("-p", "--port", dest="port", type="int", help="port to listen on")
        parser.add_option("--pidfile", dest="pidfile", help="file to write pid in")
        parser.add_option("-d", "--debug", dest="debug", action="store_true", help="Enables Debug Mode which bypasses security checks and enables stack traces")
        parser.add_option("--daemon", dest="daemon", action="store_true", help="Daemonize the process")
        parser.add_option("-c", "--conffile", action="callback", type="string", callback=self._optparseCallback, help="Load a config file")
        (options, _) = parser.parse_args(argv[1:])
        options = dict([(k, v) for (k, v) in options.__dict__.iteritems() if v is not None])
        self.setOption(**options)
    def _optparseCallback(self, option, opt_str, value, parser):
        self.loadFile(value, False)
    def setOption(self, **kwargs):
        self.merge(kwargs)
    def loadFile(self, file, failonerror=False):
        fileConfig = ConfigObj(file, file_error=failonerror)
        self.merge(fileConfig)
    def get(self,settingname,default=None, domain=None):
        if domain in self.sections and settingname in settings[domain]:
            return self[domain][settingname]
        if 'Global' in settings.sections:
            return ConfigObj.get(self['Global'], settingname, default)
        return ConfigObj.get(self, settingname, default)

class InvokeError(Exception):
    _message = None
    def __init__(self, message=""):
        self.set(message)
    def get(self): 
        return self._message
    def set(self, message):
        self._message = message
    message = property(get, set)
        
class ExceptionOptionParser(OptionParser):
    def exit(self, status=0, msg=None):
        raise InvokeError(msg)

def getGlobalSetting(settingname, default=None, domain=None):
    global settings
    if not settings:
        settings = Settings()
        settings.loadDefaults()
    return settings.get(settingname, default,domain)
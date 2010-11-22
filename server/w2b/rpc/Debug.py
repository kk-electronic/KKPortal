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
import sys

from w2b.database import portal
from sqlalchemy import text
import __builtin__
from twisted.python import filepath
from w2b.inotify import INotify,humanReadableMask
from twisted.python import log
import datetime
from collections import deque
from twisted.internet.task import LoopingCall

class TransmitInotify():
    def __init__(self,context):
        self.context = context
    def write(self,watch,path,mask):
        log.msg(path.path,humanReadableMask(mask))
        self.context.addResponse(self.context.makeNotification("INotification",[path.path,humanReadableMask(mask)[0]]))

def reload(context):
    for module in sys.modules.values():
        if module:
            __builtin__.reload(module)

def runQuery(context,query):
    result = portal.engine.execute(text(query)) #@UndefinedVariable
    resultmap={'k':result.keys,'v':[map(str,x.values()) for x in result]}
    result.close()
    return resultmap

def inotify(context,path):
    notifier = INotify()
    notifier.startReading()
    context.addResponse()
    notifier.watch(filepath.FilePath(path),callbacks=[TransmitInotify(context).write])

class Wall():
    mailboxes = []
    lastlines = ['']*10
    def __init__(self):
        pass
    def addMailBox(self,mailbox):
        if mailbox not in self.mailboxes:
            self.mailboxes.append(mailbox)
    def broadcast(self,message):
        self.lastlines.append(message)
        self.lastlines.pop(0)
        for mailbox in self.mailboxes:
            mailbox.addResponse(mailbox.makeNotification("NewWallMessageEvent",[message]))
    
def getWall(context):
    wall = Wall()
    wall.addMailBox(context)
    return wall.lastlines

def postToWall(context,message):
    wall = Wall()
    fullmessage = "<%s> [%s] %s" % (
                      datetime.datetime.now().strftime('%H:%M'),
                      context.user[:context.user.find("@")],
                      message)
    wall.broadcast(fullmessage)

class CpuInfo():
    def __init__(self):
        self.info = {}
        self.usage = {}
        self.history = {}
        self.loaded = set()
        self.lowthreshold = 0.66
        self.highthreshold = 0.80
        self.wall = Wall()
        self.timer = None
        self.callbacks = set()
    def _read(self):
        file=open('/proc/stat')
        cpulines = [line for line in file if line.startswith('cpu')]
        file.close()
        for line in cpulines:
            s = line.split()
            self._updatecpu(s[0],sum(map(float,s[1:4])),int(s[4]))
    def addCallback(self,messagebox):
        self.callbacks.add(messagebox)
    def update(self):
        self._read()
    def _updatecpu(self,cpuname,used,idle):
        if cpuname not in self.history:
            self.history[cpuname] = deque()
        if cpuname in self.info:
            lastused,lastidle = self.info[cpuname]
            useddiff = used - lastused
            idlediff = idle - lastidle
            load=round(useddiff/(useddiff+idlediff),2)
            self._callAll(cpuname,load)
            self.usage[cpuname] = (used-lastused,idle-lastidle)
            self.history[cpuname].append(load)
            while(len(self.history[cpuname]) > 60):
                self.history[cpuname].popleft()
            self._updatewarnings(cpuname,load)
        self.info[cpuname] = (used,idle)
    def _callAll(self,cpuname,load):
        print("TROUT")
        for callback in self.callbacks:
            callback.addResponse(callback.makeNotification("NewCpuUsageDataEvent",[cpuname,load]))
    def _updatewarnings(self,cpuname,load):
        if load > self.highthreshold and cpuname not in self.loaded:
            self.loaded.add(cpuname)
            self.wall.broadcast("%s is loaded" % cpuname)
        if load < self.lowthreshold and cpuname in self.loaded:
            self.loaded.remove(cpuname)
            self.wall.broadcast("%s is no longer loaded" % cpuname)
    def start(self):
        if self.timer:
            return
        self.timer = LoopingCall(self._read)
        self.timer.start(1)
cpuinfo = CpuInfo()
def getCpuHistory(context):
    cpuinfo.start()
    cpuinfo.addCallback(context)
    if 'cpu' in cpuinfo.history:
        return list(cpuinfo.history['cpu'])
    else:
        return []
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
    wall.broadcast(message)
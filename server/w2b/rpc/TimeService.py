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
'''
Created on Feb 15, 2011

@author: Rasmus Carlsen
'''

from sqlalchemy import select,or_
import w2b.database.timereg as db

def get(context, begin, end): 
    columns = [db.timereg.c.id,db.timereg.c.checkin,db.timereg.c.checkout, db.timereg.c.taskId]#@UndefinedVariable
    query = select(columns)
    query = query.where(db.timereg.c.ownerName == context.security.identity)#@UndefinedVariable
    if begin is not None:
        query=query.where(or_(db.timereg.c.checkout == None,begin <= db.timereg.c.checkout))#@UndefinedVariable
    if end is not None:
        query=query.where(or_(db.timereg.c.checkin == None,end >= db.timereg.c.checkin))#@UndefinedVariable
    result = query.execute()
    returnvalues = [dict(x) for x in result]
    result.close()
    return returnvalues

def _isValid(entry):
    if entry['checkout'] is None and entry['checkin'] is None:
        return False
    if entry['checkout'] is None or entry['checkin'] is None:
        return True
    if entry['checkin'] < entry['checkout']:
        return True
    return False
    

def update(context, entry):
    if _isValid(entry):
        entry['ownerName'] = context.security.identity
        query = db.timereg.update()
        query = query.where(db.timereg.c.id == entry['id'])#@UndefinedVariable
        query = query.where(db.timereg.c.ownerName == context.security.identity) #@UndefinedVariable
        query.execute(entry)
    else:
        raise Exception("Entry is invalid")
    
    
def add(context, entry):
    if _isValid(entry):
        entry['id'] = None
        entry['ownerName'] = context.security.identity
        query = db.timereg.insert()
        result = query.execute(entry)
        i = result.last_inserted_ids()[0]
        result.close()
        return i
    else:
        raise Exception("Entry is invalid")

def remove(context, id): 
    query = db.timereg.delete()
    query = query.where(db.timereg.c.id == id) #@UndefinedVariable
    query = query.where(db.timereg.c.ownerName == context.security.identity) #@UndefinedVariable
    query.execute()
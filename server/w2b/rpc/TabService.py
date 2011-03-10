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
Created on March 8, 2011

@author: Rasmus Carlsen
'''

import w2b.database.portal as db
from sqlalchemy import func,select,update

def create(context,tab):
    tab['tab_id'] = None
    tab['ownerName'] = context.security.identity
    query = db.tabs.insert()
    result = query.execute(tab)
    result.close()
    return result.last_inserted_ids()[0]

def update(context, tab):
    tab['ownerName'] = context.security.identity
    query = db.tabs.update()
    query = query.where(db.tabs.c.tab_id == tab['tab_id'])#@UndefinedVariable
    query = query.where(db.tabs.c.ownerName == context.security.identity) #@UndefinedVariable
    query.execute(tab)

def retrieve(context):
    query = db.tabs.select().where(db.tabs.c.ownerName == context.security.identity) #@UndefinedVariable
    result = query.execute()
    returnvalues = [dict(x) for x in result]
    result.close()
    return returnvalues

def delete(context, id):
    query = db.tabs.delete()
    query = query.where(db.tabs.c.tab_id == id) #@UndefinedVariable
    query = query.where(db.tabs.c.ownerName == context.security.identity) #@UndefinedVariable
    query.execute()

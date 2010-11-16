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
Created on Aug 2, 2010

@author: albatros
'''

import w2b.database.portal as db
from sqlalchemy import func,select

def getTabs(context, user):
    return [{'name':'test', 'id':2}, {'name':'test2', 'id':3}]

def getModules(context, tabid):
    # In the database the structure is quite different since lists can't be saved easily 
    # in most SQL databases.
    # So we build up the final answer from the result returned by this query
    # The order clause makes it easy to write an append only algorithm to do this
    # col_nr is assumed to be 0-based, while the order really only affects the ordering
    # inside the columns.
    query = db.modules.select().order_by(db.modules.c.col_nr, db.modules.c.order) #@UndefinedVariable
    result = query.execute()
    return_value = []
    for row in result:
        # This adds columns until we have enough
        while len(return_value) < row.col_nr + 1:
            return_value.append([])
        # We append the module to the last column which due the the ordering is always the right place
        return_value[-1].append({"id":row.module_id, "height":row.height, "type":row.type_id})
    return return_value

def getModule(context, moduleid):
    query = db.modules.select().order_by(db.modules.c.col_nr, db.modules.c.order).where(db.modules.c.module_id == moduleid) #@UndefinedVariable
    result = query.execute()
    row = result.fetchone()
    return {"id":row.module_id, "height":row.height, "type":row.type_id}


def getModuleTypeInfo(context, ids):
    result = db.types.select().where(db.types.c.type_id.in_(ids)).execute().fetchall() #@UndefinedVariable
    resultmap = {'k':result.keys, 'v':[map(str, x.values()) for x in result]}
    result.close()
    return resultmap

def setModules(context, tabid, modules):
    # To insert into the database we need to rename the id to module_id and add col_nr
    # and order.
    # We do this with list comprehension and enumerate.
    # The first for-in is the outer loop which iterates over the columns, and the
    # next iterates over the modules in each column.
    entries = [
     {'col_nr':col_nr,
      'order':order,
      'module_id':module['id'],
      'type_id':module['type'],
      'tab_id':tabid,
      'height':module['height']}
     for col_nr, column in enumerate(modules)
     for order, module in enumerate(column)
    ]
    # To simplify things we simply delete everything on the tab before updating since we
    # do not have a cross database way of SQL MERGE.
    #db.modules.delete(db.modules.c.tab_id == tabid).execute() #@UndefinedVariable
    db.modules.delete().execute()
    # We insert all the values from the previous step
    db.modules.insert().execute(entries)

def getTabInfos(context, user):
    return {
            "k":["name", "tab_id"],
            "v":[
                 ["Tab1", 1],
                 ["Tab2", 2]
                ]
            }

def createModule(context,user,typeid):
    query = db.modules.insert()
    result = query.execute({'col_nr':None,
      'order':None,
      'type_id':typeid,
      'tab_id':None,
      'height':40}
    )
    result.close()
    return result.last_inserted_ids()[0]

def setModulesIdsOnTab(context,tabid,moduleids):
    jsonModuleIds = json.dumps(moduleids)
    query = update(db.tabs).where(db.tabs.c.tab_id == tabid).values(module_ids=jsonModuleIds) #@UndefinedVariable
    result = query.execute()
    result.close()

def addModule(context,tabid,typeid):
    query = select([db.modules.c.col_nr,func.count()+1]).where(db.modules.c.tab_id == 2).group_by(db.modules.c.col_nr).order_by(func.sum(db.modules.c.height)).limit(1) #@UndefinedVariable
    result = query.execute().fetchone()
#select col_nr from modules where tab_id = 2 group by col_nr order by sum(height) limit 1;
#query = db.modules.select(db.modules.c.tab_id = tabid).group_by(db.modules.c.col_nr).order_by(func.sum(db.modules.c.height)) #@UndefinedVariable
    entry = {'col_nr':result[0],
      'order':result[1],
      'type_id':typeid,
      'tab_id':tabid,
      'height':170}
    db.modules.insert().execute(entry)
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
import w2b.settings
import hashlib
from sqlalchemy import MetaData,create_engine,Table,Column,String,Integer,Binary,ForeignKey

#We fetch the database connection string from the config file. (See server/defaulfs.conf)
#Passing the 3rd parameter of __file__ enables overrideing the key for this file alone, 
# allowing different databases to be used for different parts of the project
databasestring = w2b.settings.getGlobalSetting('database',None,__name__)

engine = create_engine(databasestring,echo=True)
metadata = MetaData()

#This part is the data definition for the database
users = Table('users',metadata,
              Column('user_id',Integer,primary_key=True),
              Column('name',String(256)),
              Column('secret',String(64))
              )

types = Table('types',metadata,
              Column('type_id',Integer,primary_key=True),
              Column('code',Integer),
              Column('config',Binary(1023)),
              Column('title',String(255))
              )

tabs = Table('tabs',metadata,
              Column('tab_id',Integer,primary_key=True),
              Column('name',String(255)),
              Column('module_ids',String(255))
              )


modules = Table('modules',metadata,
                Column('module_id',Integer,primary_key=True),
                Column('type_id',ForeignKey(types.c.type_id)),
                Column('tab_id',ForeignKey(tabs.c.tab_id)),
                Column('col_nr',Integer),
                Column('order',Integer),
                Column('height',Integer),
                )

#We bind the engine so we don't have to specify it in each call 
metadata.bind = engine

#For demo purposes we recreate the database and insert a few standard users
#TODO: Make this happen only when the debug flag is set

def recreate():
    metadata.drop_all()
    metadata.create_all()

def createDebugData():
    demousersid = range(1,4);
    demousernames = ['demo','demo2','jes']
    demopasswd = ['demo','password','1234']
    #Using generators we create a lists objects with a name and a secret attribute
    demousers = [{'id':i,'name':u,'secret':hashlib.sha256(u+':'+p).hexdigest()} for i,u,p in zip(demousersid,demousernames,demopasswd)]
    
    #Here we insert all the users
    users.insert().execute(demousers)
    tabs.insert().execute([{'tab_id':i,'name':'Tab ' + str(i),'module_ids':'[[1],[1,1],[1,1,1]]'} for i in range(1,4)])
    #types.insert().execute([{'type_id':0,'code':439329280,'title':'Hello World'}])
    #import random
    #modules.insert().execute([{'col_nr':2,'order':i,'module_id':i,'type_id':0,'tab_id':0,'height':random.randrange(24,80)} for i in range(8)])
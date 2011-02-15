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
from sqlalchemy import MetaData,create_engine,Table,Column,String,Integer

#We fetch the database connection string from the config file. (See server/defaulfs.conf)
#Passing the 3rd parameter of __name__ enables overrideing the key for this file alone, 
# allowing different databases to be used for different parts of the project
databasestring = w2b.settings.getGlobalSetting('database',None,__name__)

engine = create_engine(databasestring)
metadata = MetaData()

#This part is the data definition for the database
timereg = Table('timeregs',metadata,
              Column('ownerName',String(256)),
              Column('id',Integer, primary_key=True),
              Column('checkin',Integer),
              Column('checkout',Integer),
              Column('taskId',Integer)
              )

#We bind the engine so we don't have to specify it in each call 
metadata.bind = engine

#For demo purposes we recreate the database and insert a few standard users
#TODO: Make this happen only when the debug flag is set

def recreate():
    metadata.drop_all()
    metadata.create_all()

def createDebugData():
    demouser = 'jes@example.com'
    timereg.insert().execute({'ownerName':demouser,'checkin':1297604100,'checkout':1297611300,'taskId':1})
    timereg.insert().execute({'ownerName':demouser,'checkin':1297611420,'checkout':1297615020})
    timereg.insert().execute({'ownerName':demouser,'checkin':1297672620,'taskId':1})
    #import random
    #modules.insert().execute([{'col_nr':2,'order':i,'module_id':i,'type_id':0,'tab_id':0,'height':random.randrange(24,80)} for i in range(8)])
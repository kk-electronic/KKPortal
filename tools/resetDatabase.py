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

import sys,os

sys.path.append(os.path.join(os.path.dirname(os.path.dirname(__file__)),'server'))

import w2b.database

# All public python files in w2b.database without the suffix 
submodules = [filename[:-3] for filename in os.listdir(w2b.database.__path__[0]) if filename.endswith('.py') and not filename.startswith('_')]

# Import them
imports = __import__(w2b.database.__name__,fromlist=submodules)

for modulename in submodules:
    module = getattr(imports, modulename)
    print("reset %s" % modulename)
    try:
        module.recreate()
    except e:
        print("Resetting failed",e)
    try:
        module.createDebugData()
    except:
        pass

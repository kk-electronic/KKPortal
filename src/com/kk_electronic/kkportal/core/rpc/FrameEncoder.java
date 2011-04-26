/*
 * Copyright 2010 kk-electronic a/s. 
 * 
 * This file is part of KKPortal.
 *
 * KKPortal is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KKPortal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KKPortal.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kk_electronic.kkportal.core.rpc;

import java.util.List;

import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToSerialize;

public interface FrameEncoder<V> {

	public abstract <T> void encode(T object,StringBuilder response) throws UnableToSerialize;
	public abstract V decode(String string) throws UnableToDeserialize;
	public abstract <T> T validate(V object,T resultType,Class<?>[] subtypes) throws UnableToDeserialize;
	public abstract <T> T validate(V object,T resultType,List<Class<?>> subtypes) throws UnableToDeserialize;
}
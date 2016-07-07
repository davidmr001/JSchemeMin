/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.jschememin.type;
import com.github.chungkwong.jschememin.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class NativeProcedure extends ScmObject implements Evaluable{
	@Override
	public String toExternalRepresentation(){
		return this.getClass().getCanonicalName();
	}
	@Override
	public boolean isSelfevaluating(){
		return false;
	}
	@Override
	public void call(Environment env,Continuation cont,Object pointer,ScmObject param){
		cont.ret(call(param));
	}
	public abstract ScmObject call(ScmObject param);
}

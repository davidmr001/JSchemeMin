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
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class ScmPort extends ScmObject{
	public static ScmPort CURRENT_INPUT=new ScmTextualInputPort(new InputStreamReader(System.in));
	public static ScmPort CURRENT_OUTPUT=new ScmTextualOutputPort(new OutputStreamWriter(System.out));
	public static ScmPort CURRENT_ERROR=new ScmTextualOutputPort(new OutputStreamWriter(System.err));
	private boolean closed=false;
	@Override
	public boolean isSelfevaluating(){
		return false;
	}
	public ScmPort close()throws IOException{
		closed=true;
		return this;
	}
	public boolean isClosed(){
		return closed;
	}
}

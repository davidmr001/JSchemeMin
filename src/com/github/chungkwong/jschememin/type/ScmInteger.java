/*
 * Copyright (C) 2016 Chan Chung Kwong
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 */
package com.github.chungkwong.jschememin.type;
import java.math.*;
import java.util.*;
public final class ScmInteger extends ScmReal{
	public static final ScmInteger ZERO=new ScmInteger(BigInteger.ZERO);
	public static final ScmInteger ONE=new ScmInteger(BigInteger.ONE);
	private final BigInteger value;
	public ScmInteger(BigInteger value){
		this.value=value;
	}
	public static ScmInteger valueOf(long val){
		return new ScmInteger(BigInteger.valueOf(val));
	}
	@Override
	public ScmInteger negate(){
		return new ScmInteger(value.negate());
	}
	public ScmInteger add(ScmInteger num){
		return new ScmInteger(value.add(num.value));
	}
	public ScmInteger subtract(ScmInteger num){
		return new ScmInteger(value.subtract(num.value));
	}
	public ScmInteger multiply(ScmInteger num){
		return new ScmInteger(value.multiply(num.value));
	}
	public ScmInteger divide(ScmInteger num){
		return new ScmInteger(value.divide(num.value));
	}
	public ScmInteger remainder(ScmInteger num){
		return new ScmInteger(value.mod(num.value));
	}
	public ScmInteger gcd(ScmInteger num){
		return new ScmInteger(value.gcd(num.value));
	}
	public ScmInteger lcm(ScmInteger num){
		return new ScmInteger(value.multiply(num.value).divide(value.gcd(num.value)));
	}
	public int compareTo(ScmInteger num){
		return value.compareTo(num.value);
	}
	@Override
	public int compareTo(ScmReal o){
		if(o instanceof ScmInteger){
			return compareTo((ScmInteger)o);
		}else if(o instanceof ScmRational){
			return toScmRational().compareTo((ScmRational)o);
		}else if(o instanceof ScmFloatingPointNumber){
			return toInExact().compareTo((ScmFloatingPointNumber)o);
		}else{
			assert o instanceof ScmFloatingPointNumber.SpecialValue;

		}
	}
	public BigInteger getValue(){
		return value;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof ScmInteger&&((ScmInteger)obj).value.equals(value);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=67*hash+Objects.hashCode(this.value);
		return hash;
	}
	@Override
	public boolean isExact(){
		return true;
	}
	@Override
	public String toExternalRepresentation(){
		return value.toString();
	}
	@Override
	public boolean needPlusSign(){
		return value.signum()>=0;
	}
	@Override
	public int signum(){
		return value.signum();
	}
	@Override
	public boolean isInteger(){
		return true;
	}
	@Override
	public ScmInteger toScmInteger(){
		return this;
	}
	@Override
	public boolean isRational(){
		return true;
	}
	@Override
	public ScmRational toScmRational(){
		return new ScmRational(this,ONE);
	}
	@Override
	public ScmReal add(ScmReal num){
		if(num instanceof ScmInteger){
			return add((ScmInteger)num);
		}else if(num instanceof ScmRational){
			return toScmRational().add(((ScmRational)num));
		}else{
			assert num instanceof ScmFloatingPointNumber;
			return toInExact().add(num);
		}
	}
	@Override
	public ScmReal subtract(ScmReal num){
		if(num instanceof ScmInteger){
			return subtract((ScmInteger)num);
		}else if(num instanceof ScmRational){
			return toScmRational().subtract(((ScmRational)num));
		}else{
			assert num instanceof ScmFloatingPointNumber;
			return toInExact().subtract(num);
		}
	}
	@Override
	public ScmReal multiply(ScmReal num){
		if(num instanceof ScmInteger){
			return multiply((ScmInteger)num);
		}else if(num instanceof ScmRational){
			return toScmRational().multiply(((ScmRational)num));
		}else{
			assert num instanceof ScmFloatingPointNumber;
			return toInExact().multiply(num);
		}
	}
	@Override
	public ScmReal divide(ScmReal num){
		if(num instanceof ScmInteger){
			return new ScmRational(this,(ScmInteger)num);
		}else if(num instanceof ScmRational){
			return toScmRational().divide(((ScmRational)num));
		}else{
			assert num instanceof ScmFloatingPointNumber;
			return toInExact().divide(((ScmFloatingPointNumber)num));
		}
	}
	@Override
	public ScmReal sin(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public ScmReal cos(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public ScmReal sqrt(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public ScmReal toExact(){
		return this;
	}
	@Override
	public ScmFloatingPointNumber toInExact(){
		return new ScmFloatingPointNumber(new BigDecimal(value));
	}
}

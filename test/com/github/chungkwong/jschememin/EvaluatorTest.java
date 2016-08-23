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
package com.github.chungkwong.jschememin;

import com.github.chungkwong.jschememin.type.*;
import java.io.*;
import org.junit.*;

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EvaluatorTest{
	void check(String expr,ScmObject... obj){
		Evaluator evaluator=new Evaluator(true);
		Assert.assertArrayEquals(new Parser(expr).getRemainingDatums().stream().map((d)->evaluator.eval(d)).toArray(),obj);
	}
	void checkLast(String expr,ScmObject obj){
		Evaluator evaluator=new Evaluator(true);
		Object[] result=new Parser(expr).getRemainingDatums().stream().map((d)->evaluator.eval(d)).toArray();
		Assert.assertEquals(result[result.length-1],obj);
	}
	public void assertExpressionValue(String expr,String result){
		ScmObject gotval=new Evaluator(true).eval(new Parser(expr).nextDatum());
		ScmObject expectval=new Evaluator(true).eval(new Parser(result).nextDatum());
		Assert.assertEquals(gotval,expectval);
	}
	void expectException(String expr){
		try{
			new Evaluator(true).eval(new Parser(expr).nextDatum());
			Assert.assertTrue(false);
		}catch(Exception ex){

		}
	}
	public void assertStandardOutput(String expr,String result){
		StringWriter out=new StringWriter();
		ScmPort.CURRENT_OUTPUT=new ScmTextualOutputPort(out);
		try{
			new Evaluator(true).eval(new Parser(expr).nextDatum());
		}catch(RuntimeException ex){

		}
		Assert.assertEquals(out.toString(),result);
	}
	@Test
	public void testIf(){
		check("(if #f 'a 'b)",new ScmSymbol("b"));
		check("(if #t 'a 'b)",new ScmSymbol("a"));
		check("(if '() 'a 'b)",new ScmSymbol("a"));
		check("(if #f 'a)",new ScmSymbol("unspecified"));
		check("(if #t 'a)",new ScmSymbol("a"));
		check("((if #f + *) 3 4)",new ScmInteger(12));
		assertExpressionValue("(if (= 3 4) (+ 1 2) (* 5 6))","30");
	}
	@Test
	public void testLambda(){
		checkLast("(set! f (lambda () (set! x '()))) x",Environment.UNBOUNDED);
		checkLast("(set! x 3) (set! f (lambda () (set! x '()))) (f) x",ScmNil.NIL);
		checkLast("((lambda (x) (cons x x)) 'a)",new ScmPair(new ScmSymbol("a"),new ScmSymbol("a")));
		checkLast("((lambda x x) 3 4 5 6)",ScmList.toList(new ScmInteger(3),new ScmInteger(4),new ScmInteger(5),new ScmInteger(6)));
		checkLast("((lambda (x y . z) z) 3 4 5 6)",ScmList.toList(new ScmInteger(5),new ScmInteger(6)));
		assertExpressionValue("(let ((reverse-subtract (lambda (x y) (- y x)))) (reverse-subtract 7 10))","3");
		assertExpressionValue("(let ((add4 (let ((x 4)) (lambda (y) (+ x y))))) (add4 6))","10");
		assertExpressionValue("(let ((x 0)) (and (= x 0) (begin (set! x 5) (+ x 1))))","6");
	}
	@Test
	public void testInclude(){
		check("(include \"/home/kwong/NetBeansProjects/JSchemeMin/test/com/github/chungkwong/jschememin/to_include.scm\")",new ScmInteger(12));
	}
	@Test
	public void testSet(){
		check("(let ((x 1)) (set! x (+ x 2)) x)",new ScmInteger(3));
	}
	@Test
	public void testImport(){
		checkLast("(import (scheme cxr)) (cadddr '(1 2 3 4 5))",new ScmInteger(4));
	}
	@Test
	public void testQuote(){
		checkLast("(quote a)",new ScmSymbol("a"));
		checkLast("(quote #(a b c))",ScmVector.toVector(new ScmSymbol("a"),new ScmSymbol("b"),new ScmSymbol("c")));
		checkLast("(quote (+ 1 2))",ScmList.toList(new ScmSymbol("+"),new ScmInteger(1),new ScmInteger(2)));
		checkLast("'a",new ScmSymbol("a"));
		checkLast("'#(a b c)",ScmVector.toVector(new ScmSymbol("a"),new ScmSymbol("b"),new ScmSymbol("c")));
		checkLast("'()",ScmNil.NIL);
		checkLast("'(+ 1 2)",ScmList.toList(new ScmSymbol("+"),new ScmInteger(1),new ScmInteger(2)));
		checkLast("'(quote a)",ScmList.toList(new ScmSymbol("quote"),new ScmSymbol("a")));
		checkLast("''a",ScmList.toList(new ScmSymbol("quote"),new ScmSymbol("a")));
		checkLast("'145932",new ScmInteger(145932));
		checkLast("145932",new ScmInteger(145932));
		checkLast("'\"abc\"",new ScmString("abc"));
		checkLast("\"abc\"",new ScmString("abc"));
		checkLast("'#\\space",new ScmCharacter(' '));
		checkLast("#\\space",new ScmCharacter(' '));
		checkLast("'#(a 10)",ScmVector.toVector(new ScmSymbol("a"),new ScmInteger(10)));
		checkLast("#(a 10)",ScmVector.toVector(new ScmSymbol("a"),new ScmInteger(10)));
		checkLast("'#u8(64 65)",new ScmByteVector(new byte[]{64,65}));
		checkLast("#u8(64 65)",new ScmByteVector(new byte[]{64,65}));
		checkLast("'#t",ScmBoolean.TRUE);
		checkLast("#t",ScmBoolean.TRUE);
	}
	@Test
	public void testConditionals(){
		assertExpressionValue("(cond ((> 3 2) 'greater) ((< 3 2) 'less))","'greater");
		assertExpressionValue("(cond ((> 3 3) 'greater) ((< 3 3) 'less) (else 'equal))","'equal");
		assertExpressionValue("(cond ((quote (b 2)) => cadr) (else #f))","2");
		/*assertExpressionValue("(case (* 2 3) ((2 3 5 7) 'prime) ((1 4 6 8 9) 'composite))","'composite");
		assertExpressionValue("(case (car '(c d)) ((a) 'a) ((b) 'b))","'unspecified");
		assertExpressionValue("(case (car '(c d)) ((a e i o u) 'vowel) ((w y) 'semivowel) (else => (lambda (x) x)))","'c");*/
		assertExpressionValue("(and)","#t");
		assertExpressionValue("(and 5)","5");
		assertExpressionValue("(and (= 2 2) (> 2 1))","#t");
		assertExpressionValue("(and (= 2 2) (< 2 1))","#f");
		assertExpressionValue("(and 1 2 'c '(f g))","'(f g)");
		assertExpressionValue("(or)","#f");
		assertExpressionValue("(or 4)","4");
		assertExpressionValue("(or (= 2 2) (= 2 1))","#t");
		assertExpressionValue("(or (= 2 2) (> 2 1))","#t");
		assertExpressionValue("(or (< 2 1) (= 2 2))","#t");
		assertExpressionValue("(or #f #f #f)","#f");
		assertExpressionValue("(or #f #f #f 1 #f)","1");
		assertStandardOutput("(when (= 1 1) (write-string \"hello\") (flush-output-port))","hello");
		assertStandardOutput("(when (= 1 2) (write-string \"hello\") (flush-output-port))","");
		assertStandardOutput("(unless (= 1 1) (write-string \"hello\") (flush-output-port))","");
		assertStandardOutput("(unless (= 1 2) (write-string \"hello\") (flush-output-port))","hello");
	}
	@Test
	public void testBinding(){
		assertExpressionValue("(let ((x 2) (y 3)) (* x y))","6");
		assertExpressionValue("(let ((x 2) (y 3)) (let ((x 7) (z (+ x y))) (* z x)))","35");
		assertExpressionValue("(let ((x 2) (y 3)) (let* ((x 7) (z (+ x y))) (* z x)))","70");
		assertExpressionValue("(letrec ((even? (lambda (n) (if (zero? n) #t (odd? (- n 1))))) (odd? (lambda (n) (if (zero? n) #f (even? (- n 1)))))) (even? 88))","#t");
		assertExpressionValue("(letrec ((even? (lambda (n) (if (zero? n) #t (odd? (- n 1))))) (odd? (lambda (n) (if (zero? n) #f (even? (- n 1)))))) (odd? 88))","#f");
		assertExpressionValue("(letrec ((even? (lambda (n) (if (zero? n) #t (odd? (- n 1))))) (odd? (lambda (n) (if (zero? n) #f (even? (- n 1)))))) (even? 87))","#f");
		assertExpressionValue("(letrec* ((p (lambda (x) (+ 1 (q (- x 1))))) (q (lambda (y) (if (zero? y) 0 (+ 1 (p (- y 1)))))) (x (p 5)) (y x)) y)","5");
		assertExpressionValue("(let-values (((root rem) (exact-integer-sqrt 32))) (* root rem))","35");
		assertExpressionValue("(let ((a 'a) (b 'b) (x 'x) (y 'y)) (let*-values (((a b) (values x y)) ((x y) (values a b))) (list a b x y)))","'(x y x y)");
	}
	@Test
	public void testIteration(){
		assertExpressionValue("(let loop ((numbers '(3 -2 1 6 -5)) (nonneg '()) (neg '())) "
				+ "(cond ((null? numbers) (list nonneg neg)) "
				+ "((>= (car numbers) 0) (loop (cdr numbers) (cons (car numbers) nonneg) neg)) "
				+ "((< (car numbers) 0) (loop (cdr numbers) nonneg (cons (car numbers) neg)))))","'((6 1 3) (-5 -2))");
		assertExpressionValue("(do ((vec (make-vector 5)) (i 0 (+ i 1))) ((= i 5) vec) (vector-set! vec i i))"
				,"#(0 1 2 3 4)");
		assertExpressionValue("(let ((x '(1 3 5 7 9))) (do ((x x (cdr x)) (sum 0 (+ sum (car x)))) ((null? x) sum)))"
				,"25");
	}
	@Test
	public void testLazy(){
		assertExpressionValue("(begin (import (scheme lazy)) (force (delay (+ 1 2))))","3");
		assertExpressionValue("(begin (import (scheme lazy)) (force (delay (+ 1 2))))","3");
	}

}

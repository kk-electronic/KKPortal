package com.kk_electronic.kkportal.core.util;

public class Pair<A, B> {
	private A a;
	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	private B b;

	public Pair(A a,B b) {
		this.a = a;
		this.b = b;
		
	}
}

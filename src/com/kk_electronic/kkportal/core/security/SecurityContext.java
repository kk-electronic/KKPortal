package com.kk_electronic.kkportal.core.security;

import java.math.BigInteger;

import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.util.Stats;

public class SecurityContext {
	private BigInteger N = new BigInteger("115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3",16);
	private BigInteger g = new BigInteger("2",16);
	private BigInteger k = new BigInteger("3",16);
	private final Hasher hasher;
	private String identity;

	private String password = null;
	private BigInteger x = null;
	private String method;
	private Challange challange;
	private BigInteger A;
	private BigInteger a;
	private BigInteger S;
	private final Stats stats;
	
	@Inject
	public SecurityContext(Hasher hasher,Stats stats) {
		this.hasher = hasher;
		this.stats = stats;
	}
	
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
		updateX();
	}
	public void setPassword(String password) {
		this.password = password;
		updateX();
	}
	private void updateX(){
		if(identity != null && password != null){
			String input = identity + ":" + password;
			x = new BigInteger(hasher.hash(input),16);
			password = null;
		}
	}

	private String padleft(String s, int n){
		StringBuilder sb = new StringBuilder();
		for(int i=n-s.length();i>0;i--){
			sb.append('0');
		}
		sb.append(s);
		return sb.toString();
	}

	public BigInteger calc_verifier() {
		return g.modPow(x, N);
	}

	public Answer calc_answer(Challange challange) {
		stats.sendStats(SecurityContext.class, "calc_answer", "begin");
		this.challange = challange;
		this.A = make_A();
		calc_Secret();
		Answer answer = new Answer(A,hasher.hash(pad(A,256)+pad(challange.B,256)+pad(S,256)));
		stats.sendStats(SecurityContext.class, "calc_answer", "end");
		return answer;
	}

	private String pad(BigInteger number, int bits) {
		return padleft(number.toString(16), bits/4);
	}

	private void calc_Secret() {
		BigInteger u = calc_U();
		S = challange.B.subtract(g.modPow(x, N).multiply(k)).mod(N).modPow(a.add(x.multiply(u)), N);
	}
	
	private BigInteger calc_U() {
		return new BigInteger("693d7c408310c4362c5b2e13345d1840175f4976",16);
	}

	private BigInteger make_A() {
		this.a = new BigInteger("e338e421ad9ca2b3c0221d329315d6fef2919b5ef9fbf2a33093476380735430",16);
		return g.modPow(a, N);
	}

	public String getSecret() {
		return pad(S,256);
	}
}

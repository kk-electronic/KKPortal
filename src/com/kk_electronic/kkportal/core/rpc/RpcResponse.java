package com.kk_electronic.kkportal.core.rpc;

public class RpcResponse<R> implements RpcEnvelope {

	private final int id;
	private final R result;

	public RpcResponse(int id, R result) {
		this.id = id;
		this.result = result;
	}

	public int getId() {
		return id;
	}

	public R getResult() {
		return result;
	}
}

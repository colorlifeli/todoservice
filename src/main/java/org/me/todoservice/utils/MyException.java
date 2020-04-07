package org.me.todoservice.utils;

public class MyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String msg;
	// 异常编码
	private Codes code;

	public MyException(String msg, Object... params) {
		this(Codes.EXCEPTION, msg, params);
	}

	public MyException(Codes code, String msg, Object... params) {
		super(String.format(msg, params));
		this.code = code;
		this.msg = String.format(msg, params);
	}

	public String getMsg() {
		return msg;
	}

	public String getCode() {
		return code.getCode();
	}
}

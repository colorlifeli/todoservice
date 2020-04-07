package org.me.todoservice.utils;

/**
 * @Description:
 * @Date: 2019/6/15 Author: deng.zhirong
 */
public enum Codes {

	SUCCESS("200"), ERROR("400"), EXCEPTION("500");

	Codes(String code) {
		this.code = code;
	}

	private String code;

	public String getCode() {
		return code;
	}
}

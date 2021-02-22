package org.me.todoservice.utils;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public static final int BUSY = -100;
	public static final String SUCCESS_TEXT = "Success";
	public static final String FAIL_TEXT = "Fail";
	public static final String BUSY_TEXT = "Busy";
	private int status;
	private String statusText;
	private T data;

	public ApiResponse() {
		status = SUCCESS;
		statusText = SUCCESS_TEXT;
	}

	public ApiResponse(int status, String statusText, T data) {
		this.status = status;
		this.statusText = statusText;
		this.data = data;
	}

	public ApiResponse(T data) {
		if ((data instanceof Exception)) {
			status = FAIL;
			statusText = ((Exception) data).getLocalizedMessage();
		} else {
			status = SUCCESS;
			statusText = SUCCESS_TEXT;
			this.data = data;
		}
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		if (status == SUCCESS) {
			statusText = SUCCESS_TEXT;
		} else if (status == BUSY) {
			statusText = BUSY_TEXT;
		} else {
			statusText = "";
		}
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public T getData() {
		return (T) data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> ApiResponse<T> ok() {
		return new ApiResponse<T>();
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<T>(data);
	}

	public static <T> ApiResponse<T> fail() {
		return new ApiResponse<T>(FAIL, FAIL_TEXT, null);
	}

	public static <T> ApiResponse<T> fail(String msg) {
		return new ApiResponse<T>(FAIL, msg, null);
	}
}

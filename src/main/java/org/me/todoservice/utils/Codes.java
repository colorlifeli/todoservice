package org.me.todoservice.utils;

public enum Codes {

    SUCCESS("200"), ERROR("400"), EXCEPTION("500"), AUTHFAIL("401");

    Codes(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}

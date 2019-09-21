package com.jenga.yujun.util.status_code;


import com.jenga.yujun.util.SimpleEnumModel;

public enum FileStatusCode implements SimpleEnumModel {

    FILE_UPLOAD_SUCCESS(201), FILE_UPLOAD_FAIL(202);

    private String key;
    private int code;


    FileStatusCode(int code) {
        this.key = this.name();
        this.code = code;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getCode() {
        return code;
    }
}

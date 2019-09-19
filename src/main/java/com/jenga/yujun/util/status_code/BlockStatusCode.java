package com.jenga.yujun.util.status_code;

import hi.im.jenga.util.SimpleEnumModel;

public enum BlockStatusCode implements SimpleEnumModel {

    BLOCK_STACK_SUCCESS(401),
    BLOCK_STACK_FAIL(402),
    BLOCK_MOD_SUCCESS(403),
    BLOCK_MOD_FAIL(404),
    BLOCK_DEL_SUCCESS(405),
    BLOCK_DEL_FAIL(406),
    LIKE_SUCCESS(407),
    LIKE_FAIL(408),
    LIKE_EXISTS(409),
    LIKE_NOT_EXISTS(410),
    FOLLOW_SUCCESS(411),
    FOLLOW_FAIL(412),
    UNFOLLOW_SUCCESS(412),
    UNFOLLOW_FAIL(413),
    FOLLOW(414),
    NOT_FOLLOW(415),
    FOLLOW_SAME_AUTH(416);


    private int code;
    private String key;

    BlockStatusCode(int code) {
        this.code = code;
        this.key = this.name();
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

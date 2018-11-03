package com.pikndel.modal;

/**
 * Created by ashimkanshal on 26/8/16.
 */
public class BaseResponse {
    /**
     * code : 200
     * message : Success
     */

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

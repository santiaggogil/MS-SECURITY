package com.sgm.ms_security.Anserws;

public class VerifyResponse extends ResponseBase {
    private String token;


    public VerifyResponse(int code, String message, String token) {
        super(code, message);
        this.token=token;
    }

    public static VerifyResponse create (int code, String message, String token) {
        return new VerifyResponse(code, message, token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

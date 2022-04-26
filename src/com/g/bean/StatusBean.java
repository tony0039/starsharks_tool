package com.g.bean;

import java.math.BigInteger;

public class StatusBean {
    private Integer code;
    private String message;
    private BigInteger nonce=BigInteger.valueOf(0);

    private Object data;
    public StatusBean(Integer code,String message,Object data){
        this.code=code;
        this.message=message;
        this.data=data;
    }
    public Integer getCode() {
        return code;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

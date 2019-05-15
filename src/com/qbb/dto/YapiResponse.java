package com.qbb.dto;

import java.io.Serializable;

/**
 * yapi 返回结果
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 12:08 PM
 */
public class YapiResponse implements Serializable {
    /**
     * 状态码
     */
    private Integer errcode;
    /**
     * 状态信息
     */
    private String errmsg;
    /**
     * 返回结果
     */
    private Object data;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public YapiResponse() {
    }

    public YapiResponse(Object data) {
        this.errcode=0;
        this.errmsg="success";
        this.data = data;
    }

    public YapiResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }
}

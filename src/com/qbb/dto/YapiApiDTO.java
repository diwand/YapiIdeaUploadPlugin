package com.qbb.dto;

import java.io.Serializable;
import java.util.List;

/**
 * yapi dto
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/11 3:16 PM
 */
public class YapiApiDTO implements Serializable{
    /**
     * 路径
     */
    private String path;
    /**
     * 请求参数
     */
    private List<YapiQueryDTO> params;
    /**
     * 头信息
     */
    private List header;
    /**
     * title
     */
    private String title;
    /**
     * 响应
     */
    private String response;
    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 请求方法
     */
    private String method="POST";

    /**
     * 描述
     */
    private String desc;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<YapiQueryDTO> getParams() {
        return params;
    }

    public void setParams(List<YapiQueryDTO> params) {
        this.params = params;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List getHeader() {
        return header;
    }

    public void setHeader(List header) {
        this.header = header;
    }

    public YapiApiDTO() {
    }


}

package com.qbb.dto;

import java.io.Serializable;

/**
 * yapi dubbo 对象
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 5:36 PM
 */
public class YapiDubboDTO implements Serializable {
    /**
     * 路径
     */
    private String path;
    /**
     * 请求参数
     */
    private String params;
    /**
     * title
     */
    private String title;
    /**
     * 响应
     */
    private String response;
    /**
     * 描述
     */
    private String desc;
    /**
     * 菜单
     */
    private String menu;
    /**
     * 状态
     */
    private String status;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public YapiDubboDTO() {
    }
}

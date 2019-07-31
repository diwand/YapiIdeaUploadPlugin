package com.qbb.dto;

import java.io.Serializable;
import java.util.List;

/**
 * yapi 保存请求参数
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 11:43 AM
 */
public class YapiSaveParam implements Serializable{
    /**
     * 项目 token  唯一标识
     */
    private String token;

    /**
     * 请求参数
     */
    private List req_query;
    /**
     * header
     */
    private List req_headers;
    /**
     * 请求参数 form 类型
     */
    private List req_body_form;
    /**
     * 标题
     */
    private String title;
    /**
     * 分类id
     */
    private String  catid;
    /**
     * 请求数据类型   raw,form,json
     */
    private String req_body_type="json";
    /**
     * 请求数据body
     */
    private String req_body_other;
    /**
     * 请求参数body 是否为json_schema
     */
    private boolean req_body_is_json_schema;
    /**
     * 路径
     */
    private String path;
    /**
     * 状态 undone,默认done
     */
    private String status="undone";
    /**
     * 返回参数类型  json
     */
    private String res_body_type="json";

    /**
     * 返回参数
     */
    private String res_body;

    /**
     * 返回参数是否为json_schema
     */
    private boolean res_body_is_json_schema=true;

    /**
     * 创建的用户名
     */
    private Integer edit_uid=11;
    /**
     * 用户名称
     */
    private String username;

    /**
     * 邮件开关
     */
    private boolean switch_notice;

    private String message=" ";
    /**
     * 文档描述
     */
    private String desc="<h3>请补充描述</h3>";

    /**
     * 请求方式
     */
    private String method="POST";
    /**
     * 请求参数
     */
    private List req_params;


    private String  id;
    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * yapi 地址
     */
    private String yapiUrl;
    /**
     * 菜单名称
     */
    private String menu;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List getReq_query() {
        return req_query;
    }

    public void setReq_query(List req_query) {
        this.req_query = req_query;
    }

    public List getReq_headers() {
        return req_headers;
    }

    public void setReq_headers(List req_headers) {
        this.req_headers = req_headers;
    }

    public List getReq_body_form() {
        return req_body_form;
    }

    public void setReq_body_form(List req_body_form) {
        this.req_body_form = req_body_form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRes_body_type() {
        return res_body_type;
    }

    public void setRes_body_type(String res_body_type) {
        this.res_body_type = res_body_type;
    }

    public String getRes_body() {
        return res_body;
    }

    public void setRes_body(String res_body) {
        this.res_body = res_body;
    }

    public boolean isSwitch_notice() {
        return switch_notice;
    }

    public void setSwitch_notice(boolean switch_notice) {
        this.switch_notice = switch_notice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List getReq_params() {
        return req_params;
    }

    public void setReq_params(List req_params) {
        this.req_params = req_params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReq_body_type() {
        return req_body_type;
    }

    public void setReq_body_type(String req_body_type) {
        this.req_body_type = req_body_type;
    }

    public String getReq_body_other() {
        return req_body_other;
    }

    public void setReq_body_other(String req_body_other) {
        this.req_body_other = req_body_other;
    }

    public boolean isReq_body_is_json_schema() {
        return req_body_is_json_schema;
    }

    public void setReq_body_is_json_schema(boolean req_body_is_json_schema) {
        this.req_body_is_json_schema = req_body_is_json_schema;
    }

    public boolean isRes_body_is_json_schema() {
        return res_body_is_json_schema;
    }

    public void setRes_body_is_json_schema(boolean res_body_is_json_schema) {
        this.res_body_is_json_schema = res_body_is_json_schema;
    }

    public Integer getEdit_uid() {
        return edit_uid;
    }

    public void setEdit_uid(Integer edit_uid) {
        this.edit_uid = edit_uid;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getYapiUrl() {
        return yapiUrl;
    }

    public void setYapiUrl(String yapiUrl) {
        this.yapiUrl = yapiUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public YapiSaveParam() {
    }

    public YapiSaveParam(String token, String title, String path,String req_body_other ,String res_body,Integer projectId,String yapiUrl,String desc) {
        this.token = token;
        this.title = title;
        this.path = path;
        this.res_body = res_body;
        this.req_body_other=req_body_other;
        this.projectId=projectId;
        this.yapiUrl=yapiUrl;
        this.desc=desc;
    }


    public YapiSaveParam(String token, String title, String path,List req_query,String req_body_other ,String res_body,Integer projectId,String yapiUrl,boolean req_body_is_json_schema,String method,String desc,List header) {
        this.token = token;
        this.title = title;
        this.path = path;
        this.req_query=req_query;
        this.res_body = res_body;
        this.req_body_other=req_body_other;
        this.projectId=projectId;
        this.yapiUrl=yapiUrl;
        this.req_body_is_json_schema=req_body_is_json_schema;
        this.method=method;
        this.desc=desc;
        this.req_headers=header;
    }




}

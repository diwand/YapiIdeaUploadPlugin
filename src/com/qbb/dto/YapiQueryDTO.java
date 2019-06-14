package com.qbb.dto;

import java.io.Serializable;

/**
 * query
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/11 5:05 PM
 */
public class YapiQueryDTO implements Serializable{
    /**
     * 是否必填
     */
    private String required="1";


    private String _id;
    /**
     * 描述
     */
    private String desc;
    /**
     * 示例
     */
    private String example;
    /**
     * 参数名字
     */
    private String  name;


    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public YapiQueryDTO() {
    }

    public YapiQueryDTO( String desc, String example, String name) {
        this.desc = desc;
        this.example = example;
        this.name = name;
    }
}

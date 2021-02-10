package com.qbb.dto;

import com.qbb.constant.ProjectTypeConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * Yapi项目配置.
 */
public class YapiProjectConfig {

    private String yapiUrl;

    private String projectToken;

    private String projectId;

    private String projectType;

    private String returnClass;

    private String attachUpload;

    /**
     * 配置是否有效.
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(projectToken)
                && StringUtils.isNotEmpty(projectId)
                && StringUtils.isNotEmpty(yapiUrl)
                && StringUtils.isNotEmpty(projectType);
    }

    public boolean isDubboProject() {
        return ProjectTypeConstant.dubbo.equals(projectType);
    }

    public boolean isApiProject() {
        return ProjectTypeConstant.api.equals(projectType);
    }

    /**
     * 计算上传后访问地址.
     */
    public String resolveCatUrl(String catId) {
        return yapiUrl + "/project/" + projectId + "/interface/api/cat_" + catId;
    }

    /**
     * 配置合并.
     */
    public static void mergeToFirst(YapiProjectConfig a, YapiProjectConfig b) {
        if (b != null) {
            if (StringUtils.isNotEmpty(b.yapiUrl)) {
                a.yapiUrl = b.yapiUrl;
            }
            if (StringUtils.isNotEmpty(b.projectToken)) {
                a.projectToken = b.projectToken;
            }
            if (StringUtils.isNotEmpty(b.projectId)) {
                a.projectId = b.projectId;
            }
            if (StringUtils.isNotEmpty(b.projectType)) {
                a.projectType = b.projectType;
            }
            if (StringUtils.isNotEmpty(b.returnClass)) {
                a.returnClass = b.returnClass;
            }
            if (StringUtils.isNotEmpty(b.attachUpload)) {
                a.attachUpload = b.attachUpload;
            }
        }

    }

    //------------------ generated ------------------//

    public String getYapiUrl() {
        return yapiUrl;
    }

    public void setYapiUrl(String yapiUrl) {
        this.yapiUrl = yapiUrl;
    }

    public String getProjectToken() {
        return projectToken;
    }

    public void setProjectToken(String projectToken) {
        this.projectToken = projectToken;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(String returnClass) {
        this.returnClass = returnClass;
    }

    public String getAttachUpload() {
        return attachUpload;
    }

    public void setAttachUpload(String attachUpload) {
        this.attachUpload = attachUpload;
    }

    @Override
    public String toString() {
        return "YapiProjectConfig{" +
                "yapiUrl='" + yapiUrl + '\'' +
                ", projectToken='" + projectToken + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectType='" + projectType + '\'' +
                ", returnClass='" + returnClass + '\'' +
                ", attachUpload='" + attachUpload + '\'' +
                '}';
    }
}

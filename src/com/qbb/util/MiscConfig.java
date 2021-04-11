package com.qbb.util;

import com.google.common.base.Strings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;

import java.nio.charset.StandardCharsets;

/**
 * 解析配置
 *
 * @Author: chong.zhang
 * @Date: 2021-04-09 19:53:56
 */

public class MiscConfig {

    private String projectToken = null;
    private String projectId = null;
    private String yapiUrl = null;
    private String projectType = null;
    private String returnClass = null;
    private String attachUpload = null;

    public MiscConfig(AnActionEvent e) throws Exception {
        parse(e);
    }

    private void parse(AnActionEvent e) throws Exception {
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String projectConfig = new String(editor.getProject().getProjectFile().contentsToByteArray(),
                StandardCharsets.UTF_8);

        String[] modules = projectConfig.split("moduleList\">");
        if (modules.length > 1) {
            String[] moduleList = modules[1].split("</")[0].split(",");
            PsiFile psiFile = (PsiFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
            assert psiFile != null;
            String virtualFile = psiFile.getVirtualFile().getPath();
            for (String s : moduleList) {
                if (virtualFile.contains(s)) {
                    projectToken = projectConfig.split(s + "\\.projectToken\">")[1].split("</")[0];
                    projectId = projectConfig.split(s + "\\.projectId\">")[1].split("</")[0];
                    yapiUrl = projectConfig.split(s + "\\.yapiUrl\">")[1].split("</")[0];
                    projectType = projectConfig.split(s + "\\.projectType\">")[1].split("</")[0];
                    if (projectConfig.split(s + "\\.returnClass\">").length > 1) {
                        returnClass = projectConfig.split(s + "\\.returnClass\">")[1].split("</")[0];
                    }
                    String[] attachs = projectConfig.split(s + "\\.attachUploadUrl\">");
                    if (attachs.length > 1) {
                        attachUpload = attachs[1].split("</")[0];
                    }
                    break;
                }
            }
        } else {
            projectToken = projectConfig.split("projectToken\">")[1].split("</")[0];
            projectId = projectConfig.split("projectId\">")[1].split("</")[0];
            yapiUrl = projectConfig.split("yapiUrl\">")[1].split("</")[0];
            projectType = projectConfig.split("projectType\">")[1].split("</")[0];
            if (projectConfig.split("returnClass\">").length > 1) {
                returnClass = projectConfig.split("returnClass\">")[1].split("</")[0];
            }

            String[] attachs = projectConfig.split("attachUploadUrl\">");
            if (attachs.length > 1) {
                attachUpload = attachs[1].split("</")[0];
            }
        }
        // 配置校验
        if (Strings.isNullOrEmpty(projectToken) ||
                Strings.isNullOrEmpty(projectId) ||
                Strings.isNullOrEmpty(yapiUrl) ||
                Strings.isNullOrEmpty(projectType)) {
            throw new Exception("misc文件配置有误");
        }
    }

    public String getProjectToken() {
        return projectToken;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getYapiUrl() {
        return yapiUrl;
    }

    public String getProjectType() {
        return projectType;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public String getAttachUpload() {
        return attachUpload;
    }
}

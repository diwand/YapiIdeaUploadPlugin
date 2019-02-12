package com.qbb.interaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xml.config.ConfigFileSearcher;
import com.qbb.build.BuildJsonForDubbo;
import com.qbb.build.BuildJsonForYapi;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiDubboDTO;
import com.qbb.dto.YapiResponse;
import com.qbb.dto.YapiSaveParam;
import com.qbb.upload.UploadYapi;
import com.yourkit.util.Strings;

import java.io.IOException;


public class UploadToYapi extends AnAction {

    private static NotificationGroup notificationGroup;

    Gson gson= new GsonBuilder().setPrettyPrinting().create();

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        Project project = editor.getProject();
        String projectToken=null;
        String projectId=null;
        String yapiUrl=null;
        String projectType=null;
        // 获取配置
        try {
            String projectConfig=new String(editor.getProject().getProjectFile().contentsToByteArray(),"utf-8");
            projectToken=projectConfig.split("projectToken\">")[1].split("</")[0];
            projectId=projectConfig.split("projectId\">")[1].split("</")[0];
            yapiUrl=projectConfig.split("yapiUrl\">")[1].split("</")[0];
            projectType=projectConfig.split("projectType\">")[1].split("</")[0];
        } catch (Exception e2){
            Notification error = notificationGroup.createNotification("get config error:"+e2.getMessage(), NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return;
        }
        // 配置校验
        if(Strings.isNullOrEmpty(projectToken) || Strings.isNullOrEmpty(projectId) || Strings.isNullOrEmpty(yapiUrl) || Strings.isNullOrEmpty(projectType)){
            Notification error = notificationGroup.createNotification("please check config,[projectToken,projectId,yapiUrl,projectType]", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return;
        }
        if(ProjectTypeConstant.dubbo.equals(projectType)){
            BuildJsonForDubbo buildJsonForDubbo=new BuildJsonForDubbo();
            YapiDubboDTO yapiDubboDTO=buildJsonForDubbo.actionPerformed(e);
            YapiSaveParam yapiSaveParam=new YapiSaveParam(projectToken,yapiDubboDTO.getTitle(),yapiDubboDTO.getPath(),yapiDubboDTO.getParams(),yapiDubboDTO.getResponse(),Integer.valueOf(projectId),yapiUrl);
            UploadYapi uploadYapi=new UploadYapi();
            try {
                YapiResponse yapiResponse=uploadYapi.uploadSave(yapiSaveParam);
                if(yapiResponse.getErrcode()!=0){
                    Notification error = notificationGroup.createNotification("sorry ,upload api error cause:"+yapiResponse.getErrmsg(), NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                }else{
                    Notification error = notificationGroup.createNotification("success ,info: "+gson.toJson(yapiResponse.getData()), NotificationType.INFORMATION);
                    Notifications.Bus.notify(error, project);
                }
            } catch (Exception e1) {
                Notification error = notificationGroup.createNotification("sorry ,upload api error cause:"+e1.getMessage(), NotificationType.ERROR);
                Notifications.Bus.notify(error, project);
            }
        }else if(ProjectTypeConstant.api.equals(projectType)){
            BuildJsonForYapi buildJsonForYapi=new BuildJsonForYapi();
            YapiApiDTO yapiApiDTO=buildJsonForYapi.actionPerformed(e);
            YapiSaveParam yapiSaveParam=new YapiSaveParam(projectToken,yapiApiDTO.getTitle(),yapiApiDTO.getPath(),yapiApiDTO.getParams(),yapiApiDTO.getRequestBody(),yapiApiDTO.getResponse(),Integer.valueOf(projectId),yapiUrl,true);
            UploadYapi uploadYapi=new UploadYapi();
            try {
                YapiResponse yapiResponse=uploadYapi.uploadSave(yapiSaveParam);
                if(yapiResponse.getErrcode()!=0){
                    Notification error = notificationGroup.createNotification("sorry ,upload api error cause:"+yapiResponse.getErrmsg(), NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                }else{
                    Notification error = notificationGroup.createNotification("success ,info: "+gson.toJson(yapiResponse.getData()), NotificationType.INFORMATION);
                    Notifications.Bus.notify(error, project);
                }
            } catch (Exception e1) {
                Notification error = notificationGroup.createNotification("sorry ,upload api error cause:"+e1.getMessage(), NotificationType.ERROR);
                Notifications.Bus.notify(error, project);
            }
        }

    }
}

package com.qbb.interaction;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.qbb.build.BuildJsonForDubbo;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiDubboDTO;
import com.qbb.dto.YapiResponse;
import com.qbb.dto.YapiSaveParam;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.UploadUtil;
import com.qbb.util.ClipboardUtil;
import com.qbb.util.MiscConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 入口
 * @author: chengsheng@qbb6.com
 * @date: 2019/5/15
 */
public class UploadToYapi extends AnAction {

    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup",
                NotificationDisplayType.BALLOON, true);
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        Project project = editor.getProject();

        // 获取配置
        MiscConfig miscConfig = null;
        try {
            miscConfig = new MiscConfig(e);
        } catch (Exception ex) {
            Notification error = notificationGroup.createNotification("获取配置失败：" + ex.getMessage(),
                    NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }

        // 参数类组装
        List<YapiSaveParam> list = new ArrayList<>();
        String attachUpload = null;
        try {
            assert miscConfig != null;
            if (ProjectTypeConstant.dubbo.equals(miscConfig.getProjectType())) {
                list = convertDubboParam(miscConfig, e);
            } else if (ProjectTypeConstant.api.equals(miscConfig.getProjectType())) {
                list = convertApiParam(miscConfig, e);
                attachUpload = miscConfig.getAttachUpload();
            }
        }catch (Exception ex){
            Notification error = notificationGroup.createNotification("组装参数失败：" + ex.getMessage(),
                    NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }

        // 执行上传
        for (YapiSaveParam item : list) {
            try {
                YapiResponse yapiResponse = new UploadUtil().uploadSave(item, attachUpload, project.getBasePath());
                if (yapiResponse.getErrcode() != 0) {
                    // 失败处理
                    Notification error = notificationGroup.createNotification("上传失败:"
                            + yapiResponse.getErrmsg(), NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                } else {
                    // 成功处理
                    String url = miscConfig.getYapiUrl()
                            + "/project/"
                            + miscConfig.getProjectId()
                            + "/interface/api/cat_"
                            + yapiResponse.getCatId();
                    Notification error = notificationGroup.createNotification("上传成功 ,url:" + url,
                            NotificationType.INFORMATION);
                    ClipboardUtil.setClipboard(url);
                    Notifications.Bus.notify(error, project);
                }
            } catch (Exception e1) {
                // 异常处理
                Notification error = notificationGroup.createNotification("上传失败:" + e1,
                        NotificationType.ERROR);
                Notifications.Bus.notify(error, project);
            }
        }
    }

    /**
     * 处理Dubbo类型
     * @param miscConfig
     * @param e
     * @return
     */
    private List<YapiSaveParam> convertDubboParam(MiscConfig miscConfig, AnActionEvent e){
        // 获得dubbo需上传的接口列表 参数对象
        ArrayList<YapiDubboDTO> list = new BuildJsonForDubbo().actionPerformedList(e);
        List<YapiSaveParam> params = new ArrayList<>();

        list.forEach(item -> {
            YapiSaveParam yapiSaveParam = new YapiSaveParam(miscConfig.getProjectToken(),
                    item.getTitle(),
                    item.getPath(),
                    item.getParams(),
                    item.getResponse(),
                    Integer.valueOf(miscConfig.getProjectId()),
                    miscConfig.getYapiUrl(),
                    item.getDesc());
            yapiSaveParam.setStatus(item.getStatus());
            if (!Strings.isNullOrEmpty(item.getMenu())) {
                yapiSaveParam.setMenu(item.getMenu());
            } else {
                yapiSaveParam.setMenu(YapiConstant.menu);
            }
            params.add(yapiSaveParam);
        });
        return params;
    }

    /**
     * 转化为YapiSaveParam类型
     * @param miscConfig
     * @param e
     * @return
     */
    private List<YapiSaveParam> convertApiParam(MiscConfig miscConfig, AnActionEvent e) throws Exception {
        ApiBuildContext context = new ApiBuildContext(e, null, null);
        List<YapiApiDTO> list = context.getYapiApiDTOS();

        List<YapiSaveParam> params = new ArrayList<>();
        list.forEach(item ->{
            YapiSaveParam yapiSaveParam = new YapiSaveParam(
                    miscConfig.getProjectToken(),
                    item.getTitle(),
                    item.getPath(),
                    item.getParams(),
                    item.getRequestBody(),
                    item.getResponse(),
                    Integer.valueOf(miscConfig.getProjectId()),
                    miscConfig.getYapiUrl(),
                    true,
                    item.getMethod(),
                    item.getDesc(),
                    item.getHeader());
            yapiSaveParam.setReq_body_form(item.getReq_body_form());
            yapiSaveParam.setReq_body_type(item.getReq_body_type());
            yapiSaveParam.setReq_params(item.getReq_params());
            yapiSaveParam.setStatus(item.getStatus());
            if (!Strings.isNullOrEmpty(item.getMenu())) {
                yapiSaveParam.setMenu(item.getMenu());
            } else {
                yapiSaveParam.setMenu(YapiConstant.menu);
            }
            params.add(yapiSaveParam);
        });
        return params;
    }

}

package com.qbb.build;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.qbb.constant.JavaConstant;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiHeaderDTO;
import com.qbb.dto.YapiQueryDTO;
import com.qbb.upload.UploadYapi;
import com.qbb.util.DesUtil;
import com.qbb.util.FileToZipUtil;
import com.qbb.util.FileUnZipUtil;
import com.qbb.util.PsiAnnotationSearchUtil;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: 为了yapi 创建的
 * @author: chengsheng@qbb6.com
 * @date: 2018/10/27
 */ 
public class BuildJsonForYapi{
    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }

    static Set<String> filePaths=new CopyOnWriteArraySet<>();

    /**
     * 批量生成 接口数据
     * @param e
     * @return
     */
    public ArrayList<YapiApiDTO> actionPerformedList(AnActionEvent e,String attachUpload){
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = (PsiFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        String selectedText=e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        Project project = editor.getProject();
        if(Strings.isNullOrEmpty(selectedText)){
            Notification error = notificationGroup.createNotification("please select method or class", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return null;
        }
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        ArrayList<YapiApiDTO> yapiApiDTOS=new ArrayList<>();
        if(selectedText.equals(selectedClass.getName())){
            PsiMethod[] psiMethods=selectedClass.getMethods();
            for(PsiMethod psiMethodTarget:psiMethods) {
                //去除私有方法
                if(!psiMethodTarget.getModifierList().hasModifierProperty("private")) {
                    yapiApiDTOS.add(actionPerformed(selectedClass, psiMethodTarget, project, psiFile,attachUpload));
                }
            }
        }else{
            PsiMethod[] psiMethods =selectedClass.getAllMethods();
            //寻找目标Method
            PsiMethod psiMethodTarget=null;
            for(PsiMethod psiMethod:psiMethods){
                if(psiMethod.getName().equals(selectedText)){
                    psiMethodTarget=psiMethod;
                    break;
                }
            }
            if(Objects.nonNull(psiMethodTarget)) {
                yapiApiDTOS.add(actionPerformed(selectedClass, psiMethodTarget, project, psiFile,attachUpload));
            }else{
                Notification error = notificationGroup.createNotification("can not find method:"+selectedText, NotificationType.ERROR);
                Notifications.Bus.notify(error, project);
                return null;
            }
        }
        return yapiApiDTOS;
    }


    public static YapiApiDTO actionPerformed(PsiClass selectedClass,PsiMethod psiMethodTarget,Project project,PsiFile psiFile,String attachUpload) {
        YapiApiDTO yapiApiDTO=new YapiApiDTO();
        // 获得路径
        StringBuilder path=new StringBuilder();

        // 获取类上面的RequestMapping 中的value
        PsiAnnotation psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(selectedClass, SpringMVCConstant.RequestMapping);
        if(psiAnnotation!=null){
            PsiNameValuePair[] psiNameValuePairs= psiAnnotation.getParameterList().getAttributes();
            if(psiNameValuePairs.length>0){
                path.append(psiNameValuePairs[0].getLiteralValue());
            }
        }

        PsiAnnotation psiAnnotationMethod= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.RequestMapping);
        if(psiAnnotationMethod!=null){
            PsiNameValuePair[] psiNameValuePairs= psiAnnotationMethod.getParameterList().getAttributes();
            if(psiNameValuePairs!=null && psiNameValuePairs.length>0){
                for(PsiNameValuePair psiNameValuePair:psiNameValuePairs){
                    //获得方法上的路径
                    if(Objects.isNull(psiNameValuePair.getName())||"value".equals(psiNameValuePair.getName())){
                        PsiReference psiReference= psiNameValuePair.getValue().getReference();
                        if(psiReference==null){
                            path.append(psiNameValuePair.getLiteralValue());
                        }else{
                            String[] results=psiReference.resolve().getText().split("=");
                            path.append(results[results.length-1].split(";")[0].replace("\"","").trim());
                            yapiApiDTO.setTitle(DesUtil.getUrlReFerenceRDesc(psiReference.resolve().getText()));
                            yapiApiDTO.setMenu(DesUtil.getMenu(psiReference.resolve().getText()));
                            yapiApiDTO.setDesc("<pre><code>  "+psiReference.resolve().getText()+" </code></pre> <hr>");
                        }
                        yapiApiDTO.setPath(path.toString());
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("GET")){
                        // 判断是否为Get 请求
                        yapiApiDTO.setMethod("GET");
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("POST")){
                        // 判断是否为Post 请求
                        yapiApiDTO.setMethod("POST");
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("PUT")){
                        // 判断是否为Post 请求
                        yapiApiDTO.setMethod("PUT");
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("DELETE")){
                        // 判断是否为Post 请求
                        yapiApiDTO.setMethod("DELETE");
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("PATCH")){
                        // 判断是否为Post 请求
                        yapiApiDTO.setMethod("PATCH");
                    }
                }
            }
        }else{
            PsiAnnotation psiAnnotationMethodSemple= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.GetMapping);
            if(psiAnnotationMethodSemple!=null){
                yapiApiDTO.setMethod("GET");
            }else{
                psiAnnotationMethodSemple= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.PostMapping);
                if(psiAnnotationMethodSemple!=null){
                    yapiApiDTO.setMethod("POST");
                }else{
                    psiAnnotationMethodSemple= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.PutMapping);
                    if(psiAnnotationMethodSemple!=null){
                        yapiApiDTO.setMethod("PUT");
                    }else{
                        psiAnnotationMethodSemple= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.DeleteMapping);
                        if(psiAnnotationMethodSemple!=null){
                            yapiApiDTO.setMethod("DELETE");
                        }else{
                            psiAnnotationMethodSemple= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.PatchMapping);
                            if(psiAnnotationMethodSemple!=null){
                                yapiApiDTO.setMethod("PATCH");
                            }
                        }
                    }
                }
            }
            if(psiAnnotationMethodSemple!=null) {
                PsiNameValuePair[] psiNameValuePairs = psiAnnotationMethodSemple.getParameterList().getAttributes();
                for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                    //获得方法上的路径
                    if (Objects.isNull(psiNameValuePair.getName()) || psiNameValuePair.getName().equals("value")) {
                        PsiReference psiReference = psiNameValuePair.getValue().getReference();
                        if (psiReference == null) {
                            path.append(psiNameValuePair.getLiteralValue());
                        } else {
                            String[] results=psiReference.resolve().getText().split("=");
                            path.append(results[results.length-1].split(";")[0].replace("\"", "").trim());
                            yapiApiDTO.setTitle(DesUtil.getUrlReFerenceRDesc(psiReference.resolve().getText()));
                            yapiApiDTO.setMenu(DesUtil.getMenu(psiReference.resolve().getText()));
                            yapiApiDTO.setDesc("<pre><code>  "+psiReference.resolve().getText()+" </code></pre> <hr>");
                        }
                        yapiApiDTO.setPath(path.toString().trim());
                    }
                }
            }
        }
        yapiApiDTO.setDesc(Objects.nonNull(yapiApiDTO.getDesc())?yapiApiDTO.getDesc():" <pre><code>  "+psiMethodTarget.getText().replace(Objects.nonNull(psiMethodTarget.getBody())?psiMethodTarget.getBody().getText():"","")+" </code></pre>");
        try {
            // 生成响应参数
            yapiApiDTO.setResponse(getResponse(project,psiMethodTarget.getReturnType()));
            Set<String> codeSet = new HashSet<>();
            if(!Strings.isNullOrEmpty(attachUpload)) {
                // 打包响应参数文件
                if (filePaths.size() > 0) {
                    changeFilePath(project);
                    FileToZipUtil.toZip(filePaths, project.getBasePath() + "/response.zip", true);
                    filePaths.clear();
                    codeSet.add(project.getBasePath() + "/response.zip");
                }
                // 清空路径
                // 生成请求参数
            }else{
                filePaths.clear();
            }
            getRequest(project, yapiApiDTO, psiMethodTarget);
            if(!Strings.isNullOrEmpty(attachUpload)){
                if (filePaths.size() > 0) {
                    changeFilePath(project);
                    FileToZipUtil.toZip(filePaths, project.getBasePath() + "/request.zip", true);
                    filePaths.clear();
                    codeSet.add(project.getBasePath() + "/request.zip");
                }
                // 打包请求参数文件
                if (codeSet.size() > 0) {
                    FileToZipUtil.toZip(codeSet, project.getBasePath() + "/code.zip", true);
                    if (!Strings.isNullOrEmpty(attachUpload)) {
                        String fileUrl = new UploadYapi().uploadFile(attachUpload, project.getBasePath() + "/code.zip");
                        if (!Strings.isNullOrEmpty(fileUrl)) {
                            yapiApiDTO.setDesc("java类:<a href='" + fileUrl + "'>下载地址</a><br/>" + yapiApiDTO.getDesc());
                        }
                    }
                }
            }else{
                filePaths.clear();
            }
            // 清空路径
            if(Strings.isNullOrEmpty(yapiApiDTO.getTitle())) {
                yapiApiDTO.setTitle(DesUtil.getDescription(psiMethodTarget));
                if(Objects.nonNull(psiMethodTarget.getDocComment())) {
                    yapiApiDTO.setMenu(DesUtil.getMenu(psiMethodTarget.getDocComment().getText()));
                }
            }
            return yapiApiDTO;
        } catch (Exception ex) {
            Notification error = notificationGroup.createNotification("Convert to JSON failed.", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }
        return null;
    }



    /**
     * @description: 获得请求参数
     * @param: [project, yapiApiDTO, psiMethodTarget]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/19
     */ 
    public static void getRequest(Project project,YapiApiDTO yapiApiDTO,PsiMethod psiMethodTarget) throws JSONException {
        PsiParameter[] psiParameters= psiMethodTarget.getParameterList().getParameters();
        if(psiParameters.length>0) {
            ArrayList list=new ArrayList<YapiQueryDTO>();
            List<YapiHeaderDTO> yapiHeaderDTOList=new ArrayList<>();
            for(PsiParameter psiParameter:psiParameters){
                PsiAnnotation psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestBody);
                if(psiAnnotation!=null){
                    yapiApiDTO.setRequestBody(getResponse(project,psiParameter.getType()));
                }else{
                    psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestParam);
                    YapiHeaderDTO yapiHeaderDTO=null;
                    if(psiAnnotation==null){
                        psiAnnotation=PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestAttribute);
                        if(psiAnnotation==null){
                            psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestHeader);
                            yapiHeaderDTO=new YapiHeaderDTO();
                        }
                    }
                    if(psiAnnotation!=null) {
                        PsiNameValuePair[] psiNameValuePairs = psiAnnotation.getParameterList().getAttributes();
                        YapiQueryDTO yapiQueryDTO = new YapiQueryDTO();

                        if(psiNameValuePairs.length>0) {
                            for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                                if ("name".equals(psiNameValuePair.getName())) {
                                    if(yapiHeaderDTO!=null){
                                        yapiHeaderDTO.setName(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }else {
                                        yapiQueryDTO.setName(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }
                                } else if ("value".equals(psiNameValuePair.getName())) {
                                    if(yapiHeaderDTO!=null){
                                        yapiHeaderDTO.setName(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }else {
                                        yapiQueryDTO.setName(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }
                                } else if ("required".equals(psiNameValuePair.getName())) {
                                    yapiQueryDTO.setRequired(psiNameValuePair.getValue().getText().replace("\"", "").replace("false", "0").replace("true", "1"));
                                } else if ("defaultValue".equals(psiNameValuePair.getName())) {
                                    if(yapiHeaderDTO!=null) {
                                        yapiHeaderDTO.setExample(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }else{
                                        yapiQueryDTO.setExample(psiNameValuePair.getValue().getText().replace("\"", ""));
                                    }
                                } else {
                                    if(yapiHeaderDTO!=null) {
                                        yapiHeaderDTO.setName(psiNameValuePair.getLiteralValue());
                                        yapiHeaderDTO.setDesc(psiParameter.getType().getPresentableText());
                                    }else{
                                        yapiQueryDTO.setName(psiNameValuePair.getLiteralValue());
                                        yapiQueryDTO.setDesc(psiParameter.getType().getPresentableText());
                                    }
                                    if(Objects.nonNull(NormalTypes.normalTypes.containsKey(psiParameter.getType().getPresentableText()))){
                                        if(yapiHeaderDTO!=null) {
                                            yapiHeaderDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                                        }else{
                                            yapiQueryDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                                        }
                                    }else{
                                        yapiApiDTO.setRequestBody(getResponse(project,psiParameter.getType()));
                                    }

                                }
                            }
                        }else{
                            if(yapiHeaderDTO!=null) {
                                yapiHeaderDTO.setName(psiParameter.getName());
                                yapiHeaderDTO.setDesc(psiParameter.getType().getPresentableText());
                            }else{
                                yapiQueryDTO.setName(psiParameter.getName());
                                yapiQueryDTO.setDesc(psiParameter.getType().getPresentableText());
                            }
                            if(Objects.nonNull(NormalTypes.normalTypes.containsKey(psiParameter.getType().getPresentableText()))){
                                if(yapiHeaderDTO!=null){
                                    yapiHeaderDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                                }else {
                                    yapiQueryDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                                }
                            }else{
                                yapiApiDTO.setRequestBody(getResponse(project,psiParameter.getType()));
                            }
                        }
                        if(yapiHeaderDTO!=null){
                            if(Strings.isNullOrEmpty(yapiHeaderDTO.getDesc())){
                                yapiHeaderDTO.setDesc(psiParameter.getType().getPresentableText());
                            }
                            if(Strings.isNullOrEmpty(yapiHeaderDTO.getExample()) && Objects.nonNull(NormalTypes.normalTypes.containsKey(psiParameter.getType().getPresentableText())) ){
                                yapiHeaderDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                            }
                            yapiHeaderDTOList.add(yapiHeaderDTO);
                        }else {
                            if(Strings.isNullOrEmpty(yapiQueryDTO.getDesc())){
                                yapiQueryDTO.setDesc(psiParameter.getType().getPresentableText());
                            }
                            if(Strings.isNullOrEmpty(yapiQueryDTO.getExample()) && Objects.nonNull(NormalTypes.normalTypes.containsKey(psiParameter.getType().getPresentableText())) ){
                                yapiQueryDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                            }
                            list.add(yapiQueryDTO);
                        }
                    }else{
                        // 支持实体对象接收
                        yapiApiDTO.setReq_body_type("form");
                        yapiApiDTO.setReq_body_form(getRequestForm(project,psiParameter.getType()));
                    }
                }
            }
            yapiApiDTO.setParams(list);
            yapiApiDTO.setHeader(yapiHeaderDTOList);
        }
    }
    /**
     * @description: 获得表单提交数据对象
     * @param: [requestClass]
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/17
     */
    public static List<Map<String,String>> getRequestForm(Project project,PsiType psiType){
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(), GlobalSearchScope.allScope(project));
        List<Map<String,String>> requestForm=new ArrayList<>();
        for (PsiField field : psiClass.getAllFields()) {
            if(field.getModifierList().hasModifierProperty("final")){
                continue;
            }
            Map<String,String> map=new HashMap<>();
            map.put("name",field.getName());
            map.put("type","text");
            String remark= DesUtil.getFiledDesc(field.getDocComment());
            remark=DesUtil.getLinkRemark(remark,project,field);
            map.put("desc",remark);
            if(Objects.nonNull(field.getType().getPresentableText())){
                Object obj=NormalTypes.normalTypes.get(field.getType().getPresentableText());
                if(Objects.nonNull(obj)){
                    map.put("example",NormalTypes.normalTypes.get(field.getType().getPresentableText()).toString());
                }
            }
            requestForm.add(map);
        }

        return requestForm;
    }

    /**
     * @description: 获得响应参数
     * @param: [project, psiType]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/19
     */ 
    public static String getResponse(Project project,PsiType psiType) throws JSONException{
        return getPojoJson(project, psiType);
    }


    public static String getPojoJson(Project project,PsiType psiType) throws JSONException{
        if(psiType instanceof PsiPrimitiveType){
            //如果是基本类型
            KV kvClass=KV.create();
            kvClass.set(psiType.getCanonicalText(),NormalTypes.normalTypes.get(psiType.getPresentableText()));
        }else if(NormalTypes.isNormalType(psiType.getPresentableText())){
            //如果是包装类型
            KV kvClass=KV.create();
            kvClass.set(psiType.getCanonicalText(),NormalTypes.normalTypes.get(psiType.getPresentableText()));
        }else if(psiType.getPresentableText().startsWith("List")){
            String[] types=psiType.getCanonicalText().split("<");
            KV listKv=new KV();
            if(types.length>1){
                String childPackage=types[1].split(">")[0];
                if(NormalTypes.noramlTypesPackages.keySet().contains(childPackage)){
                    listKv.set("type",NormalTypes.noramlTypesPackages.get(childPackage));
                }else if(NormalTypes.collectTypesPackages.containsKey(childPackage)){
                    listKv.set("type",NormalTypes.collectTypesPackages.get(childPackage));
                }else {
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                    List<String> requiredList=new ArrayList<>();
                    KV kvObject = getFields(psiClassChild, project,null,null,requiredList);
                    listKv.set("type","object");
                    addFilePaths(filePaths,psiClassChild);
                    if(Objects.nonNull(psiClassChild.getSuperClass())&&!psiClassChild.getSuperClass().getName().toString().equals("Object") ){
                        addFilePaths(filePaths,psiClassChild.getSuperClass());
                    }
                    listKv.set("properties", kvObject);
                    listKv.set("required",requiredList);
                }
            }
            KV result = new KV();
            result.set("type", "array");
            result.set("title", psiType.getPresentableText());
            result.set("description", psiType.getPresentableText());
            result.set("items", listKv);
            String json = result.toPrettyJson();
            return json;
        }else if(psiType.getPresentableText().startsWith("Set")){
            String[] types=psiType.getCanonicalText().split("<");
            KV listKv=new KV();
            if(types.length>1){
                String childPackage=types[1].split(">")[0];
                if(NormalTypes.noramlTypesPackages.keySet().contains(childPackage)){
                    listKv.set("type",NormalTypes.noramlTypesPackages.get(childPackage));
                }else if(NormalTypes.collectTypesPackages.containsKey(childPackage)){
                    listKv.set("type",NormalTypes.collectTypesPackages.get(childPackage));
                }else {
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                    List<String> requiredList=new ArrayList<>();
                    KV kvObject = getFields(psiClassChild, project,null,null,requiredList);
                    listKv.set("type","object");
                    addFilePaths(filePaths,psiClassChild);
                    if(Objects.nonNull(psiClassChild.getSuperClass())&& !psiClassChild.getSuperClass().getName().toString().equals("Object")){
                        addFilePaths(filePaths,psiClassChild.getSuperClass());
                    }
                    listKv.set("properties", kvObject);
                    listKv.set("required",requiredList);
                }
            }
            KV result = new KV();
            result.set("type", "array");
            result.set("title", psiType.getPresentableText());
            result.set("description", psiType.getPresentableText());
            result.set("items", listKv);
            String json = result.toPrettyJson();
            return json;
        }else if(psiType.getPresentableText().startsWith("Map")){
            HashMap hashMapChild=new HashMap();
            String[] types=psiType.getCanonicalText().split("<");
            if(types.length>1){
                hashMapChild.put("paramMap",psiType.getPresentableText());
            }
            KV kvClass=KV.create();
            kvClass.set(types[0],hashMapChild);
            KV result = new KV();
            result.set("type", "object");
            result.set("title", psiType.getPresentableText());
            result.set("description", psiType.getPresentableText());
            result.set("properties", hashMapChild);
            String json = result.toPrettyJson();
            return json;
        }else if(NormalTypes.collectTypes.containsKey(psiType.getPresentableText())){
            //如果是集合类型
            KV kvClass=KV.create();
            kvClass.set(psiType.getCanonicalText(),NormalTypes.collectTypes.get(psiType.getPresentableText()));
        }else{
            String[] types=psiType.getCanonicalText().split("<");
            if(types.length>1) {
                PsiClass psiClassChild =  JavaPsiFacade.getInstance(project).findClass(types[0], GlobalSearchScope.allScope(project));
                KV result = new KV();
                List<String> requiredList=new ArrayList<>();
                KV kvObject = getFields(psiClassChild, project,types,1,requiredList);
                result.set("type", "object");
                result.set("title", psiType.getPresentableText());
                result.set("required",requiredList);
                addFilePaths(filePaths,psiClassChild);
                if(Objects.nonNull(psiClassChild.getSuperClass())&&!psiClassChild.getSuperClass().getName().toString().equals("Object")){
                    addFilePaths(filePaths,psiClassChild.getSuperClass());
                }
                result.set("description", (psiType.getPresentableText()+" :"+psiClassChild.getName()).trim());
                result.set("properties", kvObject);
                String json = result.toPrettyJson();
                return json;
            }else{
                PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(), GlobalSearchScope.allScope(project));
                KV result = new KV();
                List<String> requiredList=new ArrayList<>();
                KV kvObject = getFields(psiClassChild, project,null,null,requiredList);
                addFilePaths(filePaths,psiClassChild);
                if(Objects.nonNull(psiClassChild.getSuperClass()) && !psiClassChild.getSuperClass().getName().toString().equals("Object")){
                    addFilePaths(filePaths,psiClassChild.getSuperClass());
                }
                result.set("type", "object");
                result.set("required",requiredList);
                result.set("title", psiType.getPresentableText());
                result.set("description", (psiType.getPresentableText()+" :"+psiClassChild.getName()).trim());
                result.set("properties", kvObject);
                String json = result.toPrettyJson();
                return json;
            }
        }
        return null;
    }

    /**
     * @description: 获得属性列表
     * @param: [psiClass, project, childType, index]
     * @return: com.qbb.build.KV
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public static KV getFields(PsiClass psiClass,Project project,String[] childType,Integer index,List<String> requiredList) {
        KV kv = KV.create();
        if (psiClass != null) {
            if(Objects.nonNull(psiClass.getSuperClass()) && Objects.nonNull(NormalTypes.collectTypes.get(psiClass.getSuperClass().getName()))){
                for (PsiField field : psiClass.getFields()) {
                    if(Objects.nonNull(PsiAnnotationSearchUtil.findAnnotation(field, JavaConstant.NotNull))){
                        requiredList.add(field.getName());
                    }
                    getField(field,project,kv,childType,index,psiClass.getName());
                }
            }else{
                if("T".equals(psiClass.getName())&&childType!=null && childType.length>index){
                    String child = childType[index].split(">")[0];
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(child, GlobalSearchScope.allScope(project));
                    return getFields(psiClassChild,project,childType,index+1,requiredList);
                }else {
                    for (PsiField field : psiClass.getAllFields()) {
                        if(Objects.nonNull(PsiAnnotationSearchUtil.findAnnotation(field, JavaConstant.NotNull))){
                            requiredList.add(field.getName());
                        }
                        getField(field, project, kv, childType, index, psiClass.getName());
                    }
                }
            }
        }
        return kv;
    }

    /**
     * @description: 获得单个属性
     * @param: [field, project, kv, childType, index, pName]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public static  void getField(PsiField field,Project project,KV kv,String[] childType,Integer index,String pName){
        if(field.getModifierList().hasModifierProperty("final")){
            return;
        }
        PsiType type = field.getType();
        String name = field.getName();
        String remark ="";
        if(field.getDocComment()!=null) {
            remark = DesUtil.getFiledDesc(field.getDocComment());
            //获得link 备注
            remark = DesUtil.getLinkRemark(remark, project, field);
        }
        // 如果是基本类型
        if (type instanceof PsiPrimitiveType) {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("type",type.getPresentableText());
            if(!Strings.isNullOrEmpty(remark)) {
                jsonObject.addProperty("description", remark);
            }
            kv.set(name, jsonObject);
        } else {
            //reference Type
            String fieldTypeName = type.getPresentableText();
            //normal Type
            if (NormalTypes.isNormalType(fieldTypeName)) {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("type",fieldTypeName);
                if(!Strings.isNullOrEmpty(remark)) {
                    jsonObject.addProperty("description", remark);
                }
                kv.set(name, jsonObject);
            }else if(!(type instanceof PsiArrayType)&&((PsiClassReferenceType) type).resolve().isEnum()) {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("type","enum");
                if(!Strings.isNullOrEmpty(remark)) {
                    jsonObject.addProperty("description", remark);
                }
                kv.set(name, jsonObject);
            }else if(fieldTypeName.equals("T")) {
                if(childType!=null) {
                    String child = childType[index].split(">")[0];
                    if (child.contains("List") || child.contains("Set") || child.contains("HashSet")) {
                        PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childType[index + 1].split(">")[0], GlobalSearchScope.allScope(project));
                        getCollect(kv, psiClassChild.getName(), remark, psiClassChild, project, name,pName,childType,index+1);
                    } else {
                        //class type
                        KV kv1 = new KV();
                        kv1.set(KV.by("type", "object"));
                        PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(child, GlobalSearchScope.allScope(project));
                        kv1.set(KV.by("description", (Strings.isNullOrEmpty(remark)?(""+psiClassChild.getName().trim()):remark+" ,"+psiClassChild.getName().trim())));
                        if(!pName.equals(psiClassChild.getName())) {
                            List<String> requiredList=new ArrayList<>();
                            kv1.set(KV.by("properties", getFields(psiClassChild, project, childType, index+1,requiredList)));
                            kv1.set("required",requiredList);
                            addFilePaths(filePaths,psiClassChild);
                        }else{
                            kv1.set(KV.by("type", pName));
                        }
                        kv.set(name, kv1);
                    }
                }
            //    getField()
            } else if (type instanceof PsiArrayType) {
                //array type
                PsiType deepType = type.getDeepComponentType();
                KV kvlist = new KV();
                String deepTypeName = deepType.getPresentableText();
                String cType="";
                if (deepType instanceof PsiPrimitiveType) {
                    kvlist.set("type",type.getPresentableText());
                    if(!Strings.isNullOrEmpty(remark)) {
                        kvlist.set("description", remark);
                    }
                } else if (NormalTypes.isNormalType(deepTypeName)) {
                    kvlist.set("type",deepTypeName);
                    if(!Strings.isNullOrEmpty(remark)) {
                        kvlist.set("description", remark);
                    }
                } else {
                    kvlist.set(KV.by("type","object"));
                    PsiClass psiClass= PsiUtil.resolveClassInType(deepType);
                    cType=psiClass.getName();
                    kvlist.set(KV.by("description",(Strings.isNullOrEmpty(remark)?(""+psiClass.getName().trim()):remark+" ,"+psiClass.getName().trim())));
                    if(!pName.equals(PsiUtil.resolveClassInType(deepType).getName())){
                        List<String> requiredList=new ArrayList<>();
                        kvlist.set("properties",getFields(psiClass,project,null,null,requiredList));
                        kvlist.set("required",requiredList);
                        addFilePaths(filePaths,psiClass);
                    }else{
                        kvlist.set(KV.by("type",pName));
                    }
                }
                KV kv1=new KV();
                kv1.set(KV.by("type","array"));
                kv1.set(KV.by("description",(remark+" :"+cType).trim()));
                kv1.set("items",kvlist);
                kv.set(name, kv1);
            } else if (fieldTypeName.startsWith("List")||fieldTypeName.startsWith("Set") || fieldTypeName.startsWith("HashSet")) {
                //list type
                PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                String classTypeName = iterableClass.getName();
                getCollect(kv,classTypeName,remark,iterableClass,project,name,pName,childType,index);
            } else if(fieldTypeName.startsWith("HashMap") || fieldTypeName.startsWith("Map") || fieldTypeName.startsWith("LinkedHashMap")){
                //HashMap or Map
                CompletableFuture.runAsync(()->{
                    try {
                        TimeUnit.MILLISECONDS.sleep(700);
                        Notification warning = notificationGroup.createNotification("Map Type Can not Change,So pass", NotificationType.WARNING);
                        Notifications.Bus.notify(warning, project);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }else {
                //class type
                KV kv1=new KV();
                PsiClass psiClass=PsiUtil.resolveClassInType(type);
                kv1.set(KV.by("type","object"));
                kv1.set(KV.by("description",(Strings.isNullOrEmpty(remark)?(""+psiClass.getName().trim()):(remark+" ,"+psiClass.getName()).trim())));
                if(!pName.equals(((PsiClassReferenceType) type).getClassName())) {
                    addFilePaths(filePaths,psiClass);
                    List<String> requiredList=new ArrayList<>();
                    kv1.set(KV.by("properties", getFields(PsiUtil.resolveClassInType(type), project, childType, index,requiredList)));
                    kv1.set("required",requiredList);
                }else{
                    kv1.set(KV.by("type",pName));
                }
                kv.set(name,kv1);
            }
        }
    }


    /**
     * @description: 获得集合
     * @param: [kv, classTypeName, remark, psiClass, project, name, pName]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public static void getCollect(KV kv,String classTypeName,String remark,PsiClass psiClass,Project project,String name,String pName,String[] childType,Integer index) {
        KV kvlist = new KV();
        if (NormalTypes.isNormalType(classTypeName) || NormalTypes.collectTypes.containsKey(classTypeName)) {
            kvlist.set("type",classTypeName);
            if(!Strings.isNullOrEmpty(remark)) {
                kvlist.set("description", remark);
            }
        } else {
            kvlist.set(KV.by("type","object"));
            kvlist.set(KV.by("description",(Strings.isNullOrEmpty(remark)?(""+psiClass.getName().trim()):remark+" ,"+psiClass.getName().trim())));
            if(!pName.equals(psiClass.getName())) {
                List<String> requiredList=new ArrayList<>();
                kvlist.set("properties", getFields(psiClass, project, childType, index,requiredList));
                kvlist.set("required",requiredList);
                addFilePaths(filePaths,psiClass);
            }else{
                kvlist.set(KV.by("type",pName));
            }
        }
        KV kv1=new KV();
        kv1.set(KV.by("type","array"));
        kv1.set(KV.by("description",(Strings.isNullOrEmpty(remark)?(""+psiClass.getName().trim()):remark+" ,"+psiClass.getName().trim())));
        kv1.set("items",kvlist);
        kv.set(name, kv1);
    }


    /**
     * @description: 添加到文件路径列表
     * @param: [filePaths, psiClass]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/6
     */
    public static void addFilePaths(Set<String> filePaths,PsiClass psiClass){
        try {
            filePaths.add(((PsiJavaFileImpl) psiClass.getContext()).getViewProvider().getVirtualFile().getPath());
        }catch (Exception e){
            try {
                filePaths.add(((ClsFileImpl) psiClass.getContext()).getViewProvider().getVirtualFile().getPath());
            }catch (Exception e1){
            }
        }
    }


    /**
     * @description: 转换文件路径
     * @param: [project]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/6
     */ 
    public static void changeFilePath(Project project){
        Set<String> changeFilePaths=filePaths.stream().map(filePath->{
            if(filePath.contains(".jar")){
                String[] filePathsubs=  filePath.split("\\.jar");
                String jarPath=filePathsubs[0]+"-sources.jar";
                try {
                    //去解压源码包
                    FileUnZipUtil.uncompress(new File(jarPath),new File(filePathsubs[0]));
                    filePath=filePathsubs[0]+filePathsubs[1].replace("!","");
                    return filePath.replace(".class",".java");
                } catch (IOException e) {
                    Notification error = notificationGroup.createNotification("can not find sources java:"+jarPath, NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                }
            }
            return filePath;
        }).collect(Collectors.toSet());
        filePaths.clear();
        filePaths.addAll(changeFilePaths);
    }


}

package com.qbb.build;

import com.google.gson.JsonObject;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiQueryDTO;
import com.qbb.util.PsiAnnotationSearchUtil;
import com.yourkit.util.Strings;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NonNls;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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



    public YapiApiDTO actionPerformed(AnActionEvent e) {
        YapiApiDTO yapiApiDTO=new YapiApiDTO();
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = (PsiFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        Project project = editor.getProject();
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        String selectedText=e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        if(Strings.isNullOrEmpty(selectedText)){
            Notification error = notificationGroup.createNotification("please select method", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return null;
        }

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

        PsiMethod[] psiMethods =selectedClass.getAllMethods();
        //寻找目标Method
        PsiMethod psiMethodTarget=null;
        for(PsiMethod psiMethod:psiMethods){
            if(psiMethod.getName().equals(selectedText)){
                psiMethodTarget=psiMethod;
                break;
            }
        }
        PsiAnnotation psiAnnotationMethod= PsiAnnotationSearchUtil.findAnnotation(psiMethodTarget,SpringMVCConstant.RequestMapping);
        if(psiAnnotationMethod!=null){
            PsiNameValuePair[] psiNameValuePairs= psiAnnotationMethod.getParameterList().getAttributes();
            if(psiNameValuePairs!=null && psiNameValuePairs.length>0){
                for(PsiNameValuePair psiNameValuePair:psiNameValuePairs){
                    //获得方法上的路径
                    if("value".equals(psiNameValuePair.getName())){
                        PsiReference psiReference= psiNameValuePair.getDetachedValue().getReference();
                        if(psiReference==null){
                            path.append(psiNameValuePair.getValue());
                        }else{
                            path.append(psiReference.resolve().getText().split("=")[1].split(";")[0].replace("\"",""));
                            yapiApiDTO.setTitle(BuildJsonForYapi.trimFirstAndLastChar(psiReference.resolve().getText().replace("@description","").replace("@Description","").replace(":","").split("@")[0].replace("*","").replace("/","").replace("\n"," "),' '));
                        }
                        yapiApiDTO.setPath(path.toString());
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("GET")){
                        // 判断是否为Get 请求
                        yapiApiDTO.setMethod("GET");
                    }else if("method".equals(psiNameValuePair.getName()) && psiNameValuePair.getValue().toString().toUpperCase().contains("POST")){
                        // 判断是否为Post 请求
                        yapiApiDTO.setMethod("POST");
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
                }
            }
            if(psiAnnotationMethodSemple!=null) {
                PsiNameValuePair[] psiNameValuePairs = psiAnnotationMethodSemple.getParameterList().getAttributes();
                for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                    //获得方法上的路径
                    if (psiNameValuePair.getName().equals("value")) {
                        PsiReference psiReference = psiNameValuePair.getDetachedValue().getReference();
                        if (psiReference == null) {
                            path.append(psiNameValuePair.getValue());
                        } else {
                            path.append(psiReference.resolve().getText().split("=")[1].split(";")[0].replace("\"", ""));
                            yapiApiDTO.setTitle(BuildJsonForYapi.trimFirstAndLastChar(psiReference.resolve().getText().replace("@description","").replace("@Description","").replace(":","").split("@")[0].replace("*","").replace("/","").replace("\n"," "),' '));
                        }
                        yapiApiDTO.setPath(path.toString());
                    }
                }
            }
        }
        try {
            yapiApiDTO.setResponse(getResponse(project,psiMethodTarget.getReturnType()));
            getRequest(project,yapiApiDTO,psiMethodTarget);
            if(Strings.isNullOrEmpty(yapiApiDTO.getTitle())) {
                yapiApiDTO.setTitle(getDescription(psiMethodTarget));
            }
            return yapiApiDTO;
        } catch (Exception ex) {
            Notification error = notificationGroup.createNotification("Convert to JSON failed.", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }
        return null;
    }


    /**
     * @description: 获得描述
     * @param: [psiMethodTarget]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/2
     */
    public String getDescription(PsiMethod psiMethodTarget){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if (psiDocTag.getText().contains("@description") || psiDocTag.getText().contains("@Description")) {
                    return BuildJsonForYapi.trimFirstAndLastChar(psiDocTag.getText().replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("\n", " "), ' ');
                }
            }
            return BuildJsonForYapi.trimFirstAndLastChar(psiMethodTarget.getDocComment().getText().split("@")[0].replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("/", "").replace("\n", " "), ' ');
        }
        return null;
    }


    public static void getRequest(Project project,YapiApiDTO yapiApiDTO,PsiMethod psiMethodTarget) throws JSONException{
        PsiParameter[] psiParameters= psiMethodTarget.getParameterList().getParameters();
        if(psiParameters.length>0) {
            ArrayList list=new ArrayList<YapiQueryDTO>();
            for(PsiParameter psiParameter:psiParameters){
                PsiAnnotation psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestBody);
                if(psiAnnotation!=null){
                    yapiApiDTO.setRequestBody(getResponse(project,psiParameter.getType()));
                }else{
                    psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(psiParameter,SpringMVCConstant.RequestParam);
                    PsiNameValuePair[] psiNameValuePairs=psiAnnotation.getParameterList().getAttributes();
                    YapiQueryDTO yapiQueryDTO=new YapiQueryDTO();
                    for(PsiNameValuePair psiNameValuePair:psiNameValuePairs){
                        if("name".equals(psiNameValuePair.getName())){
                            yapiQueryDTO.setName(psiNameValuePair.getValue().getText().replace("\"",""));
                        }else if("value".equals(psiNameValuePair.getName())){
                            yapiQueryDTO.setName(psiNameValuePair.getValue().getText().replace("\"",""));
                        }else if("required".equals(psiNameValuePair.getName())){
                            yapiQueryDTO.setRequired(psiNameValuePair.getValue().getText().replace("\"","").replace("false","0").replace("true","1"));
                        }else if("defaultValue".equals(psiNameValuePair.getName())){
                            yapiQueryDTO.setExample(psiNameValuePair.getValue().getText().replace("\"",""));
                        }else{
                            yapiQueryDTO.setName(psiNameValuePair.getLiteralValue());
                            yapiQueryDTO.setExample(NormalTypes.normalTypes.get(psiParameter.getType().getPresentableText()).toString());
                            yapiQueryDTO.setDesc(psiParameter.getType().getPresentableText());
                        }
                    }
                    list.add(yapiQueryDTO);
                }
            }
            yapiApiDTO.setParams(list);
        }
    }


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
                }else {
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                    KV kvObject = getFields(psiClassChild, project,null,null);
                    listKv.set("type","object");
                    listKv.set("properties", kvObject);
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
                }else {
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                    KV kvObject = getFields(psiClassChild, project,null,null);
                    listKv.set("type","object");
                    listKv.set("properties", kvObject);
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
                hashMapChild.put("String","Object");
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
        }else{
            String[] types=psiType.getCanonicalText().split("<");
            if(types.length>1) {
                PsiClass psiClassChild =  JavaPsiFacade.getInstance(project).findClass(types[0], GlobalSearchScope.allScope(project));
                KV result = new KV();
                KV kvObject = getFields(psiClassChild, project,types,1);
                result.set("type", "object");
                result.set("title", psiType.getPresentableText());
                result.set("description", psiType.getPresentableText());
                result.set("properties", kvObject);
                String json = result.toPrettyJson();
                return json;
            }else{
                PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(), GlobalSearchScope.allScope(project));
                KV result = new KV();
                KV kvObject = getFields(psiClassChild, project,null,null);
                result.set("type", "object");
                result.set("title", psiType.getPresentableText());
                result.set("description", psiType.getPresentableText());
                result.set("properties", kvObject);
                String json = result.toPrettyJson();
                return json;
            }
        }
        return null;
    }


    public static KV getFields(PsiClass psiClass,Project project,String[] childType,Integer index) {
        KV kv = KV.create();
        if (psiClass != null) {
            for (PsiField field : psiClass.getAllFields()) {
               getField(field,project,kv,childType,index);
            }
        }
        return kv;
    }


    public static  void getField(PsiField field,Project project,KV kv,String[] childType,Integer index){
        if(field.getModifierList().hasModifierProperty("final")){
            return;
        }
        PsiType type = field.getType();
        String name = field.getName();
        String remark ="";
        if(field.getDocComment()!=null) {
            remark=field.getDocComment().getText().replace("*", "").replace("/", "").replace(" ", "").replace("\n", ",").replace("\t","");
            remark=trimFirstAndLastChar(remark,',');
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
            }else if(((PsiClassReferenceType) type).resolve().isEnum()) {
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
                        getCollect(kv, psiClassChild.getName(), remark, psiClassChild, project, name);
                    } else {
                        //class type
                        KV kv1 = new KV();
                        kv1.set(KV.by("type", "object"));
                        if (!Strings.isNullOrEmpty(remark)) {
                            kv1.set(KV.by("description", remark));
                        }
                        kv1.set(KV.by("properties", getFields(PsiUtil.resolveClassInType(type), project, null, null)));
                        kv.set(name, kv1);
                    }
                }
            //    getField()
            } else if (type instanceof PsiArrayType) {
                //array type
                PsiType deepType = type.getDeepComponentType();
                KV kvlist = new KV();
                String deepTypeName = deepType.getPresentableText();
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
                    if(!Strings.isNullOrEmpty(remark)) {
                        kvlist.set(KV.by("description",remark));
                    }
                    kvlist.set("properties",getFields(PsiUtil.resolveClassInType(deepType),project,null,null));
                }
                KV kv1=new KV();
                kv1.set(KV.by("type","array"));
                if(!Strings.isNullOrEmpty(remark)) {
                    kv1.set(KV.by("description",remark));
                }
                kv1.set("items",kvlist);
                kv.set(name, kv1);
            } else if (fieldTypeName.startsWith("List")||fieldTypeName.startsWith("Set") || fieldTypeName.startsWith("HashSet")) {
                //list type
                PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                String classTypeName = iterableClass.getName();
                getCollect(kv,classTypeName,remark,iterableClass,project,name);
            } else if(fieldTypeName.startsWith("HashMap") || fieldTypeName.startsWith("Map")){
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
                kv1.set(KV.by("type","object"));
                if(!Strings.isNullOrEmpty(remark)) {
                    kv1.set(KV.by("description",remark));
                }
                kv1.set(KV.by("properties",getFields(PsiUtil.resolveClassInType(type),project,null,null)));
                kv.set(name,kv1);
            }
        }
    }



    public static void getCollect(KV kv,String classTypeName,String remark,PsiClass psiClass,Project project,String name) {
        KV kvlist = new KV();
        if (NormalTypes.isNormalType(classTypeName)) {
            kvlist.set("type",classTypeName);
            if(!Strings.isNullOrEmpty(remark)) {
                kvlist.set("description", remark);
            }
        } else {
            kvlist.set(KV.by("type","object"));
            if(!Strings.isNullOrEmpty(remark)) {
                kvlist.set(KV.by("description",remark));
            }
            kvlist.set("properties",getFields(psiClass,project,null,null));
        }
        KV kv1=new KV();
        kv1.set(KV.by("type","array"));
        if(!Strings.isNullOrEmpty(remark)) {
            kv1.set(KV.by("description",remark));
        }
        kv1.set("items",kvlist);
        kv.set(name, kv1);
    }
    /**
     * 去除字符串首尾出现的某个字符.
     * @param source 源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source,char element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            if(Strings.isNullOrEmpty(source.trim())){
                source=null;
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }
}

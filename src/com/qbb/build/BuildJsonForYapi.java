package com.qbb.build;

import com.google.gson.JsonObject;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
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



    public String actionPerformed(AnActionEvent e) {
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = (PsiFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        Project project = editor.getProject();
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        try {
            KV result=new KV();
            KV kv = getFields(selectedClass,project,null,null);
            result.set("type","object");
            result.set("title",selectedClass.getName());
            result.set("description",selectedClass.getName());
            result.set("properties",kv);
            String json = result.toPrettyJson();
            return json;
        } catch (Exception ex) {
            Notification error = notificationGroup.createNotification("Convert to JSON failed.", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }
        return null;
    }


    public static String getResponse(Project project,PsiType psiType) throws JSONException{
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
            } else if(fieldTypeName.equals("T")) {
                String child=childType[index].split(">")[0];
                if(child.contains("List") || child.contains("Set") ||child.contains("HashSet") ){
                    PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childType[index+1].split(">")[0], GlobalSearchScope.allScope(project));
                    getCollect(kv,psiClassChild.getName(),remark,psiClassChild,project,name);
                }else{
                    //class type
                    KV kv1=new KV();
                    kv1.set(KV.by("type","object"));
                    if(!Strings.isNullOrEmpty(remark)) {
                        kv1.set(KV.by("description",remark));
                    }
                    kv1.set(KV.by("properties",getFields(PsiUtil.resolveClassInType(type),project,null,null)));
                    kv.set(name,kv1);
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
            if(Strings.isNullOrEmpty(source)){break;}
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }
}

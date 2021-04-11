package com.qbb.rebuild;

import com.google.common.base.Strings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.qbb.constant.HttpMethodConstant;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.process.AbstractProcessor;
import com.qbb.rebuild.process.DefaultProcess;
import com.qbb.util.PsiAnnotationSearchUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chong.zhang
 * @Date: 2021-04-10 00:47:50
 */

public class ApiBuildContext {

    private String attachUpload;
    private String returnClass;
    private PsiFile psiFile;
    private String selectedText;
    private Project project;
    private PsiClass selectedClass;
    private ArrayList<YapiApiDTO> yapiApiDTOS;

    AbstractProcessor<YapiApiDTO> processor = new DefaultProcess();

    public ApiBuildContext(AnActionEvent event, String attachUpload, String returnClass) throws Exception {
        // 初始化参数
        this.attachUpload = attachUpload;
        this.returnClass = returnClass;
        yapiApiDTOS = new ArrayList<>();
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        psiFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        selectedText = event.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        project = editor.getProject();
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});

        refresh();
    }

    private void refresh() throws Exception {
        List<PsiMethod> psiMethods = getTargetMethod();
        psiMethods.forEach(item -> {
            YapiApiDTO bean = new YapiApiDTO();
            yapiApiDTOS.add(bean);
            processor.process(bean, this, item);
        });
    }

    /**
     * 获取方法上的GetMapping等注解类型
     * @param psiMethod
     * @return
     */
    public Map<String, Object> getMethAnnota(PsiMethod psiMethod){
        String type = null;
        PsiAnnotation psiAnnotationMethodSemple = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.GetMapping);

        if (psiAnnotationMethodSemple != null) {
            type = HttpMethodConstant.GET;
        } else {
            psiAnnotationMethodSemple = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.PostMapping);
            if (psiAnnotationMethodSemple != null) {
                type = HttpMethodConstant.POST;
            } else {
                psiAnnotationMethodSemple = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.PutMapping);
                if (psiAnnotationMethodSemple != null) {
                    type = HttpMethodConstant.PUT;
                } else {
                    psiAnnotationMethodSemple = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.DeleteMapping);
                    if (psiAnnotationMethodSemple != null) {
                        type = HttpMethodConstant.DELETE;
                    } else {
                        psiAnnotationMethodSemple = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.PatchMapping);
                        if (psiAnnotationMethodSemple != null) {
                            type = HttpMethodConstant.PATCH;
                        }
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("type", type);
        result.put("Annotation", psiAnnotationMethodSemple);
        return result;
    }

    /**
     * 获取目标方法
     * @return List<PsiMethod>
     */
    private List<PsiMethod> getTargetMethod() throws Exception {
        if (Strings.isNullOrEmpty(selectedText) || selectedText.equals(selectedClass.getName())) {
            // 未选中方法或者选中的是类
            PsiMethod[] psiMethods = selectedClass.getMethods();
            return Arrays.stream(psiMethods)
                    .filter(item -> !item.getModifierList().hasModifierProperty("private")
                            && Objects.nonNull(item.getReturnType()))
                    .collect(Collectors.toList());
        } else {
            // 选中方法
            PsiMethod[] psiMethods = selectedClass.getAllMethods();
            List<PsiMethod> result =  Arrays.stream(psiMethods)
                    .filter(item -> item.getName().equals(selectedText))
                    .collect(Collectors.toList());
            if (result.size() <= 0){
                throw new Exception("未找到方法: " + selectedText);
            }else {
                return result;
            }
        }
    }

    public String getAttachUpload() {
        return attachUpload;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public Project getProject() {
        return project;
    }

    public PsiClass getSelectedClass() {
        return selectedClass;
    }

    public ArrayList<YapiApiDTO> getYapiApiDTOS() {
        return yapiApiDTOS;
    }
}

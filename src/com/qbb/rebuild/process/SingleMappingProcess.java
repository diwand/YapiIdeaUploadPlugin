package com.qbb.rebuild.process;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.qbb.constant.HttpMethodConstant;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.DesUtil;
import com.qbb.util.PsiAnnotationSearchUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 处理requestMapping上的信息
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class SingleMappingProcess extends AbstractProcessor<YapiApiDTO> {

    public SingleMappingProcess() {
        // 设置下一个处理节点
        super();
        super.setNextProcesser(new ComplexProcess());
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        Map map = context.getMethAnnota(psiMethod);
        PsiAnnotation annotation = (PsiAnnotation)map.get("Annotation");
        String method = (String) map.get("type");

        StringBuilder path = new StringBuilder(yapiApiDTO.getPath());
        if (annotation != null) {
            // method
            yapiApiDTO.setMethod(method);

            PsiNameValuePair[] psiNameValuePairs = annotation.getParameterList().getAttributes();
            if (psiNameValuePairs.length > 0) {
                Arrays.stream(psiNameValuePairs).filter(item ->
                        Objects.isNull(item.getName())
                                || "value".equals(item.getName())
                                || "path".equals(item.getName())
                ).forEach(item -> {
                    PsiReference psiReference = item.getValue().getReference();
                    if (psiReference == null) {
                        // 注解中使用的是魔数
                        DesUtil.addPath(path, item.getLiteralValue());
                    } else {
                        // 注解中使用的是变量
                        String[] results = psiReference.resolve().getText().split("=");
                        DesUtil.addPath(path, results[results.length - 1].split(";")[0].replace("\"", "").trim());

                        // info : title、menu等
                        yapiApiDTO.setTitle(DesUtil.getUrlReFerenceRDesc(psiReference.resolve().getText()));
                        yapiApiDTO.setMenu(DesUtil.getMenu(psiReference.resolve().getText()));
                        yapiApiDTO.setStatus(DesUtil.getStatus(psiReference.resolve().getText()));
                        yapiApiDTO.setDesc("<pre><code>  " + psiReference.resolve().getText() + " </code></pre> <hr>");

                    }
                    yapiApiDTO.setPath(path.toString());
                });
            }
        }
    }
}

package com.qbb.rebuild.process;

import com.intellij.psi.*;
import com.qbb.constant.HttpMethodConstant;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.DesUtil;
import com.qbb.util.PsiAnnotationSearchUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * 处理requestMapping上的信息
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class RequestMappingProcess extends AbstractProcessor<YapiApiDTO> {

    public RequestMappingProcess() {
        // 设置下一个处理节点
        super();
        super.setNextProcesser(new SingleMappingProcess());
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        PsiAnnotation psiAnnotationMethod = PsiAnnotationSearchUtil.findAnnotation(psiMethod, SpringMVCConstant.RequestMapping);
        if (psiAnnotationMethod != null) {
            // 跳过 SingleMappingProcess
            super.setNextProcesser(new ComplexProcess());

            StringBuilder path = new StringBuilder(yapiApiDTO.getPath());
            PsiNameValuePair[] psiNameValuePairs = psiAnnotationMethod.getParameterList().getAttributes();
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

                // method, 当有多种请求方式的时候，按下面的顺序匹配
                Arrays.stream(psiNameValuePairs).filter(item -> "method".equals(item.getName()))
                        .forEach(item -> {
                            String method = item.getValue().toString().toUpperCase();
                            if (method.contains(HttpMethodConstant.GET)){
                                yapiApiDTO.setMethod(HttpMethodConstant.GET);
                            } else if (method.contains(HttpMethodConstant.POST)){
                                yapiApiDTO.setMethod(HttpMethodConstant.POST);
                            } else if (method.contains(HttpMethodConstant.PUT)){
                                yapiApiDTO.setMethod(HttpMethodConstant.PUT);
                            } else if (method.contains(HttpMethodConstant.DELETE)){
                                yapiApiDTO.setMethod(HttpMethodConstant.DELETE);
                            } else if (method.contains(HttpMethodConstant.PATCH)){
                                yapiApiDTO.setMethod(HttpMethodConstant.PATCH);
                            } });

            }
        }
    }
}

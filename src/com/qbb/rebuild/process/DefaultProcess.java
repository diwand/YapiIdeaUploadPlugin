package com.qbb.rebuild.process;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.DesUtil;
import com.qbb.util.PsiAnnotationSearchUtil;

/**
 * 获取类上面的RequestMapping 中的path
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class DefaultProcess extends AbstractProcessor<YapiApiDTO> {

    public DefaultProcess() {
        // 设置下一个处理节点
        super.setNextProcesser(new SwaggerTitleProcess());
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        StringBuilder path = new StringBuilder();
        PsiAnnotation psiAnnotation = PsiAnnotationSearchUtil.findAnnotation(context.getSelectedClass(), SpringMVCConstant.RequestMapping);
        if (psiAnnotation != null) {
            PsiNameValuePair[] psiNameValuePairs = psiAnnotation.getParameterList().getAttributes();
            if (psiNameValuePairs.length > 0) {
                if (psiNameValuePairs[0].getLiteralValue() != null) {
                    DesUtil.addPath(path, psiNameValuePairs[0].getLiteralValue());
                } else {
                    PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findAttributeValue("value");
                    if (psiAnnotationMemberValue != null) {
                        String[] results = psiAnnotationMemberValue.getReference().resolve().getText().split("=");
                        DesUtil.addPath(path, results[results.length - 1].split(";")[0].replace("\"", "").trim());
                    }
                }
            }
        }

        yapiApiDTO.setPath(path.toString());
    }
}

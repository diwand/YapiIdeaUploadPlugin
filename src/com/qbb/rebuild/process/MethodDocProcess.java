package com.qbb.rebuild.process;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.qbb.constant.SpringMVCConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.DesUtil;
import com.qbb.util.PsiAnnotationSearchUtil;

import java.util.Objects;

/**
 * 处理方法上的自定义注释
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class MethodDocProcess extends AbstractProcessor<YapiApiDTO> {

    public MethodDocProcess() {
        // 设置下一个处理节点
        super.setNextProcesser(new CheckMenuProcess());
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        if (Strings.isNullOrEmpty(yapiApiDTO.getTitle())) {
            yapiApiDTO.setTitle(DesUtil.getDescription(psiMethod));
            if (Objects.nonNull(psiMethod.getDocComment())) {
                // 支持菜单
                String menu = DesUtil.getMenu(psiMethod.getDocComment().getText());
                if (!Strings.isNullOrEmpty(menu)) {
                    yapiApiDTO.setMenu(menu);
                }
                // 支持状态
                String status = DesUtil.getStatus(psiMethod.getDocComment().getText());
                if (!Strings.isNullOrEmpty(status)) {
                    yapiApiDTO.setStatus(status);
                }
                // 支持自定义路径
                String pathCustom=DesUtil.getPath(psiMethod.getDocComment().getText());
                if(!Strings.isNullOrEmpty(pathCustom)){
                    yapiApiDTO.setPath(pathCustom);
                }
            }
        }
    }
}

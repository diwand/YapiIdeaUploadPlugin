package com.qbb.rebuild.process;

import com.google.common.base.Strings;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.DesUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * 如果menu为空，设置menu
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class CheckMenuProcess extends AbstractProcessor<YapiApiDTO> {

    public CheckMenuProcess() {
        // 设置下一个处理节点
        super.setNextProcesser(null);
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        PsiClass selectedClass = context.getSelectedClass();
        String classMenu = null;
        if (Objects.nonNull(selectedClass.getContext())) {
            String text = selectedClass.getContext().getText().replace(selectedClass.getText(), "");
            classMenu = DesUtil.getMenu(text);
        }
        if (Objects.nonNull(selectedClass.getDocComment())) {
            classMenu = DesUtil.getMenu(selectedClass.getText());
        }
        if (StringUtils.isEmpty(classMenu)) {
            classMenu = DesUtil.camelToLine(selectedClass.getName(),null);
        }
        yapiApiDTO.setMenu(classMenu);
    }
}

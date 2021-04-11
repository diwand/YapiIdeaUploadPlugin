package com.qbb.rebuild.process;

import com.intellij.psi.PsiMethod;
import com.qbb.constant.SwaggerConstants;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.PsiAnnotationSearchUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 处理 注解io.swagger.annotations.ApiOperation
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class SwaggerTitleProcess extends AbstractProcessor<YapiApiDTO> {

    public SwaggerTitleProcess() {
        super();
        super.setNextProcesser(new RequestMappingProcess());
    }

    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) {
        String operation = PsiAnnotationSearchUtil.getPsiParameterAnnotationValue(psiMethod, SwaggerConstants.API_OPERATION);
        if (StringUtils.isNotEmpty(operation)) {
            yapiApiDTO.setTitle(operation);
        }
    }
}

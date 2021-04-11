package com.qbb.rebuild.process;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.qbb.build.BuildJsonForYapi;
import com.qbb.dto.YapiApiDTO;
import com.qbb.rebuild.ApiBuildContext;
import com.qbb.util.FileToZipUtil;
import com.qbb.util.UploadUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.qbb.build.BuildJsonForYapi.*;

/**
 * 上传类文件，设置请求参数等
 * @Author: chong.zhang
 * @Date: 2021-04-10 17:16:25
 */

public class ComplexProcess extends AbstractProcessor<YapiApiDTO> {

    public ComplexProcess() {
        super();
        super.setNextProcesser(new MethodDocProcess());
    }

    static Set<String> filePaths = new CopyOnWriteArraySet<>();


    @Override
    void realProcess(YapiApiDTO yapiApiDTO, ApiBuildContext context, PsiMethod psiMethod) throws Exception {
        Project project = context.getProject();
        String attachUpload = context.getAttachUpload();
        String returnClass = context.getReturnClass();

        String classDesc = psiMethod.getText().replace(Objects.nonNull(psiMethod.getBody()) ? psiMethod.getBody().getText() : "", "");
        if (!Strings.isNullOrEmpty(classDesc)) {
            classDesc = classDesc.replace("<", "&lt;").replace(">", "&gt;");
        }
        yapiApiDTO.setDesc(Objects.nonNull(yapiApiDTO.getDesc()) ? yapiApiDTO.getDesc() : " <pre><code>  " + classDesc + "</code></pre>");
        try {
            // 先清空之前的文件路径
            filePaths.clear();
            // 生成响应参数
            yapiApiDTO.setResponse(BuildJsonForYapi.getResponse(project, psiMethod.getReturnType(), returnClass));
            Set<String> codeSet = new HashSet<>();
            Long time = System.currentTimeMillis();
            String responseFileName = "/response_" + time + ".zip";
            String requestFileName = "/request_" + time + ".zip";
            String codeFileName = "/code_" + time + ".zip";
            if (!Strings.isNullOrEmpty(attachUpload)) {
                // 打包响应参数文件
                if (filePaths.size() > 0) {
                    changeFilePath(project);
                    FileToZipUtil.toZip(filePaths, project.getBasePath() + responseFileName, true);
                    filePaths.clear();
                    codeSet.add(project.getBasePath() + responseFileName);
                }
                // 清空路径
                // 生成请求参数
            } else {
                filePaths.clear();
            }
            getRequest(project, yapiApiDTO, psiMethod);
            if (!Strings.isNullOrEmpty(attachUpload)) {
                if (filePaths.size() > 0) {
                    changeFilePath(project);
                    FileToZipUtil.toZip(filePaths, project.getBasePath() + requestFileName, true);
                    filePaths.clear();
                    codeSet.add(project.getBasePath() + requestFileName);
                }
                // 打包请求参数文件
                if (codeSet.size() > 0) {
                    FileToZipUtil.toZip(codeSet, project.getBasePath() + codeFileName, true);
                    if (!Strings.isNullOrEmpty(attachUpload)) {
                        String fileUrl = new UploadUtil().uploadFile(attachUpload, project.getBasePath() + codeFileName);
                        if (!Strings.isNullOrEmpty(fileUrl)) {
                            yapiApiDTO.setDesc("java类:<a href='" + fileUrl + "'>下载地址</a><br/>" + yapiApiDTO.getDesc());
                        }
                    }
                }
            } else {
                filePaths.clear();
            }
            //清空打包文件
            if (!Strings.isNullOrEmpty(attachUpload)) {
                File file = new File(project.getBasePath() + codeFileName);
                if (file.exists() && file.isFile()) {
                    file.delete();
                    file = new File(project.getBasePath() + responseFileName);
                    file.delete();
                    file = new File(project.getBasePath() + requestFileName);
                    file.delete();
                }
            }

        } catch (Exception ex) {
            String message = Objects.nonNull(ex.getMessage()) ? ex.getMessage() : "创建 response/request 失败";
            throw new Exception(message);
        }
    }
}

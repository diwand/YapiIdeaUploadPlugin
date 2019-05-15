package com.qbb.upload;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.*;
import com.qbb.util.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 上传到yapi
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 11:41 AM
 */
public class UploadYapi {


    private Gson gson=new Gson();

    public static Map<String,Integer> catMap=new HashMap<>();

    /**
     * @description: 调用保存接口
     * @param: [yapiSaveParam, attachUpload, path]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public YapiResponse  uploadSave(YapiSaveParam yapiSaveParam,String attachUpload,String path) throws IOException {
        if(Strings.isNullOrEmpty(yapiSaveParam.getTitle())){
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        if(yapiSaveParam.getReq_headers()==null || yapiSaveParam.getReq_headers().isEmpty()){
            YapiHeaderDTO yapiHeaderDTO=new YapiHeaderDTO();
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/json");
            if(Objects.isNull(yapiSaveParam.getReq_headers())){
                List list=new ArrayList();
                list.add(yapiHeaderDTO);
                yapiSaveParam.setReq_headers(list);
            }else{
                yapiSaveParam.getReq_headers().add(yapiHeaderDTO);
            }
        }
        if(!Strings.isNullOrEmpty(attachUpload) && !Strings.isNullOrEmpty(path)){
            File file=new File(path+"/code.zip");
            if(file.exists()&&file.isFile()) {
                file.delete();
                file=new File(path+"/response.zip");
                file.delete();
                file=new File(path+"/request.zip");
                file.delete();
            }
            // 移除 文件
        }
        YapiResponse yapiResponse= this.getCatIdOrCreate(yapiSaveParam);
        if(yapiResponse.getErrcode()==0 && yapiResponse.getData()!=null){
            yapiSaveParam.setCatid(String.valueOf(yapiResponse.getData()));
            String response=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiSave,gson.toJson(yapiSaveParam))),"utf-8");
            return gson.fromJson(response,YapiResponse.class);
        }else{
            return yapiResponse;
        }
    }



    /**
     * 获得httpPost
     * @return
     */
    private HttpPost getHttpPost(String url, String body) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            //httpPost.setHeader("Connection", "close");
            HttpEntity reqEntity = new StringEntity(body == null ? "" : body, "UTF-8");
            httpPost.setEntity(reqEntity);
        } catch (Exception e) {
        }
        return httpPost;
    }

    /**
     * @description: 上传文件
     * @param: [url, filePath]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public String uploadFile(String url,String filePath){
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            //httpPost.setHeader("Connection", "close");
            FileBody bin = new FileBody(new File(filePath));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();
            httpPost.setEntity(reqEntity);
            return  HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(httpPost),"utf-8");
        } catch (Exception e) {
        }
        return "";
    }


    private HttpGet getHttpGet(String url){
        try {
            return HttpClientUtil.getHttpGet(url, "application/json", "application/json; charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * @description: 获得分类或者创建分类或者
     * @param: [yapiSaveParam]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public YapiResponse getCatIdOrCreate(YapiSaveParam yapiSaveParam){
        Integer catId= catMap.get(yapiSaveParam.getProjectId().toString());
        if(catId!=null){
            return new YapiResponse(catId);
        }
        String response= null;
        try {
            response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiSaveParam.getYapiUrl()+ YapiConstant.yapiCatMenu+"?project_id="+yapiSaveParam.getProjectId()+"&token="+yapiSaveParam.getToken())),"utf-8");
            YapiResponse yapiResponse=gson.fromJson(response,YapiResponse.class);
            if(yapiResponse.getErrcode()==0) {
                List<YapiCatResponse> list = (List<YapiCatResponse>) yapiResponse.getData();
                list=gson.fromJson(gson.toJson(list),new TypeToken<List<YapiCatResponse>>() {
                }.getType());
                for (YapiCatResponse yapiCatResponse : list) {
                    if (yapiCatResponse.getName().equals("tool-temp")) {
                        catMap.put(yapiSaveParam.getProjectId().toString(),yapiCatResponse.get_id());
                        return new YapiResponse(yapiCatResponse.get_id());
                    }
                }
            }
            YapiCatMenuParam  yapiCatMenuParam=new YapiCatMenuParam(yapiSaveParam.getProjectId(),yapiSaveParam.getToken());
            String responseCat=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiAddCat,gson.toJson(yapiCatMenuParam))),"utf-8");
            YapiCatResponse yapiCatResponse=gson.fromJson(gson.fromJson(responseCat,YapiResponse.class).getData().toString(),YapiCatResponse.class);
            catMap.put(yapiSaveParam.getProjectId().toString(),yapiCatResponse.get_id());
            return  new YapiResponse(yapiCatResponse.get_id());
        } catch (IOException e) {
            e.printStackTrace();
           return  new YapiResponse(0,e.toString());
        }
    }



}

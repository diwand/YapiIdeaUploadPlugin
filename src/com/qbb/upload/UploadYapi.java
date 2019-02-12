package com.qbb.upload;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.YapiCatMenuParam;
import com.qbb.dto.YapiCatResponse;
import com.qbb.dto.YapiResponse;
import com.qbb.dto.YapiSaveParam;
import com.qbb.util.HttpClientUtil;
import com.yourkit.util.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上传到yapi
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 11:41 AM
 */
public class UploadYapi {


    private Gson gson=new Gson();

    private static Map<String,Integer> catMap=new HashMap<>();


    public YapiResponse  uploadSave(YapiSaveParam yapiSaveParam) throws IOException {
        if(Strings.isNullOrEmpty(yapiSaveParam.getTitle())){
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        if(yapiSaveParam.getReq_headers()==null || yapiSaveParam.getReq_headers().isEmpty()){
            Map map=new HashMap();
            map.put("name","Content-Type");
            map.put("value","application/json");
            List list=new ArrayList();
            list.add(map);
            yapiSaveParam.setReq_headers(list);
        }
        if(yapiSaveParam.getEdit_uid()==null){
            yapiSaveParam.setEdit_uid(11);
        }
        yapiSaveParam.setCatid(String.valueOf(this.getCatIdOrCreate(yapiSaveParam)));
        String response=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiSave,gson.toJson(yapiSaveParam))),"utf-8");
        return gson.fromJson(response,YapiResponse.class);
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


    private HttpGet getHttpGet(String url){
        try {
            return HttpClientUtil.getHttpGet(url, "application/json", "application/json; charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public Integer getCatIdOrCreate(YapiSaveParam yapiSaveParam){
        Integer catId= catMap.get(yapiSaveParam.getProjectId().toString());
        if(catId!=null){
            return catId;
        }
        String response= null;
        try {
            response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiSaveParam.getYapiUrl()+ YapiConstant.yapiCatMenu+"?project_id="+yapiSaveParam.getProjectId()+"&token="+yapiSaveParam.getToken())),"utf-8");
            YapiResponse yapiResponse=gson.fromJson(response,YapiResponse.class);
            if(yapiResponse.getErrcode().equals(0)) {
                List<YapiCatResponse> list = (List<YapiCatResponse>) yapiResponse.getData();
                list=gson.fromJson(gson.toJson(list),new TypeToken<List<YapiCatResponse>>() {
                }.getType());
                for (YapiCatResponse yapiCatResponse : list) {
                    if (yapiCatResponse.getName().equals("tool-temp")) {
                        catMap.put(yapiSaveParam.getProjectId().toString(),yapiCatResponse.get_id());
                        return yapiCatResponse.get_id();
                    }
                }
            }
            YapiCatMenuParam  yapiCatMenuParam=new YapiCatMenuParam(yapiSaveParam.getProjectId(),yapiSaveParam.getToken());
            String responseCat=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiAddCat,gson.toJson(yapiCatMenuParam))),"utf-8");
            YapiCatResponse yapiCatResponse=gson.fromJson(gson.fromJson(responseCat,YapiResponse.class).getData().toString(),YapiCatResponse.class);
            catMap.put(yapiSaveParam.getProjectId().toString(),yapiCatResponse.get_id());
            return  yapiCatResponse.get_id();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}

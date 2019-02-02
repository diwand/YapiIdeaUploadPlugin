
#### 目的
减少yapi 录入时间


#### 支持接口
目前 dubbo 接口


#### 使用方式
- 导入jar 包
- 配置信息：在项目目录下，.idea 文件夹下，找到misc.xml


token 获取方式： 打开yapi ->具体项目->设置->token 配置 <br>
项目id 获取方式：点击项目，查看url 中project 后面的数字为项目id  http://47.96.254.39:3000/project/72/interface/api<br>
yapiUrl 固定<br>
编辑人id 目前随便填，但要填<br>
projectType 根据你要上传的接口类型决定，如果为dubbo 接口就填dubbo ，如果是api 接口就填api<br>




```xml
<component name="yapi">
  <option name="projectToken">yapi 中项目token</option>
  <option name="projectId">yapi 中项目id</option>
  <option name="yapiUrl">http://47.96.254.39:3000</option>
  <option name="projectType">dubbo/api</option>
  <option name="editId">38(编辑人id)</option> 
</component>
```


- 选中dubbo interface 文件中的一个方法（要选中方法名称），右击YapiUpload(alt+u 快捷键)




> 注意：接口会上传到一个临时文件目录下 tool-temp，根据需要移动,接口注释要加上@description: ，不然会已路径作为接口描述，dubbo 版本为1.0


### 目前只是测试版，未完善，有问题反馈


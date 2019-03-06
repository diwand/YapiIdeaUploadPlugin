
#### 目的
减少yapi 录入时间


#### 单个&批量

单个上传选中方法 <br>
批量上传选中类名 <br>

#### 支持接口
目前 dubbo 接口/api


#### 使用方式
- 导入jar 包
- 配置信息：在项目目录下，.idea 文件夹下，找到misc.xml


token 获取方式： 打开yapi ->具体项目->设置->token 配置 <br>
项目id 获取方式：点击项目，查看url 中project 后面的数字为项目id  http://47.96.254.39:3000/project/72/interface/api<br>
yapiUrl 固定<br>
projectType 根据你要上传的接口类型决定，如果为dubbo 接口就填dubbo ，如果是api 接口就填api<br>




```xml
<component name="yapi">
  <option name="projectToken">yapi 中项目token</option>
  <option name="projectId">yapi 中项目id</option>
  <option name="yapiUrl">http://47.96.254.39:3000</option>
  <option name="projectType">dubbo/api</option>
</component>
```


- 选中dubbo interface 文件中的一个方法（要选中方法名称），右击YapiUpload(alt+u 快捷键)




> 注意：接口会上传到一个临时文件目录下 tool-temp，根据需要移动


### 目前只是测试版，未完善，有问题反馈


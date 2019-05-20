#### 目的
减少yapi 录入时间，通过工具反向规范代码注释，和代码整洁

#### 支持语言
java

#### 单个&批量

单个上传选中方法名称 <br>
批量上传选中类名 <br>

#### 支持接口
目前 dubbo 接口/api

#### 支持yapi 的版本
1.5.0+

#### 支持idea 版本
2017+


#### 使用方式
- 下载yapiupload jar 包 （或者在idea 插件库搜索）
- 打开idea，preferneces->plugins-> install plugin from disk（或者搜索 yapiupload),导入jar 包后(install)，重启
- 单模块配置信息：在项目目录下，.idea 文件夹下，找到misc.xml   (如果找不到.idea 请查看是否被折叠或被隐藏) 如果是 .ipr 模式创建的
就找到 项目名.ipr


token 获取方式： 打开yapi ->具体项目->设置->token 配置 <br>
项目id 获取方式：点击项目，查看url 中project 后面的数字为项目id  http://127.0.0.1:3000/project/72/interface/api<br>
yapiUrl 固定<br>
projectType 根据你要上传的接口类型决定，如果为dubbo 接口就填dubbo ，如果是api 接口就填api<br>

attachUploadUrl:上传java 类zip 的url,可不填,如果要用请实现http://localhost/fileupload 接口
接口请求参数为 file  文件类型。


```xml
<component name="yapi">
  <option name="projectToken">yapi 中项目token</option>
  <option name="projectId">yapi 中项目id</option>
  <option name="yapiUrl">http://127.0.0.1:3000</option>
  <option name="projectType">api</option>
  <option name="attachUploadUrl">http://localhost/fileupload</option>
</component>
```

- 多模块配置信息：在项目目录下，.idea 文件夹下，找到misc.xml   (如果找不到.idea 请查看是否被折叠或被隐藏) 如果是 .ipr 模式创建的
          就找到 项目名.ipr
          
moduleList 获取方式：模块名称，用 "," 分割 ，不支持父节点和子模块名称一样的情况      
          
```xml
 <component name="yapi">
    <option name="moduleList">moduleName1,moduleName2</option>
  </component>

  <component name="moduleName1">
      <option name="moduleName1.Token">yapi 中项目token</option>
      <option name="moduleName1.Id">yapi 中项目id</option>
      <option name="moduleName1.Url">http://127.0.0.1:3000</option>
      <option name="moduleName1.Type">api</option>
      <option name="moduleName1.AttachUploadUrl">http://localhost/fileupload</option>
  </component>

```          

- 如果是dubbo 项目，选中dubbo interface 文件中的一个方法（要选中方法名称），右击YapiUpload(alt+u 快捷键)
- 如果是api 项目，选中controller 类中的方法名称或类名（要选中方法名称，或类名，选中类名为当前类所有接口都上传），右击YapiUpload(alt+u快捷键)




> 注意：接口会上传到一个临时文件目录下 tool-temp，根据需要移动



#### 使用规则

- 良好的java doc 注释能生成更好的文档，如下：生成的文档中属性就可以带上注释

```
/** 
* 年龄
*/
private Integer age;

```

生成的文档接口名称就可以使用：添加或更新课程数据,没有注释默认使用接口路径做为名称

```
    /**
     * 添加或更新课程数据
     *
     * @param courseOpt
     * @return {@link CommonRes}
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Course addOrUpdateCourse(@RequestBody CourseParam courseParam){
    
    }
    
    
    /**
     * @description: 添加或更新课程数据
     * @param: [CourseParam]
     * @return: Course
     * @date: 2018/3/15
     */
     @RequestMapping(value = "/test", method = RequestMethod.POST)
     public Course addOrUpdateCourse(@RequestBody CourseParam courseParam){
    
    }

```

- 支持@link 参量定义展示在字段备注中


```java

第一种@link 方式

/** 
* 状态 {@link com.xxx.constant.StatusConstant}
*/
private Integer status;


第二种@link 方式

import com.xxx.constant.StatusConstant;

/** 
* 状态 {@link StatusConstant}
*/
private Integer status;


不支持方式
import com.xxx.constant.*;

/** 
* 状态 {@link StatusConstant}
*/
private Integer status;

``` 

- 支持自定义分类 

通过在方法或类注释中加  @menu 注释实现，优先级 方法>类


```java 

/** 
 *@description: 用户控制器
 *@menu 这里填写类分类名称
 */   
@RestController
public class UserController {

    /**
     * @description: 新增用户
     * @param: [User]
     * @menu: 这里填写方法级别分类名称
     * @return: Response<UserDTO>
     * @date: 2018/3/15
     */
     @RequestMapping(value = "/addUser", method = RequestMethod.POST)
     public Response<UserDTO> addUser(@RequestBody User user){
    
    }
}

```




#### 注意点

- YapiUpload.iml 中 <module type="PLUGIN_MODULE" version="4">  type 必须为PLUGIN_MODULE 才能正常运行其代码
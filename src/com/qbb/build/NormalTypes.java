package com.qbb.build;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基本类
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/30 9:58 AM
 */
public class NormalTypes {

    @NonNls
    public static final Map<String, Object> normalTypes = new HashMap<>();

    public static final Map<String,Object> noramlTypesPackages=new HashMap<>();

    public static final Map<String,Object> collectTypes=new HashMap<>();

    public static final Map<String,Object> collectTypesPackages=new HashMap<>();
    /**
     * 泛型列表
     */
    public static final List<String> genericList=new ArrayList<>();


    static {
        normalTypes.put("int",1);
        normalTypes.put("boolean",false);
        normalTypes.put("byte",1);
        normalTypes.put("short",1);
        normalTypes.put("long",1L);
        normalTypes.put("float",1.0F);
        normalTypes.put("double",1.0D);
        normalTypes.put("char",'a');
        normalTypes.put("Boolean", false);
        normalTypes.put("Byte", 0);
        normalTypes.put("Short", Short.valueOf((short) 0));
        normalTypes.put("Integer", 0);
        normalTypes.put("Long", 0L);
        normalTypes.put("Float", 0.0F);
        normalTypes.put("Double", 0.0D);
        normalTypes.put("String", "String");
        normalTypes.put("Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        normalTypes.put("BigDecimal",0.111111);
        normalTypes.put("LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        normalTypes.put("LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        normalTypes.put("LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        normalTypes.put("Timestamp",new Timestamp(System.currentTimeMillis()));
        collectTypes.put("HashMap","HashMap");
        collectTypes.put("Map","Map");
        collectTypes.put("LinkedHashMap","LinkedHashMap");

        genericList.add("T");
        genericList.add("E");
        genericList.add("A");
        genericList.add("B");
        genericList.add("K");
        genericList.add("V");
    }

    static {
        noramlTypesPackages.put("int",1);
        noramlTypesPackages.put("boolean",true);
        noramlTypesPackages.put("byte",1);
        noramlTypesPackages.put("short",1);
        noramlTypesPackages.put("long",1L);
        noramlTypesPackages.put("float",1.0F);
        noramlTypesPackages.put("double",1.0D);
        noramlTypesPackages.put("char",'a');
        noramlTypesPackages.put("java.lang.Boolean",false);
        noramlTypesPackages.put("java.lang.Byte",0);
        noramlTypesPackages.put("java.lang.Short",Short.valueOf((short) 0));
        noramlTypesPackages.put("java.lang.Integer",1);
        noramlTypesPackages.put("java.lang.Long",1L);
        noramlTypesPackages.put("java.lang.Float",1L);
        noramlTypesPackages.put("java.lang.Double",1.0D);
        noramlTypesPackages.put("java.sql.Timestamp",new Timestamp(System.currentTimeMillis()));
        noramlTypesPackages.put("java.util.Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        noramlTypesPackages.put("java.lang.String","String");
        noramlTypesPackages.put("java.math.BigDecimal",1);
        noramlTypesPackages.put("java.time.LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        noramlTypesPackages.put("java.time.LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        noramlTypesPackages.put("java.time.LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        collectTypesPackages.put("java.util.LinkedHashMap","LinkedHashMap");
        collectTypesPackages.put("java.util.HashMap","HashMap");
        collectTypesPackages.put("java.util.Map","Map");
    }




    public static boolean isNormalType(String typeName) {
        return normalTypes.containsKey(typeName);
    }

    public static JsonObject formatMockType(String type){
        return formatMockType(type,null);
    }

    /**
     * mock type
     * @param type
     * @return
     */
    public static JsonObject formatMockType(String type,String exampleMock) {
        JsonObject mock = new JsonObject();

        //支持传入自定义mock
        if (StringUtils.isNotEmpty(exampleMock)) {
            mock.addProperty("mock", exampleMock);
            return mock;
        }
        if (type.equals("int")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("char")){
            mock.addProperty("mock", "@char");
        }else if (type.equals("Boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("Byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("Short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Integer")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("Double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("String")){
            mock.addProperty("mock", "@string");
        }else if (type.equals("Date")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("BigDecimal")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("LocalDate")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("LocalTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("LocalDateTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("Timestamp")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.lang.Boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("java.lang.Byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("java.lang.Short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Integer")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.lang.Double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.sql.Timestamp")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.util.Date")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.lang.String")){
            mock.addProperty("mock", "@string");
        }else if (type.equals("java.math.BigDecimal")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.time.LocalDate")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.time.LocalTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.time.LocalDateTime")){
            mock.addProperty("mock", "@timestamp");
        }else{
            mock.addProperty("mock", "mock");
        }
        return mock;
    }

}

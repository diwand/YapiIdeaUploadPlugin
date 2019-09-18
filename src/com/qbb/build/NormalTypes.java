package com.qbb.build;

import io.netty.util.internal.StringUtil;
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
    public static final Map<String, String> normalTypes = new HashMap<>();

    public static final Map<String,Object> noramlTypesPackages=new HashMap<>();

    public static final Map<String,Object> collectTypes=new HashMap<>();

    public static final Map<String,Object> collectTypesPackages=new HashMap<>();
    /**
     * 泛型列表
     */
    public static final List<String> genericList=new ArrayList<>();


    static {
        normalTypes.put("int","integer");
        normalTypes.put("boolean","boolean");
        normalTypes.put("byte","string");
        normalTypes.put("short","integer");
        normalTypes.put("long","integer");
        normalTypes.put("float","number");
        normalTypes.put("double","number");
        normalTypes.put("char","string");
        normalTypes.put("Boolean", "boolean");
        normalTypes.put("Byte", "string");
        normalTypes.put("Short", "integer");
        normalTypes.put("Integer", "integer");
        normalTypes.put("Long", "integer");
        normalTypes.put("Float", "number");
        normalTypes.put("Double", "number");
        normalTypes.put("String", "string");
        normalTypes.put("Date", "string");
        normalTypes.put("BigDecimal","number");
        normalTypes.put("LocalDate", "string");
        normalTypes.put("LocalTime", "string");
        normalTypes.put("LocalDateTime", "string");
        normalTypes.put("Timestamp","integer");
        collectTypes.put("HashMap","object");
        collectTypes.put("Map","object");
        collectTypes.put("LinkedHashMap","object");

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
        return normalTypes.containsKey(typeName) || noramlTypesPackages.containsKey(typeName);
    }

    public static String getYapiType(String typeName) {
        String type = normalTypes.get(typeName);
        return StringUtil.isNullOrEmpty(type)?typeName:type;
    }
}

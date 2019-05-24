package com.qbb.build;

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

        collectTypesPackages.put("java.util.LinkedHashMap","LinkedHashMap");
        collectTypesPackages.put("java.util.HashMap","HashMap");
        collectTypesPackages.put("java.util.Map","Map");
    }




    public static boolean isNormalType(String typeName) {
        return normalTypes.containsKey(typeName);
    }
}

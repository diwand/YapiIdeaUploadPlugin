package com.qbb.util;

import javax.annotation.Nullable;

/**
 * @author jiajixu
 * @date 2023/3/22 22:52
 */
public class StrUtil {
    public static boolean isEmpty(@Nullable String str) {
        if (str == null) return true;
        return str.equals("");
    }

    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(@Nullable String str) {
        if (str == null) return true;
        return str.trim().equals("");
    }

    public static boolean isNotBlank(@Nullable String str) {
        return !isBlank(str);
    }

}

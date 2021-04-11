package com.qbb.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @Author: chong.zhang
 * @Date: 2021-04-09 21:57:44
 */

public class ClipboardUtil {

    /**
     * @description: 设置到剪切板
     * @param: [content]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/3
     */
    public static void setClipboard(String content) {
        //获取系统剪切板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //构建String数据类型
        StringSelection selection = new StringSelection(content);
        //添加文本到系统剪切板
        clipboard.setContents(selection, null);
    }
}

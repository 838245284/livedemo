package com.myylook.common.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by cxf on 2019/6/20.
 */

public class LogUtil {

    public static void print(File file, String content) {
        if (file == null || TextUtils.isEmpty(content)) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void e(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize ) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        }else {
            while (msg.length() > segmentSize ) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize );
                msg = msg.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, msg);// 打印剩余日志
        }
    }
}

package com.gdin.lxx.mobileplayer.util;

import android.util.Log;

public class Logger {
    static boolean  isShowLog = true;

    public static void i(Object obj, String msg) {
        if (!isShowLog) {
            return;
        }

        String tag;
        if (obj instanceof String) {
            tag = (String) obj;
        } else if (obj instanceof Class) {
            Class clazz = (Class) obj;
            tag = clazz.getSimpleName();
        } else {
            tag = obj.getClass().getSimpleName();
        }

        Log.i(tag, msg);

    }

}

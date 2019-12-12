package com.tongchuang.phonelive.utils;

/**
 * Created by cxf on 2018/9/29.
 */

public class ClickUtil {

    private static long sLastClickTime;

    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - sLastClickTime < 500) {
            return false;
        }
        sLastClickTime = curTime;
        return true;
    }

}

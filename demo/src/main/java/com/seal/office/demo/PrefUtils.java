package com.seal.office.demo;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_CURRENT_PAGE = "current_page";

    // 保存当前页码
    public static void saveCurrentPage(Context context, int page) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_PAGE, page);
        editor.apply(); // 或者使用 commit() 如果需要立即写入
    }

    // 读取当前页码
    public static int getCurrentPage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_CURRENT_PAGE, 0); // 0 是默认值
    }
}

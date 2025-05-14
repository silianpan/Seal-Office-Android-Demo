package com.seal.office.demo;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seal.office.aar.api.ISealReaderCallback;
import com.seal.office.aar.api.SealOfficeEngineApi;

public class CustomActivity extends AppCompatActivity {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 横竖屏切换阅读视图宽高自适应
        SealOfficeEngineApi.setReadWidthHeight(CustomActivity.this, null, null);
//        SealOfficeEngineApi.setReadWidthHeight(CustomActivity.this, 800, 640);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        JSONObject params = new JSONObject();
        params.put("waterMarkText", "您好\n这是一个本地docx");
//        params.put("url", getFilesDir().getAbsolutePath() + File.separator + "test.docx");
        params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
        params.put("isDeleteFile", false);
        params.put("readViewWidth", 640);
        params.put("readViewHeight", 1200);
        params.put("menuItems", new JSONArray() {{
            add("下载");
            add("分享");
        }});
        // 第一种方式：通过xml获取自定义FrameLayout
        FrameLayout customFrameLayout = findViewById(R.id.custom_frame_layout_custom);

        // 第二种方式：动态创建FrameLayout
        // 1. 创建一个 FrameLayout 对象
//        FrameLayout customFrameLayout = new FrameLayout(MainActivity.this);
//        customFrameLayout.setBackgroundColor(Color.WHITE);
//        // 2. 设置 FrameLayout 的布局参数
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(800,1000);
//        customFrameLayout.setLayoutParams(layoutParams);
//        // 3. 将 FrameLayout 添加到父视图
//        ViewGroup viewLayout = findViewById(R.id.view_root);
//        viewLayout.addView(customFrameLayout);

        SealOfficeEngineApi.openFile(CustomActivity.this, customFrameLayout, params, new ISealReaderCallback() {
            @Override
            public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                Log.e("打开文件URL：" + code, msg);
                if (code == 1010) {
                    // 页面返回，删除布局，避免重复添加
//                    viewLayout.removeView(customFrameLayout);
                }
            }

            @Override
            public void menuClick(Context context, JSONObject jsonObject) {
                Toast.makeText(CustomActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

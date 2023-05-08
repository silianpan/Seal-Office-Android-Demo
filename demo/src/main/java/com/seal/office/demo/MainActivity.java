package com.seal.office.demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.seal.office.api.SealOfficeEngineApi;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 插件初始化
        SealOfficeEngineApi.initEngine(MainActivity.this);

        // 参数传递
        JSONObject params = new JSONObject();
        params.put("waterMarkText", "你好，世界\n准备好了吗？时刻准备着");

        // docx
        findViewById(R.id.open_docx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
                SealOfficeEngineApi.openFile(MainActivity.this, params);
            }
        });

        // xlsx
        findViewById(R.id.open_xlsx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.xlsx");
                SealOfficeEngineApi.openFile(MainActivity.this, params);
            }
        });

        // pptx
        findViewById(R.id.open_pptx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.pptx");
                SealOfficeEngineApi.openFile(MainActivity.this, params);
            }
        });

        // pdf
        findViewById(R.id.open_pdf_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/2.pdf");
                SealOfficeEngineApi.openFile(MainActivity.this, params);
            }
        });

        // jpg
        findViewById(R.id.open_jpg_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("imageUrls", new String[]{"http://silianpan.cn/upload/2022/01/01/1.jpg", "http://silianpan.cn/upload/2022/01/01/1.png"});
                params.put("isSaveImg", true);
                SealOfficeEngineApi.openFileImage(MainActivity.this, params);
            }
        });

        // mp3
        findViewById(R.id.open_mp3_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp3");
                SealOfficeEngineApi.openFileVideo(MainActivity.this, params);
            }
        });

        // mp4
        findViewById(R.id.open_mp4_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp4");
                SealOfficeEngineApi.openFileVideo(MainActivity.this, params);
            }
        });
    }
}
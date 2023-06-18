package com.seal.office.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.seal.office.aar.api.ISealReaderCallback;
import com.seal.office.aar.api.SealOfficeEngineApi;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 插件初始化
        SealOfficeEngineApi.initEngine(MainActivity.this, new ISealReaderCallback() {
            @Override
            public void callback(int code, String msg) {
                Log.e("" + code, msg);
            }
        });

        // 获取版本号
        String versionCode = SealOfficeEngineApi.getVersion(MainActivity.this);
        Log.d("SealOffice版本号：", versionCode);

        // 参数传递
        JSONObject params = new JSONObject();
        params.put("waterMarkText", "你好，世界\n准备好了吗？时刻准备着");
        params.put("isDeleteFile", false);

        // 文件url
        findViewById(R.id.open_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fileUrlText = findViewById(R.id.file_url);
                if (!TextUtils.isEmpty(fileUrlText.getText())) {
                    params.put("url", fileUrlText.getText());
                }
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("打开文件URL：" + code, msg);
                    }
                });
            }
        });

        // docx
        findViewById(R.id.open_docx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("打开docx：" + code, msg);
                    }
                });
            }
        });

        // 嵌入打开docx
        findViewById(R.id.open_docx_emed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout customContainer = findViewById(R.id.custom_container);
                JSONObject params = new JSONObject(2);
                params.put("filePath", "/data/data/com.seal.office.demo/files/1.docx");
                params.put("readViewWidth", 1000);
                SealOfficeEngineApi.openFile(MainActivity.this, customContainer, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("嵌入打开docx：" + code, msg);
                    }
                });
            }
        });

        // xlsx
        findViewById(R.id.open_xlsx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.xlsx");
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("打开xlsx：" + code, msg);
                    }
                });
            }
        });

        // pptx
        findViewById(R.id.open_pptx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.pptx");
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("打开pptx：" + code, msg);
                    }
                });
            }
        });

        // pdf
        findViewById(R.id.open_pdf_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("url", "https://static.gongkaoleida.com/2021/file/download/2021湖南省公务员考试《报考指导手册》.pdf");
//                params.put("fileName", "2021湖南省公务员考试《报考指导手册》");
//                params.put("fileType", "pdf");
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("打开pdf：" + code, msg);
                    }
                });
            }
        });

        // jpg
        findViewById(R.id.open_jpg_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("imageUrls", new String[]{"http://silianpan.cn/upload/2022/01/01/1.jpg", "http://silianpan.cn/upload/2022/01/01/1.png"});
                params.put("isSaveImg", true);
                SealOfficeEngineApi.openFileImage(MainActivity.this, params);
            }
        });

        // mp3
        findViewById(R.id.open_mp3_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp3");
                SealOfficeEngineApi.openFileVideo(MainActivity.this, params);
            }
        });

        // mp4
        findViewById(R.id.open_mp4_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp4");
                SealOfficeEngineApi.openFileVideo(MainActivity.this, params);
            }
        });

        // WPS编辑
        findViewById(R.id.open_wps_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
                params.put("openMode", "EditMode");
                SealOfficeEngineApi.openFileWPS(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        Log.e("WPS编辑：" + code, msg);
                    }
                });
            }
        });
    }
}
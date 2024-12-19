package com.seal.office.demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seal.image.aar.api.SealImageEngineApi;
import com.seal.office.aar.api.ISealReaderCallback;
import com.seal.office.aar.api.SealOfficeEngineApi;
import com.seal.video.aar.api.ISealImageCallback;
import com.seal.video.aar.api.SealVideoEngineApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    // 当前页码
    private int mCurPage;
    // 总页码
    private int mPageCount;

    private void gotoPage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("输入页码，当前页码/总页码：" + mCurPage + "/" + mPageCount);

        // 创建一个水平的 LinearLayout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // 创建一个 EditText 并设置为数字输入类型
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // 设置宽度为 0，并使其权重为 1

        // 创建一个 TextView 显示当前页码/总页码
        final TextView pageInfo = new TextView(context);
        pageInfo.setText(String.format("%d/%d", mCurPage, mPageCount));
        pageInfo.setPadding(10, 0, 0, 0); // 添加一些左侧填充

        // 将 EditText 和 TextView 添加到布局中
        layout.addView(input);
        layout.addView(pageInfo);

        // 设置布局为对话框的视图
        builder.setView(layout);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pageNumber = input.getText().toString();
                Toast.makeText(context, "输入的页码是: " + pageNumber, Toast.LENGTH_SHORT).show();

                try {
                    int page = Integer.parseInt(pageNumber);
                    SealOfficeEngineApi.gotoPage(page);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "无效的页码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // 显示对话框
        builder.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyAssetsFileToAppFiles("file" + File.separator + "test.docx", "test.docx");
        SealOfficeEngineApi.initLicenseFile("product/seal-office.license");

        // 插件初始化
        SealOfficeEngineApi.initEngine(MainActivity.this, new ISealReaderCallback() {
            @Override
            public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                Log.e(TAG, code + ", " + msg);
            }

            @Override
            public void menuClick(Context context, JSONObject jsonObject) {

            }
        });

        // 获取版本号
        String versionCode = SealOfficeEngineApi.getVersion(MainActivity.this);
        Log.d("SealOffice版本号：", versionCode);

        // 参数传递
        JSONObject params = new JSONObject();
        params.put("waterMarkText", "你好，世界\n准备好了吗？时刻准备着");
        // 指定跳转页码
//        params.put("targetPage", 5);
        params.put("menuItems", new JSONArray() {{
            add("下载");
            add("分享");
            add("页码跳转");
        }});

        // 文件url
        findViewById(R.id.open_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fileUrlText = findViewById(R.id.file_url);
                EditText fileTypeText = findViewById(R.id.file_type);
                EditText fileNameText = findViewById(R.id.file_name);
                if (!TextUtils.isEmpty(fileUrlText.getText())) {
                    params.put("url", fileUrlText.getText());
                }
                if (!TextUtils.isEmpty(fileTypeText.getText())) {
                    params.put("fileType", fileTypeText.getText());
                }
                if (!TextUtils.isEmpty(fileNameText.getText())) {
                    params.put("fileName", fileNameText.getText());
                }
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开文件URL：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 自定义FrameLayout预览
        findViewById(R.id.open_custom_frame_layout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                JSONObject params = new JSONObject();
//                params.put("waterMarkText", "您好\n这是一个本地docx");
//                params.put("url", getFilesDir().getAbsolutePath() + File.separator + "test.docx");
//                params.put("isDeleteFile", false);
//                params.put("menuItems", new JSONArray() {{
//                    add("下载");
//                    add("分享");
//                }});
//                // 第一种方式：通过xml获取自定义FrameLayout
////                FrameLayout customFrameLayout = findViewById(R.id.custom_frame_layout);
//
//                // 第二种方式：动态创建FrameLayout
//                // 1. 创建一个 FrameLayout 对象
//                FrameLayout customFrameLayout = new FrameLayout(MainActivity.this);
//                customFrameLayout.setBackgroundColor(Color.WHITE);
//                // 2. 设置 FrameLayout 的布局参数
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(800,1000);
//                customFrameLayout.setLayoutParams(layoutParams);
//                // 3. 将 FrameLayout 添加到父视图
//                ViewGroup viewLayout = findViewById(R.id.view_root);
//                viewLayout.addView(customFrameLayout);
//                SealOfficeEngineApi.openFile(MainActivity.this, customFrameLayout, params, new ISealReaderCallback() {
//                    @Override
//                    public void callback(int code, String msg) {
//                        Log.e(TAG, "打开文件URL：" + code, msg);
//                        if (code == 1010) {
//                            // 页面返回，删除布局，避免重复添加
//                            viewLayout.removeView(customFrameLayout);
//                        }
//                    }
//
//                    @Override
//                    public void menuClick(JSONObject jsonObject) {
//                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                Intent intent = new Intent(MainActivity.this, CustomActivity.class);
                // 可选：传递数据给DynamicActivity
                intent.putExtra("key", "Hello, DynamicActivity!");
                startActivity(intent);
            }
        });

        // 打开对话框预览文档
        findViewById(R.id.open_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建对话框
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_custom);

                // 设置对话框宽度和高度
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);

                // 在对话框布局中找到自定义的 FrameLayout 和关闭按钮
                FrameLayout customFrameLayout = dialog.findViewById(R.id.custom_frame_layout_custom);
                Button closeButton = dialog.findViewById(R.id.close_dialog_btn);

                // 关闭按钮的操作
                closeButton.setOnClickListener(view -> dialog.dismiss());

                JSONObject params = new JSONObject();
                params.put("waterMarkText", "您好\n这是一个本地docx");
                params.put("url", getFilesDir().getAbsolutePath() + File.separator + "test.docx");
                params.put("isDeleteFile", false);
                // 指定跳转页码
//                params.put("targetPage", 5);
                params.put("menuItems", new JSONArray() {{
                    add("下载");
                    add("分享");
                }});
                SealOfficeEngineApi.openFile(MainActivity.this, customFrameLayout, params, new ISealReaderCallback() {
                    @Override
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开文件URL：" + code + ", " + msg);
                        if (code == 1010) {
                            // 页面返回，删除布局，避免重复添加
                            customFrameLayout.removeAllViews();
                            dialog.dismiss();
                        }
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }
                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                    }
                });

                // 显示对话框
                dialog.show();
            }
        });

        // 本地docx
        findViewById(R.id.open_local_docx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("waterMarkText", "您好\n这是一个本地docx");
                params.put("url", getFilesDir().getAbsolutePath() + File.separator + "test.docx");
                params.put("isDeleteFile", false);
                // 指定跳转页码
//                params.put("targetPage", 5);
                params.put("menuItems", new JSONArray() {{
                    add("下载");
                    add("分享");
                    add("跳转页码");
                }});
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开本地docx：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                        gotoPage(context);
                    }
                });
            }
        });

        // docx
        findViewById(R.id.open_docx_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("waterMarkText", "您好\n这是一个在线docx");
                params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
                // 指定跳转页码
//                params.put("targetPage", 5);
                params.put("menuItems", new JSONArray() {{
                    add("下载");
                    add("分享");
                    add("跳转页码");
                }});
                SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
                    @Override
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开在线docx：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                        gotoPage(context);
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
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开在线xlsx：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                        gotoPage(context);
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
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开在线pptx：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                        gotoPage(context);
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
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "打开在线pdf：" + code + ", " + msg);
                        if (code == 1011) {
                            int curPage = jsonObject.getIntValue("curPage");
                            int pageCount = jsonObject.getIntValue("pageCount");
                            mCurPage = curPage;
                            mPageCount = pageCount;
                            Log.e(TAG, "当前页码/总页码：" + curPage + "/" + pageCount);
                        }
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                        gotoPage(context);
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
                SealImageEngineApi.openFileImage(MainActivity.this, params);
            }
        });

        // mp3
        findViewById(R.id.open_mp3_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp3");
                params.put("menuItems", new JSONArray() {{
                    add("下载");
                    add("分享");
                }});
                SealVideoEngineApi.openFileVideo(MainActivity.this, params, new ISealImageCallback() {
                    @Override
                    public void callback(int i, String s) {

                    }

                    @Override
                    public void menuClick(JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // mp4
        findViewById(R.id.open_mp4_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp4");
                params.put("menuItems", new JSONArray() {{
                    add("下载");
                    add("分享");
                }});
                SealVideoEngineApi.openFileVideo(MainActivity.this, params, new ISealImageCallback() {
                    @Override
                    public void callback(int i, String s) {

                    }

                    @Override
                    public void menuClick(JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this, jsonObject.toJSONString(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                    public void callback(Context context, int code, String msg, JSONObject jsonObject) {
                        Log.e(TAG, "WPS编辑：" + code + ", " + msg);
                    }

                    @Override
                    public void menuClick(Context context, JSONObject jsonObject) {

                    }
                });
            }
        });
    }

    /**
     * 从assets目录中复制某文件内容
     *
     * @param assetFileName assets目录下的文件
     * @param newFileName   复制到/data/data/package_name/files/目录下文件名
     */
    private void copyAssetsFileToAppFiles(String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = this.getAssets().open(assetFileName);
            fos = this.openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[1024];
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
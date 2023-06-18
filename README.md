## Seal-OfficeOnline文档预览插件原生Android项目集成方式

### 一、介绍

Seal-OfficeOnline是跨平台Office文档预览原生插件，具有以下特点：

* 支持UniApp项目集成，也支持原生Android项目集成
* 非腾讯X5，无内核加载，高效率、稳定高可用
* 支持在线文档，也支持离线设备本地文档
* 支持Android和IOS
* 支持全屏预览，也支持组件嵌入方式预览
* 支持WPS应用预览或编辑文档
* 支持水印、防截屏、自定义状态栏、保存图片等诸多可选配置
* 支持docx、pptx、xlsx等多种office文档格式，也支持常用图片（jpg、png、bmp等）和音视频格式（mp3、flac、wma、mp4、mkv等）
* 支持wps、doc、docx、xls、xlsx、csv、ppt、pptx、txt、properties、log、Log、ini、lua、conf、m、cpp、java、h、xml、html、htm等常见文档格式

### 二、UniApp项目集成方式

UniApp项目集成方式，参考：[插件市场](https://ext.dcloud.net.cn/plugin?id=3226)。

本文主要是说明原生Android集成方式。

### 三、原生Android项目集成方式

#### 1、Demo工程地址

[Demo工程](https://github.com/silianpan/Seal-Office-Android-Demo)

#### 2、联系客服获取License授权

* 更改应用包名，即applicationId

* 创建src/main/assets/seal-office.license文件，并添加授权码

* 扫下面二维码添加WX，联系客服获取授权码；或者添加QQ号（2480621579）

<img src="http://silianpan.cn/upload/2022/01/01/Seal-UniPlugin-WeiXin-Me.jpg" width="240" style="width:240px;" />

#### 3、添加依赖

```java
// 不需要图片预览，删除libs/ImagePreview-release.aar包
implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])

// ================= SealOffice文档预览需要添加的依赖包 begin ==============
implementation 'androidx.recyclerview:recyclerview:1.0.0'
implementation 'androidx.legacy:legacy-support-v4:1.0.0'
implementation 'androidx.appcompat:appcompat:1.2.0'
implementation 'com.alibaba:fastjson:1.2.83'
implementation 'com.facebook.fresco:fresco:1.13.0'
implementation 'com.google.android.material:material:1.3.0'
implementation "com.github.bumptech.glide:glide:4.9.0"
implementation "androidx.constraintlayout:constraintlayout:2.1.3"

// ============ 音视频播放，不需要直接去掉 begin ==========
implementation 'xyz.doikki.android.dkplayer:dkplayer-java:3.3.7'
implementation 'xyz.doikki.android.dkplayer:dkplayer-ui:3.3.7'
// ============ 音视频播放，不需要直接去掉 end ============

implementation 'net.lingala.zip4j:zip4j:2.11.5'
// ================= SealOffice文档预览需要添加的依赖包 end ================
```

#### 4、插件初始化（在应用启动时进行调用）

```java
SealOfficeEngineApi.initEngine(MainActivity.this, new ISealReaderCallback() {
  @Override
  public void callback(int code, String msg) {
    Log.e("" + code, msg);
  }
});
```

#### 5、接口调用

#### 注意：接口参数请参考：[五、openFile接口参数说明](https://github.com/silianpan/Seal-UniPlugin-Demo)

##### （1）打开Office文档（支持wps、doc、docx、xls、xlsx、csv、ppt、pptx、txt、properties、log、Log、ini、lua、conf、m、cpp、java、h、xml、html、htm等常见文档格式）

```java
JSONObject params = new JSONObject();
params.put("waterMarkText", "你好，世界\n准备好了吗？时刻准备着");
params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
params.put("isDeleteFile", false);
SealOfficeEngineApi.openFile(MainActivity.this, params, new ISealReaderCallback() {
  @Override
  public void callback(int code, String msg) {
    Log.e("" + code, msg);
  }
});
```

##### （2）打开Office文档（嵌入方式，自定义界面）

* 接口一：传入字符串参数，文档本地绝对路径

```java
/**
* 打开Office文档（嵌入方式，自定义界面）
* @param activity Activity对象
* @param frameLayout FrameLayout对象
* @param filePath 文档本地绝对路径
*/
FrameLayout customContainer = findViewById(R.id.custom_container);
SealOfficeEngineApi.openFile(activity, frameLayout, filePath, new ISealReaderCallback() {
  @Override
  public void callback(int code, String msg) {
    Log.e("" + code, msg);
  }
});
```

* 接口二：传入参数对象

```java
/**
* 打开Office文档（嵌入方式，自定义界面）
* @param activity Activity对象
* @param frameLayout FrameLayout对象
* @param params JSONObject对象
*    （1）filePath String（必传） 文档本地绝对路径
* 	 （2）readViewWidth Integer（可选） 阅读视图宽度，可以根据屏幕大小计算传入
*    （3）readViewHeight Integer（可选） 阅读视图高度，可以根据屏幕大小计算传入，建议不传
*/
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
```



##### （3）添加水印

```java
/**
* 添加水印
* @param context 上下文对象
* @param frameLayout FrameLayout对象
* @param waterMarkText 水印文本，以“\n”作为分隔符换行
* @param waterMarkDegree 水印旋转角度，默认为-30（逆时针30度）
* @param waterMarkFontSize 水印字体大小，单位为sp，默认为13
* @param waterMarkFontColor 水印字体颜色，默认为浅灰色（#40F3F5F9）
*/
SealOfficeEngineApi.addWaterMark(context, frameLayout, waterMarkText, waterMarkDegree, waterMarkFontSize, waterMarkFontColor);
```

##### （4）打开图片（jpg、png、jpeg、bmp等）

```java
JSONObject params = new JSONObject();
params.put("imageUrls", new String[]{"http://silianpan.cn/upload/2022/01/01/1.jpg", "http://silianpan.cn/upload/2022/01/01/1.png"});
params.put("isSaveImg", true);
SealOfficeEngineApi.openFileImage(MainActivity.this, params);
```

##### （5）打开音视频（mp3、flac、wma、wav、mp4、mkv等）

```java
JSONObject params = new JSONObject();
params.put("videoUrl", "http://silianpan.cn/upload/2022/01/01/1.mp4");
SealOfficeEngineApi.openFileVideo(MainActivity.this, params);
```

##### （6）WPS预览或编辑

```java
JSONObject params = new JSONObject();
params.put("url", "http://silianpan.cn/upload/2022/01/01/1.docx");
SealOfficeEngineApi.openFileWPS(MainActivity.this, params, new ISealReaderCallback() {
  @Override
  public void callback(int code, String msg) {
    Log.e("" + code, msg);
  }
});
```

##### （7）检查WPS是否安装

```java
boolean hasWps = SealOfficeEngineApi.checkWps(MainActivity.this);
```

#### 四、回调结果状态码说明

| 状态码 | 说明                           |
| ------ | ------------------------------ |
| 1      | 初始化引擎成功                 |
| 2      | 文件预览成功                   |
|        |                                |
| -1     | 初始化引擎失败                 |
| -2     | 初始化引擎失败                 |
| -3     | 授权失败，请联系客服重新授权   |
| -4     | 请联系客服授权包名             |
| -5     | 授权已过期，请联系客服重新授权 |
| -6     | 参数异常，加载文件失败         |
| -7     | 不支持文件类型                 |
| -8     | 文件不存在                     |
| -9     | 文件预览失败                   |
|        |                                |
| 301    | 文档下载失败                   |
| 1001   | 文档下载成功                   |
| 1008   | 缓存文档删除成功               |
| 1010   | 页面返回                       |

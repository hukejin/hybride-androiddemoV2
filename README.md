# android端使用混合开发框架库例子

### 调用的API文档地址
#### [API文档](https://hukejin.github.io/hybridelibrary-android/)

### 开发准备
1. 在工程的build.gradle里引入库地址和架包
    ```
    repositories 
    {
        maven { url "http://192.168.1.14:8081/nexus/content/repositories/snapshots/" }
    }
    implementation 'com.hesc.android.library:HybrideLibrary:1.0.1-SNAPSHOT'
    ```

2. 创建activity，继承cn.com.hesc.library.HybrideBaseActivity，并实现所有的接口方法
3. 以上方法包含了https://hukejin.github.io/hybridelibrary-android/里的所有功能，可按实际业务进行实现
4. 函数的功能请查看上述文档里的描述
5. 通过layout或者代码引入cn.com.hesc.library.ExtendsWebView
6. 对ExtendsWebView进行相关设置，以下代码可以直接拷贝使用

    ```
    webView.setWebViewClient(new WebViewClientSelf(this, new WebViewClientSelf.HttpStatusListener() {
             @Override
             public void onSuccess() {
                 //页面加载完成回调
             }
    
             @Override
             public void onError(int code) {
                 //页面加载失败回调，如404，5XX等
             }
         }));
         webView.setWebChromeClient(new WebChromeClient(){
             @Override
             public void onProgressChanged(WebView view, final int newProgress) {
                 //页面加载进度，newProgress进度值 0-100
             }
                
             /** 支持input type=file时的文件选择 */
             protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
             {
                 mUploadMessage = uploadMsg;
                 Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                 i.addCategory(Intent.CATEGORY_OPENABLE);
                 i.setType("*/*");
                 startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
             }
             // For Lollipop 5.0+ Devices
             @TargetApi(Build.VERSION_CODES.LOLLIPOP)
             public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
             {
                 if (uploadMessage != null) {
                     uploadMessage.onReceiveValue(null);
                     uploadMessage = null;
                 }
                 uploadMessage = filePathCallback;
                 Intent intent = fileChooserParams.createIntent();
                 try
                 {
                     startActivityForResult(intent, REQUEST_SELECT_FILE);
                 } catch (ActivityNotFoundException e)
                 {
                     uploadMessage = null;
                     Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                     return false;
                 }
                 return true;
             }
             //For Android 4.1 only
             protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
             {
                 mUploadMessage = uploadMsg;
                 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                 intent.addCategory(Intent.CATEGORY_OPENABLE);
                 intent.setType("*/*");
                 startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
             }
    
             protected void openFileChooser(ValueCallback<Uri> uploadMsg)
             {
                 mUploadMessage = uploadMsg;
                 Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                 i.addCategory(Intent.CATEGORY_OPENABLE);
                 i.setType("*/*");
                 startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
             }
         });
         /** 建立JS桥接功能 */
         webView.addJavascriptInterface(new JavaScriptInterface(this),"Native");
    
    ```
7. JS通信统一采用,MessageHandler作为消息载体，res可放入任何对象

    ```
    协议结构体
    cn.com.hesc.library.MessageHandler{
        String callbackId = "";//JS生成的唯一标识码,不填写即为原生主动调用JS功能   
        Object res;//调用原生功能结束后的返回值对象   --如失败将失败调试信息作为字符串返回
        boolean success = true;//原生功能调用成功   false失败   
        boolean running = false;//只适用于上传图片、下载文件时设置为true   
    }
    回调给JS必须调用此方法
    webView.excuteJs(messageHandler);
    ```
8. 在图片上传时，会根据JS里设定的是否优先返回缩略图进行相关操作，请开发人员用到时仔细阅读例子代码
9. 例子代码只为演示所有效果，并不满足所有业务，如果遇到定制业务请自行开发
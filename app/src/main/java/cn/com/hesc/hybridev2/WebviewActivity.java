package cn.com.hesc.hybridev2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.com.hesc.audiolibrary.audio.RecordVoice;
import cn.com.hesc.audiolibrary.audio.VoiceUtils;
import cn.com.hesc.gpslibrary.model.GpsForUser;
import cn.com.hesc.hybridev2.utils.BaseFile;
import cn.com.hesc.hybridev2.utils.WebRequest;
import cn.com.hesc.library.ExtendsWebView;
import cn.com.hesc.library.HybrideBaseActivity;
import cn.com.hesc.library.JavaScriptInterface;
import cn.com.hesc.library.MessageHandler;
import cn.com.hesc.library.WebViewClientSelf;
import cn.com.hesc.library.entity.CaptureBean;
import cn.com.hesc.library.entity.ConfirmBean;
import cn.com.hesc.library.entity.DateBean;
import cn.com.hesc.library.entity.DeviceInfoBean;
import cn.com.hesc.library.entity.DownLoadBean;
import cn.com.hesc.library.entity.LocationBean;
import cn.com.hesc.library.entity.MultiConfirmBean;
import cn.com.hesc.library.entity.NetWorkBean;
import cn.com.hesc.library.entity.ScanBean;
import cn.com.hesc.library.entity.SignalConfirmBean;
import cn.com.hesc.library.entity.SoftVersionBean;
import cn.com.hesc.library.entity.StorageBean;
import cn.com.hesc.library.entity.UUIDBean;
import cn.com.hesc.library.entity.VoiceBean;
import cn.com.hesc.maplibrary.MapActivity;
import cn.com.hesc.maplibrary.model.MapExtent;
import cn.com.hesc.maplibrary.view.iMapView;
import cn.com.hesc.materialdialogs.HescMaterialDialog;
import cn.com.hesc.picture.Camera_VideoActivity;
import cn.com.hesc.picture.MultiePreViewActivity;
import cn.com.hesc.picture.multiplepic.MultiplePicActivity;
import cn.com.hesc.request.HttpRequest;
import cn.com.hesc.tools.BitMapUtils;
import cn.com.hesc.tools.DeviceInfo;
import cn.com.hesc.tools.DisplayUtils;
import cn.com.hesc.tools.Media2Base64Util;
import cn.com.hesc.tools.OpenActivityUtils;
import cn.com.hesc.tools.SdcardInfo;
import cn.com.hesc.tools.SoftVersion;
import cn.com.hesc.tools.TimeUtils;
import cn.com.hesc.tools.ToastUtils;
import cn.com.hesc.utils.DownLoadUtils;
import cn.com.hesc.utils.HttpWebUtils;
import cn.com.hesc.zxinglibrary.android.CaptureActivity;
import cn.com.hesc.zxinglibrary.bean.ZxingConfig;
import cn.com.hesc.zxinglibrary.common.Constant;

public class WebviewActivity extends HybrideBaseActivity implements OnDateSetListener, View.OnClickListener {

    private String url;
    /**标题栏相关设置***************************************/
    private ProgressBar loadingProgress;
    private TextView navbacktv,navtitle,navright;
    private boolean filterLeft = false,filterRight = false;
    private MessageHandler leftMessageHandler,rightMessageHandler;
    private LinearLayout navline;
    /*******************************************************/
    private ExtendsWebView webview;
    private ImageView reload;
    /**实现 input type=file******************************/
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 101;
    public static final int REQUEST_CAPTURE_FILE = 102;
    /*******************************************************/
    /***日期相关*/
    private String dateFormat;//日历格式
    private MessageHandler dateMessageHandler;
    TimePickerDialog mDialogAll;//年月日时分
    /*******************************************************/
    /**扫码功能*/
    private MessageHandler scanMessageHandler;
    private final static int READERCODE = 0;
    /******************************************************/
    /**数据存储功能*/
    private SharedPreferences sharedPreferences;
    /******************************************************/
    /**GPS信息--地图功能*/
    private GpsForUser gpsForUser;
    private static String localMapKEY;
    private MessageHandler mapMessageHandler;
    private final static int OPEMMAP = 3;
    /******************************************************/
    /**拍照相关功能**/
    private String mediaUrl;
    private MessageHandler captureMessageHandler;
    private static final int TAKE_PICTURE = 4;
    private Uri imageUri;
    /*******************************************************/
    /***选择图片**/
    private MessageHandler multiPictureMessageHandler;
    private static final int MULTIPLE_CHOISE = 5;
    private static final int PREVIEW_PICTURE = 6;
    /*******************************************************/
    /**录音相关***/
    private static RecordVoice mRecordVoice;
    private static VoiceUtils mVoiceUtils;
    /*******************************************************/
    /**input file 涉及到的选择操作*/
    private FrameLayout mediatoolbar;
    private TextView tocapture,tomediachoose;
    private ImageView devider;
    /*********************************************************/

    private DisplayUtils displayUtils;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        sharedPreferences = getSharedPreferences("WebViewExample",0);

        context = this;

        gpsForUser = GpsForUser.getInstance(this);

        displayUtils = new DisplayUtils(this);

        url = getIntent().getExtras().getString("url");
        initView();
    }

    private void initView() {
        navline = (LinearLayout)findViewById(R.id.navline);
        navbacktv = (TextView)findViewById(R.id.navbacktv);
        navbacktv.setOnClickListener(this);
        navtitle = (TextView)findViewById(R.id.navtitle);
        navright = (TextView)findViewById(R.id.navright);
        navright.setOnClickListener(this);
        loadingProgress = (ProgressBar)findViewById(R.id.webprogress);
        webview = (ExtendsWebView)findViewById(R.id.webview);
        reload = (ImageView)findViewById(R.id.reload);
        reload.setOnClickListener(this);
        webviewSetting();
        webview.loadUrl(url);
        mediatoolbar = (FrameLayout) findViewById(R.id.mediatoolbar);
        mediatoolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                mediatoolbar.setVisibility(View.GONE);
            }
        });
        tocapture = (TextView)findViewById(R.id.tocapture);
        tocapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediatoolbar.setVisibility(View.GONE);
                //打开照相机
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = getOutputMediaFileUri(WebviewActivity.this);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                //Android7.0添加临时权限标记，此步千万别忘了
                openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(openCameraIntent, REQUEST_CAPTURE_FILE);
            }
        });
        tomediachoose = (TextView)findViewById(R.id.tomediachoose);
        tomediachoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(WebviewActivity.this, MultiplePicActivity.class);
                Bundle bl = new Bundle();
                bl.putInt("piccount",3);
                it.putExtras(bl);
                startActivityForResult(it,REQUEST_SELECT_FILE);
            }
        });
        devider = (ImageView)findViewById(R.id.devider);
    }

    /**
     * 可根据业务需求，是否加标题栏等进行设置拷贝
     */
    private void webviewSetting() {
        webview.setWebViewClient(new WebViewClientSelf(this, new WebViewClientSelf.HttpStatusListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingProgress.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reload.setVisibility(View.VISIBLE);
                    }
                });
            }
        }));
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingProgress.setProgress(newProgress);
                        if(newProgress == 100) {
                            loadingProgress.setVisibility(View.GONE);
                            webview.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            //实现 input type=file的做法 可直接拷贝 Android  >= 3.0
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }
            // For Lollipop 5.0+ Devices 实现 input type=file的做法 可直接拷贝 Android >= 5.0
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;


                mediatoolbar.setVisibility(View.VISIBLE);
                if(fileChooserParams.isCaptureEnabled()){
                    tocapture.setVisibility(View.VISIBLE);
                }else{
                    tocapture.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }

                if(fileChooserParams.getMode() == 1){
                    tomediachoose.setVisibility(View.VISIBLE);
                }else{
                    tomediachoose.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }


                //添加相机和相册的支持
//                final HescMaterialDialog materialDialog = new HescMaterialDialog(WebviewActivity.this);
//                materialDialog.showSignalDialog("请选择上传方式", "确定", "取消", new String[]{"拍照", "相册"}, 0, new HescMaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onPositive(HescMaterialDialog dialog) {
//                        super.onPositive(dialog);
//                        String item = materialDialog.getMultiItems().get(0);
//                        if("拍照".equals(item)){
//                            //打开照相机
//                            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            imageUri = getOutputMediaFileUri(WebviewActivity.this);
//                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//                            //Android7.0添加临时权限标记，此步千万别忘了
//                            openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            startActivityForResult(openCameraIntent, REQUEST_CAPTURE_FILE);
//                        }else{
//                            Intent intent = fileChooserParams.createIntent();
//                                try
//                                {
//                                    startActivityForResult(intent, REQUEST_SELECT_FILE);
//                                }
//                                catch (ActivityNotFoundException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                        }
//                    }
//
//                    @Override
//                    public void onNegative(HescMaterialDialog dialog) {
//                        super.onNegative(dialog);
//                    }
//                });

//                Intent intent = fileChooserParams.createIntent();
//                try
//                {
//                    startActivityForResult(intent, REQUEST_SELECT_FILE);
//                } catch (ActivityNotFoundException e)
//                {
//                    uploadMessage = null;
//                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
//                    return false;
//                }
                return true;
            }
            //For Android 4.1 only 实现 input type=file的做法 可直接拷贝 Android  >= 4.1
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            //实现 input type=file的做法 可直接拷贝 Android < 3.0
            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
        webview.addJavascriptInterface(new JavaScriptInterface(this),"Native");
    }

    public static Uri getOutputMediaFileUri(Context context) {
        File mediaFile = null;
        String cameraPath;
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath()
                    + File.separator
                    + "Pictures/temp.jpg");//注意这里需要和filepaths.xml中配置的一样
            cameraPath = mediaFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider",//与清单文件中android:authorities的值保持一致
                    mediaFile);//FileProvider方式或者ContentProvider。也可使用VmPolicy方式
            return contentUri;

        } else {
            return Uri.fromFile(mediaFile);//或者 Uri.isPaise("file://"+file.toString()

        }
    }

    /**
     * 获取android容器版本信息
     * @param messageHandler
     */
    @Override
    protected void getSoftVersion(MessageHandler messageHandler) {
        SoftVersionBean softVersionBean = new SoftVersionBean();
        softVersionBean.setVersionCode(new SoftVersion(this).getVersioncode());
        softVersionBean.setVersionName(new SoftVersion(this).getVersionName());
        messageHandler.setRes(softVersionBean);
        webview.excuteJs(messageHandler);
    }

    /***
     * alert使用方法
     * @param messageHandler
     * @param params
     */
    @Override
    protected void dialogAlert(final MessageHandler messageHandler, String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            HescMaterialDialog hescMaterialDialog = new HescMaterialDialog(this);
            hescMaterialDialog.showConfirmDialog(jsonObject.getString("title"), jsonObject.getString("message"), jsonObject.getString("buttonName"), "", new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            messageHandler.setSuccess(false);
            messageHandler.setRes("alert 没创建成功");
            webview.excuteJs(messageHandler);
        }
    }

    /***
     * Confirm
     * @param messageHandler
     * @param params
     */
    @Override
    protected void dialogConfirm(final MessageHandler messageHandler, String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            HescMaterialDialog hescMaterialDialog = new HescMaterialDialog(this);
            JSONArray jsonArray = jsonObject.getJSONArray("buttonLabels");
            String positivetext = jsonArray.getString(0);
            String navitivetext = jsonArray.getString(1);
            hescMaterialDialog.showConfirmDialog(jsonObject.getString("title"), jsonObject.getString("message"), positivetext, navitivetext, new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);
                    ConfirmBean confirmBean = new ConfirmBean();
                    confirmBean.setButtonIndex(0);
                    messageHandler.setRes(confirmBean);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                    ConfirmBean confirmBean = new ConfirmBean();
                    confirmBean.setButtonIndex(1);
                    messageHandler.setRes(confirmBean);
                    messageHandler.setSuccess(true);
                    webview.excuteJs(messageHandler);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            messageHandler.setSuccess(false);
            messageHandler.setRes("confirm 没创建成功");
            webview.excuteJs(messageHandler);
        }
    }

    /**
     * toast显示
     * @param messageHandler
     * @param params
     */
    @Override
    protected void dialogToast(MessageHandler messageHandler, String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            String content = jsonObject.getString("text");
            int duration = jsonObject.getInt("duration");
            if(duration > 2)
                ToastUtils.showLong(this,content);
            else
                ToastUtils.showShort(this,content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 单选
     * @param messageHandler
     * @param params
     */
    @Override
    protected void dialogSignal(final MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String title = jsonObject.getString("title");
            String positivetxt = jsonObject.getJSONArray("buttonLabels").getString(0);
            String nativetxt = jsonObject.getJSONArray("buttonLabels").getString(1);
            JSONArray jsonArray = jsonObject.getJSONArray("array");
            String[] choice = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                choice[i] = jsonArray.getString(i);
            }
            final HescMaterialDialog signalDialog = new HescMaterialDialog(this);
            signalDialog.showSignalDialog(title, positivetxt, nativetxt, choice, 0, new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);

                    SignalConfirmBean confirmBean = new SignalConfirmBean();
                    confirmBean.setButtonIndex(0);
                    confirmBean.setItem(signalDialog.getMultiItems().get(0));
                    messageHandler.setRes(confirmBean);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                    SignalConfirmBean confirmBean = new SignalConfirmBean();
                    confirmBean.setButtonIndex(1);
                    messageHandler.setRes(confirmBean);
                    webview.excuteJs(messageHandler);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 多选
     * @param messageHandler
     * @param params
     */
    @Override
    protected void dialogMulti(final MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String title = jsonObject.getString("title");
            String positivetxt = jsonObject.getJSONArray("buttonLabels").getString(0);
            String nativetxt = jsonObject.getJSONArray("buttonLabels").getString(1);
            JSONArray jsonArray = jsonObject.getJSONArray("array");
            String[] choice = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                choice[i] = jsonArray.getString(i);
            }
            final HescMaterialDialog multiDialog = new HescMaterialDialog(this);
            multiDialog.showMultiDialog(title, positivetxt, nativetxt, choice, null, new HescMaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(HescMaterialDialog dialog) {
                    super.onPositive(dialog);

                    MultiConfirmBean confirmBean = new MultiConfirmBean();
                    confirmBean.setButtonIndex(0);
                    confirmBean.setItems(multiDialog.getMultiItems());
                    messageHandler.setRes(confirmBean);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onNegative(HescMaterialDialog dialog) {
                    super.onNegative(dialog);
                    MultiConfirmBean confirmBean = new MultiConfirmBean();
                    confirmBean.setButtonIndex(1);
                    messageHandler.setRes(confirmBean);
                    webview.excuteJs(messageHandler);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 当前网络类型
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getNetworkType(MessageHandler messageHandler, String params) {
        ConnectivityManager mConnectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nk = mConnectivityManager.getActiveNetworkInfo();
        NetWorkBean netWorkBean = new NetWorkBean();
        if(nk != null){
            String nettype = "unknown";
            int nettypeint = nk.getType();
            if(nettypeint == ConnectivityManager.TYPE_WIFI)
                nettype = "wifi";
            else if(nettypeint == ConnectivityManager.TYPE_MOBILE)
                nettype = "mobile";
            netWorkBean.setResult(nettype);
        }else{
            netWorkBean.setResult("none");
        }
        messageHandler.setRes(netWorkBean);
        webview.excuteJs(messageHandler);
    }

    /**
     * 手机唯一编码  imei等
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getUUID(MessageHandler messageHandler, String params) {
        UUIDBean uuidBean = new UUIDBean();
        uuidBean.setUuid(new DeviceInfo(this).getDeviceCode());
        messageHandler.setRes(uuidBean);
        webview.excuteJs(messageHandler);
    }

    /**
     * 获取手机基本信息
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getPhoneInfo(MessageHandler messageHandler, String params) {
        DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
        DisplayUtils displayUtils = new DisplayUtils(this);
        deviceInfoBean.setScreenWidth(displayUtils.getWidth());
        deviceInfoBean.setScreenHeight(displayUtils.getHeight());
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        deviceInfoBean.setOperatorType(tm.getSimOperatorName());
        deviceInfoBean.setBrand(android.os.Build.BRAND);
        deviceInfoBean.setModel(android.os.Build.MODEL);
        deviceInfoBean.setVersion(android.os.Build.VERSION.SDK);
        ConnectivityManager mConnectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nk = mConnectivityManager.getActiveNetworkInfo();
        if(nk != null){
            String nettype = "unknown";
            int nettypeint = nk.getType();
            if(nettypeint == ConnectivityManager.TYPE_WIFI)
                nettype = "wifi";
            else if(nettypeint == ConnectivityManager.TYPE_MOBILE)
                nettype = "mobile";
            deviceInfoBean.setNetInfo(nettype);
        }else{
            deviceInfoBean.setNetInfo("none");
        }
        messageHandler.setSuccess(true);
        messageHandler.setRes(deviceInfoBean);
        webview.excuteJs(messageHandler);
    }

    /**
     * 选择日期 yyyy-MM-dd
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getDate(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            dateFormat = jsonObject.getString("format");
            String defaultTime = jsonObject.getString("value");
            dateMessageHandler = messageHandler;
            initTimeDialog(dateFormat,defaultTime == null ? System.currentTimeMillis(): TimeUtils.stringtoLong(defaultTime + " 00:00:00"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 选择时间  HH:mm
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getTime(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            dateFormat = jsonObject.getString("format");
            String defaultTime = jsonObject.getString("value");
            dateMessageHandler = messageHandler;
            initTimeDialog(dateFormat,defaultTime == null ? System.currentTimeMillis():TimeUtils.stringtoLong(TimeUtils.longToString(System.currentTimeMillis(),"yyyy-MM-dd ") + defaultTime + ":00"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 选择日期和时间 yyyy-MM-dd HH:mm
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getDateTime(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            dateFormat = jsonObject.getString("format");
            String defaultTime = jsonObject.getString("value");
            dateMessageHandler = messageHandler;
            initTimeDialog(dateFormat,defaultTime == null ? System.currentTimeMillis():TimeUtils.stringtoLong(defaultTime+":00"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置标题栏背景色
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarbg(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            final String colorstr = jsonObject.getString("bgcolor");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navline.setBackgroundColor(Color.parseColor(colorstr));
                }
            });
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 标题栏标题内容
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarTitle(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            final String title = jsonObject.getString("title");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navtitle.setText(title);
                }
            });
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 标题栏左侧按钮
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarLeft(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            final String text = jsonObject.getString("text");
            filterLeft = jsonObject.getBoolean("control");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navbacktv.setText(text);
                }
            });
            leftMessageHandler = messageHandler;
//            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 标题栏右侧按钮
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarRight(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            final String text = jsonObject.getString("text");
            filterRight = jsonObject.getBoolean("control");
            final boolean show = jsonObject.getBoolean("show");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!show)
                        navright.setVisibility(View.INVISIBLE);
                    navright.setText(text);
                }
            });
            rightMessageHandler = messageHandler;
//            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 替换当前页面
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarReplace(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            final String url = jsonObject.getString("url");
            messageHandler.setSuccess(true);
            webview.excuteJs(messageHandler);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl(url);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭当前浏览器
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarClose(MessageHandler messageHandler, String params) {
        webview.excuteJs(messageHandler);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebviewActivity.this.finish();
            }
        });
    }

    /**
     * 返回上一级页面
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setNavigationBarGoBack(MessageHandler messageHandler, String params) {
        webview.excuteJs(messageHandler);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(webview.canGoBack())
                    webview.goBack();
                else
                    WebviewActivity.this.finish();
            }
        });
    }

    /**
     * 设置浏览器顶部进度条颜色
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setUIProgressBarBG(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);

            ColorDrawable bg = new ColorDrawable();
            bg.setColor(Color.parseColor("#696969"));

            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(Color.parseColor(jsonObject.getString("colors")));

            ColorDrawable colorDrawable2 = new ColorDrawable();
            colorDrawable2.setColor(Color.TRANSPARENT);

            ClipDrawable d = new ClipDrawable(colorDrawable2, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            ClipDrawable d1 = new ClipDrawable(colorDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            ClipDrawable d2 = new ClipDrawable(bg, Gravity.LEFT, ClipDrawable.HORIZONTAL);

            Drawable[] drawables = {d2,d,d1};
            LayerDrawable layerDrawable = new LayerDrawable(drawables);

            layerDrawable.setId(0, android.R.id.background);
            layerDrawable.setId(1, android.R.id.secondaryProgress);
            layerDrawable.setId(2, android.R.id.progress);
            loadingProgress.setProgressDrawable(layerDrawable);

            /******只为显示设置效果，让页面重新加载一下***************************/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingProgress.setVisibility(View.VISIBLE);
                    webview.reload();
                }
            });
            /***********************************************************************/

            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 扫描条形码或者二维码
     * @param messageHandler
     * @param params
     */
    @Override
    protected void scan(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String type = jsonObject.getString("type");
            scanMessageHandler = messageHandler;
            toScan(type);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     * @param messageHandler
     * @param params
     */
    @Override
    protected void setLocalStorage(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String key = jsonObject.getString("name");
            String value = jsonObject.getString("value").replaceAll("\"","'");
            sharedPreferences.edit().putString(key,value).apply();
            StorageBean storageBean = new StorageBean();
            storageBean.setResult(true);
            storageBean.setValue(value);
            messageHandler.setSuccess(true);
            messageHandler.setRes(storageBean);
            webview.excuteJs(messageHandler);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 提取数据
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getLocalStorage(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String key = jsonObject.getString("name");
            String value = sharedPreferences.getString(key,"");
            StorageBean storageBean = new StorageBean();
            storageBean.setResult(true);
            storageBean.setValue(value);
            messageHandler.setSuccess(true);
            messageHandler.setRes(storageBean);
            webview.excuteJs(messageHandler);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 移除数据
     * @param messageHandler
     * @param params
     */
    @Override
    protected void removeLocalStorage(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String key = jsonObject.getString("name");
            sharedPreferences.edit().putString(key,"").apply();
            StorageBean storageBean = new StorageBean();
            storageBean.setResult(true);
            messageHandler.setSuccess(true);
            messageHandler.setRes(storageBean);
            webview.excuteJs(messageHandler);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取GPS位置信息，通过设置可以选择天地图的地理信息，key申请要选择web端应用
     * @param messageHandler
     * @param params
     */
    @Override
    protected void getLocalGPS(final MessageHandler messageHandler, String params) {
        final Location location = gpsForUser.getLocation();
        if(location == null){
            messageHandler.setSuccess(false);
            messageHandler.setRes("未获取位置，请稍后重试");
            webview.excuteJs(messageHandler);
        }else{
            try{
                JSONObject jsonObject = new JSONObject(params);
                boolean istiandi = jsonObject.getBoolean("tiandi");
                boolean poi = jsonObject.getBoolean("poi");
                if(poi){
                    if(istiandi){
                        String key = jsonObject.getString("key");
                        HttpWebUtils httpWebUtils = new HttpWebUtils();
                        String url = "http://api.tianditu.gov.cn/geocoder?postStr={'lon':" + location.getLongitude() + ",'lat':" + location.getLatitude() + ",'ver':1}&type=geocode&tk="+key+"";
                        httpWebUtils.get_url(url, null, new HttpRequest.OnResponseLister() {
                            @Override
                            public void onResponse(Object response) {
                                LocationBean locationBean = new LocationBean();
                                try{
                                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                    if(("0").equals(jsonObject.getString("status"))){
                                        JSONObject result = jsonObject.getJSONObject("result");
                                        if(result.has("formatted_address"))
                                            locationBean.setFormatted_address(result.getString("formatted_address"));
                                        if(result.has("addressComponent")){
                                            JSONObject addressComponent = result.getJSONObject("addressComponent");
                                            if(addressComponent.has("county_code"))
                                                locationBean.setAdCode(addressComponent.getString("county_code"));
                                            if(addressComponent.has("county"))
                                                locationBean.setAdName(addressComponent.getString("county"));
                                            if(addressComponent.has("province"))
                                                locationBean.setProvince(addressComponent.getString("province"));
                                            if(addressComponent.has("province_code"))
                                                locationBean.setProvinceCode(addressComponent.getString("province_code"));
                                            if(addressComponent.has("city"))
                                                locationBean.setCity(addressComponent.getString("city"));
                                            if(addressComponent.has("city_code"))
                                                locationBean.setCityCode(addressComponent.getString("city_code"));
                                            if(addressComponent.has("poi"))
                                                locationBean.setTitle(addressComponent.getString("poi"));
                                            if(addressComponent.has("address"))
                                                locationBean.setAddress(addressComponent.getString("address"));
                                        }

                                    }
                                    locationBean.setLatitude(location.getLatitude());
                                    locationBean.setLongitude(location.getLongitude());
                                    messageHandler.setSuccess(true);
                                    messageHandler.setRes(locationBean);
                                    webview.excuteJs(messageHandler);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Object errormsg) {
                                Log.e("err",String.valueOf(errormsg));
                            }

                            @Override
                            public void onDownLoad(float progress, long total) {

                            }
                        });
                    }else{
                        LocationBean locationBean = new LocationBean();
                        locationBean.setLatitude(location.getLatitude());
                        locationBean.setLongitude(location.getLongitude());
                        messageHandler.setSuccess(true);
                        messageHandler.setRes(locationBean);
                        webview.excuteJs(messageHandler);
                    }
                }else{
                    LocationBean locationBean = new LocationBean();
                    locationBean.setLatitude(location.getLatitude());
                    locationBean.setLongitude(location.getLongitude());
                    messageHandler.setSuccess(true);
                    messageHandler.setRes(locationBean);
                    webview.excuteJs(messageHandler);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开天地图
     * @param messageHandler
     * @param params
     */
    @Override
    protected void openMap(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            double lat = jsonObject.optDouble("latitude");
            double lng = jsonObject.optDouble("longitude");
            String baseurl = jsonObject.getString("baseUrl");
            String annourl = jsonObject.getString("annotation");
            String parturl = jsonObject.getString("parturl");
            double minx = jsonObject.getJSONObject("mapExtent").getDouble("minX");
            double miny = jsonObject.getJSONObject("mapExtent").getDouble("minY");
            double maxx = jsonObject.getJSONObject("mapExtent").getDouble("maxX");
            double maxy = jsonObject.getJSONObject("mapExtent").getDouble("maxY");
            localMapKEY = jsonObject.getString("key");
            MapExtent mapExtent = new MapExtent(minx,miny,maxx,maxy);
            openMapNative(baseurl,annourl,parturl,lng,lat,mapExtent,localMapKEY);
            mapMessageHandler = messageHandler;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 打开第三方应用
     * @param messageHandler
     * @param params
     */
    @Override
    protected void openApp(MessageHandler messageHandler, String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            final String path = jsonObject.getString("agentId");
            final String activity = jsonObject.getString("appId");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OpenActivityUtils softUtils = new OpenActivityUtils(WebviewActivity.this);
                    Bundle bundle = new Bundle();
                    softUtils.openOtherApk(bundle,path,activity);
                }
            });

            messageHandler.setSuccess(true);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     * @param messageHandler
     * @param params
     */
    @Override
    protected void downloadFile(final MessageHandler messageHandler, String params) {
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        try{
            JSONObject jsonObject = new JSONObject(params);
            String url = jsonObject.getString("url");
            String name = jsonObject.getString("name");
            downLoadUtils.downLoadFile(url, name, new HttpRequest.OnResponseLister<File>() {
                @Override
                public void onResponse(File response) {
                    DownLoadBean downLoadBean = new DownLoadBean();
                    downLoadBean.setPath(response.getAbsolutePath());
                    messageHandler.setRes(downLoadBean);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onError(Object errormsg) {
                    messageHandler.setSuccess(false);
                    messageHandler.setRes(errormsg);
                    webview.excuteJs(messageHandler);
                }

                @Override
                public void onDownLoad(float progress, long total) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     * @param messageHandler
     * @param params
     */
    @Override
    protected void capture(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            boolean custom = jsonObject.getBoolean("custome");
            boolean compress = jsonObject.getBoolean("compress");
            boolean thumbnail = jsonObject.getBoolean("thumbnail");
            mediaUrl = jsonObject.getString("mediaUrl");
            captureMessageHandler = messageHandler;
            toCapture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 多选图片
     * @param messageHandler
     * @param params
     */
    @Override
    protected void selectPictures(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            boolean multiple = jsonObject.getJSONObject("image").getBoolean("multiple");
            int maxCount = jsonObject.getJSONObject("image").getInt("max");
            boolean compress = jsonObject.getJSONObject("image").getBoolean("compress");
            boolean thumbnail = jsonObject.getBoolean("thumbnail");
            mediaUrl = jsonObject.getString("mediaUrl");
            multiPictureMessageHandler = messageHandler;
            toMultiPictures(multiple?maxCount:1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 预览图片
     * @param messageHandler
     * @param params
     */
    @Override
    protected void previewPictures(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            JSONArray jsonArray = jsonObject.getJSONArray("urls");
            ArrayList<String> previewpics = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                previewpics.add((String) jsonArray.get(i));
            }

            Intent it = new Intent(WebviewActivity.this, MultiePreViewActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putStringArrayList("pics",previewpics);
            bundle1.putBoolean("isedit",false);
            it.putExtras(bundle1);
            startActivityForResult(it,PREVIEW_PICTURE);
            webview.excuteJs(messageHandler);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 拨打电话
     * @param messageHandler
     * @param params
     */
    @Override
    protected void callPhone(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String phone = jsonObject.getString("phone");
            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送短信
     * @param messageHandler
     * @param params
     */
    @Override
    protected void sendSMG(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String phone = jsonObject.getString("phone");
            String content = jsonObject.getString("msg");
            Uri smsToUri = Uri.parse("smsto:"+phone);
            Intent smgintent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            smgintent.putExtra("sms_body", content);
            startActivity(smgintent);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     * @param messageHandler
     * @param params
     */
    @Override
    protected void startRecordAudio(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            mediaUrl = jsonObject.getString("mediaUrl");
            if(mRecordVoice == null){
                String dir = SdcardInfo.File_Voice;
                String fileName = TimeUtils.getSystime("yyyyMMddHHmmss");
                mRecordVoice = new RecordVoice(dir,fileName);
            }
            mRecordVoice.startRecord();
            webview.excuteJs(messageHandler);
        }catch (Exception e){

        }
    }

    /**
     * 停止录音
     * @param messageHandler
     * @param params
     */
    @Override
    protected void stopRecordAudio(final MessageHandler messageHandler, String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            mediaUrl = jsonObject.getString("mediaUrl");
            mRecordVoice.setFinishedListener(new RecordVoice.OnFinishedRecordListener() {
                @Override
                public void onFinishedRecord(final String audioPath, final long recordtime) {
                    final VoiceBean voiceFile = new VoiceBean();
                    voiceFile.setMediaUrl(audioPath);
                    voiceFile.setDuration(recordtime);
                    sendVoiceFile(messageHandler,audioPath,recordtime);
                }
            });
            mRecordVoice.stopRecording();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 监听录制结束---时间限制为20S
     * @param messageHandler
     * @param params
     */
    @Override
    protected void recordAudioSubscribe(final MessageHandler messageHandler, String params) {
        if(mRecordVoice == null){
            String dir = SdcardInfo.File_Voice;
            String fileName = TimeUtils.getSystime("yyyyMMddHHmmss");
            mRecordVoice = new RecordVoice(dir,fileName);
        }
        mRecordVoice.setTimeenable(true);
        mRecordVoice.setFinishedListener(new RecordVoice.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(final String audioPath, final long recordtime) {
                final VoiceBean voiceFile = new VoiceBean();
                voiceFile.setMediaUrl(audioPath);
                voiceFile.setDuration(recordtime);
                sendVoiceFile(messageHandler,audioPath,recordtime);
            }
        });
    }

    /**
     * 播放录音
     * @param messageHandler
     * @param params
     */
    @Override
    protected void playAudio(MessageHandler messageHandler, String params) {
        if(mVoiceUtils == null)
            mVoiceUtils = new VoiceUtils();
        try{
            JSONObject jsonObject = new JSONObject(params);
            String andioUrl = jsonObject.getString("localAudioId");
            if(mVoiceUtils.getPlayer().isPlaying())
                mVoiceUtils.getPlayer().stop();
            mVoiceUtils.startPlay(andioUrl);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 暂停录音
     * @param messageHandler
     * @param params
     */
    @Override
    protected void pauseAudio(MessageHandler messageHandler, String params) {
        if(mVoiceUtils != null && null != mVoiceUtils.getPlayer()){
            if(mVoiceUtils.getPlayer().isPlaying())
                mVoiceUtils.getPlayer().pause();
        }
        webview.excuteJs(messageHandler);
    }

    /**
     * 恢复播放
     * @param messageHandler
     * @param params
     */
    @Override
    protected void resumeAudio(MessageHandler messageHandler, String params) {
        if(mVoiceUtils != null && null != mVoiceUtils.getPlayer()){
            mVoiceUtils.getPlayer().start();
        }
        webview.excuteJs(messageHandler);
    }

    /**
     * 停止播放
     * @param messageHandler
     * @param params
     */
    @Override
    protected void stopAudio(MessageHandler messageHandler, String params) {
        if(mVoiceUtils != null) {
            mVoiceUtils.stopPlayVoice();
        }
        webview.excuteJs(messageHandler);
    }

    /**
     * 监听播放停止
     * @param messageHandler
     * @param params
     */
    @Override
    protected void playAudioSubscribe(final MessageHandler messageHandler, String params) {
        if(mVoiceUtils == null)
            mVoiceUtils = new VoiceUtils();
        mVoiceUtils.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVoiceUtils.stopPlayVoice();
                webview.excuteJs(messageHandler);
            }
        });
    }

    /**
     * 新窗口打开页面
     * @param messageHandler
     * @param params
     */
    @Override
    protected void openHrefUrl(MessageHandler messageHandler, String params) {
        try{
            JSONObject jsonObject = new JSONObject(params);
            String href = jsonObject.getString("hrefurl");
            Intent it1 = new Intent(WebviewActivity.this, ItemActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("url",href);
            it1.putExtras(bundle2);
            startActivity(it1);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过JS调用任何原生功能
     * @param messageHandler 回调JS的数据
     * @param params 从JS传来的数据
     */
    @Override
    protected void nativeMethod(MessageHandler messageHandler, String params) {
        /*
        * 这里进行很多原生开发
        * */
        try{
            JSONObject jsonObject = new JSONObject(params);
            String href = jsonObject.getString("url");
            ToastUtils.showShort(WebviewActivity.this,"从js传来的:"+href);
            webview.excuteJs(messageHandler);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        if(view == reload){
            webview.reload();
            loadingProgress.setVisibility(View.VISIBLE);
        }else if(view == navbacktv){
            if(filterLeft){
                leftMessageHandler.setSuccess(true);
                webview.excuteJs(leftMessageHandler);
                filterLeft = false;
            }else{
                if(webview.canGoBack())
                    webview.goBack();
                else
                    super.onBackPressed();
            }
        }else if(view == navright){
            if(filterRight){
                rightMessageHandler.setSuccess(true);
                webview.excuteJs(rightMessageHandler);

            }
        }
    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack())
            webview.goBack();
        else
            super.onBackPressed();
    }

    private void initTimeDialog(String datetype,long miseconde){

        Type type;
        if(datetype.equals("yyyy-MM-dd"))
            type = Type.YEAR_MONTH_DAY;
        else if(datetype.equals("HH:mm"))
            type = Type.HOURS_MINS;
        else
            type = Type.ALL;
        long tenYears = 20L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("时间选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - tenYears)
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setSelectedDate(TimeUtils.longToString(miseconde,"yyyy-MM-dd HH:mm"))
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(type)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
//                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
//        mDialogAll.show(getFragmentManager(),"all");
    }

    private void toScan(String codeType){
        final String cType = codeType;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WebviewActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(cType.equals("qrCode")?false:true);//是否扫描条形码 默认为true
                config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
                config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, READERCODE);
            }
        });
    }

    private void openMapNative(final String baseurl, final String annourl, String parturl, final double lon, final double lat, final MapExtent mapExtent, final String mapKey){
        final Intent it = new Intent(this, MapActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                /**杭州天地图测试*/
                Random random = new Random();
                int index = random.nextInt(6);
                bundle.putString("basicinfo",baseurl+"?tk="+mapKey);
                bundle.putString("annotation",annourl+"?tk="+mapKey);
                bundle.putSerializable("maptype", iMapView.MapType.TIANDI);
                bundle.putInt("maxlevel",16);
                bundle.putSerializable("mapextent",mapExtent);

                if(!Double.isNaN(lat) && !Double.isNaN(lon)){
                    bundle.putDouble("strx",lon);
                    bundle.putDouble("stry",lat);
                }
                it.putExtras(bundle);
                startActivityForResult(it,OPEMMAP);
            }
        });

    }

    private void toCapture(){
        final Intent it = new Intent(this, Camera_VideoActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(it,TAKE_PICTURE);
            }
        });
    }

    private void toMultiPictures(int maxCount){
        Intent it = new Intent(this, MultiplePicActivity.class);
        Bundle bl = new Bundle();
        bl.putInt("piccount",maxCount);
        it.putExtras(bl);
        startActivityForResult(it,MULTIPLE_CHOISE);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        DateBean dateBean = new DateBean();
        dateBean.setValue(TimeUtils.longToString(millseconds,dateFormat));
        dateMessageHandler.setRes(dateBean);
        webview.excuteJs(dateMessageHandler);
        dateMessageHandler = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case READERCODE:
                if(resultCode == RESULT_OK){
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        ScanBean scanBean = new ScanBean();
                        scanBean.setText(content);
                        scanMessageHandler.setSuccess(true);
                        scanMessageHandler.setRes(scanBean);
                        webview.excuteJs(scanMessageHandler);
                    }
                }
                break;
            case TAKE_PICTURE:
                if(resultCode == RESULT_OK){

                    final String path = data.getExtras().getString("mediapath");
                    List<String> pics = new ArrayList<>();
                    pics.add(path);
                    captureMessageHandler.setRunning(true);
                    CaptureBean captureBean = new CaptureBean();
                    ArrayList<String> list = new ArrayList<>();
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),100,100);
                    list.add(Media2Base64Util.bitmapToBase64(bitmap).replaceAll("\n",""));
                    captureBean.setThumbnails(list);
                    captureMessageHandler.setRunning(true);
                    captureMessageHandler.setRes(captureBean);
                    webview.excuteJs(captureMessageHandler);
                    sendMediaFile(pics,captureMessageHandler);
                }
                break;
            case MULTIPLE_CHOISE:
                if(resultCode == RESULT_OK){
                    ArrayList<String> pics = new ArrayList<>();
                    pics.clear();
                    Bundle bl = data.getExtras();
                    pics.addAll(bl.getStringArrayList("pics"));
                    ArrayList<String> list = new ArrayList<>();
                    CaptureBean captureBean = new CaptureBean();
                    for (int i = 0; i < pics.size(); i++) {
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pics.get(i)),100,100);
                        list.add(Media2Base64Util.bitmapToBase64(bitmap).replaceAll("\n",""));
                    }
                    captureBean.setThumbnails(list);
                    multiPictureMessageHandler.setRunning(true);
                    multiPictureMessageHandler.setRes(captureBean);
                    webview.excuteJs(multiPictureMessageHandler);
                    sendMediaFile(pics,multiPictureMessageHandler);
                }
                break;
            case REQUEST_SELECT_FILE:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    if (uploadMessage == null)
                        return;
                    Bundle bl = data.getExtras();
                    List<String> list = bl.getStringArrayList("pics");
                    Uri[] uris = new Uri[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        File mediaFile = new File(list.get(i));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
                            Uri contentUri = FileProvider.getUriForFile(context,
                                    context.getApplicationContext().getPackageName() + ".provider",//与清单文件中android:authorities的值保持一致
                                    mediaFile);//FileProvider方式或者ContentProvider。也可使用VmPolicy方式
                            uris[i] = contentUri;

                        } else {
                            uris[i] = Uri.fromFile(mediaFile);//或者 Uri.isPaise("file://"+file.toString()

                        }


                    }
                    uploadMessage.onReceiveValue(uris);
//                    uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    uploadMessage = null;

                    mediatoolbar.setVisibility(View.GONE);
                }
                break;
            case REQUEST_CAPTURE_FILE:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    if (uploadMessage == null)
                        return;
                    Uri[] uris = new Uri[1];
                    uris[0] = imageUri;
                    uploadMessage.onReceiveValue(uris);
                    uploadMessage = null;

                    mediatoolbar.setVisibility(View.GONE);
                }
                break;
            case FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage)
                    return;
                Uri result = data == null || resultCode != WebviewActivity.RESULT_OK ? null : data.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
            case OPEMMAP:
                if(resultCode == RESULT_OK){
                    Log.e("position",data.getExtras().getString("strx")+"@"+data.getExtras().getString("stry")+"@"+data.getExtras().getString("partcode",""));
                    final double lat = Double.parseDouble(data.getExtras().getString("stry"));
                    final double lng = Double.parseDouble(data.getExtras().getString("strx"));
                    HttpWebUtils httpWebUtils = new HttpWebUtils();
                    String url = "http://api.tianditu.gov.cn/geocoder?postStr={'lon':" + data.getExtras().getString("strx") + ",'lat':" + data.getExtras().getString("stry") + ",'ver':1}&type=geocode&tk="+localMapKEY+"";
                    httpWebUtils.get_url(url, null, new HttpRequest.OnResponseLister() {
                        @Override
                        public void onResponse(Object response) {
                            LocationBean locationBean = new LocationBean();
                            try{
                                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                if(("0").equals(jsonObject.getString("status"))){
                                    JSONObject result = jsonObject.getJSONObject("result");
                                    if(result.has("formatted_address"))
                                        locationBean.setFormatted_address(result.getString("formatted_address"));
                                    if(result.has("addressComponent")){
                                        JSONObject addressComponent = result.getJSONObject("addressComponent");
                                        if(addressComponent.has("county_code"))
                                            locationBean.setAdCode(addressComponent.getString("county_code"));
                                        if(addressComponent.has("county"))
                                            locationBean.setAdName(addressComponent.getString("county"));
                                        if(addressComponent.has("province"))
                                            locationBean.setProvince(addressComponent.getString("province"));
                                        if(addressComponent.has("province_code"))
                                            locationBean.setProvinceCode(addressComponent.getString("province_code"));
                                        if(addressComponent.has("city"))
                                            locationBean.setCity(addressComponent.getString("city"));
                                        if(addressComponent.has("city_code"))
                                            locationBean.setCityCode(addressComponent.getString("city_code"));
                                        if(addressComponent.has("poi"))
                                            locationBean.setTitle(addressComponent.getString("poi"));
                                        if(addressComponent.has("address"))
                                            locationBean.setAddress(addressComponent.getString("address"));
                                    }

                                }
                                locationBean.setLatitude(lat);
                                locationBean.setLongitude(lng);
                                mapMessageHandler.setSuccess(true);
                                mapMessageHandler.setRes(locationBean);
                                webview.excuteJs(mapMessageHandler);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Object errormsg) {
                            Log.e("err",String.valueOf(errormsg));
                        }

                        @Override
                        public void onDownLoad(float progress, long total) {

                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    /**
     * 附件上传
     * @param files
     */
    private void sendMediaFile(List<String> files, final MessageHandler messageHandler){

        if(null == files || files.size() == 0)
            return;

        final List<String> uploadfiles = new ArrayList<>();
        //遍历文件，如果是图片，超过1M进行压缩
        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i).toLowerCase();
            if(file.endsWith(".jpg") || file.endsWith(".jpeg")){
                File file1 = new File(file);
                if(file1.length() > 200*1024*1024){
                    String temppic = file1.getParent() + file1.getName() + "-temp.jpg";
                    if(copyFile(file1,temppic)) {
                        BitMapUtils.mixCompress(BitmapFactory.decodeFile(temppic),temppic);
                        uploadfiles.add(temppic);
                    }else
                        uploadfiles.add(file);

                }else
                    uploadfiles.add(file);
            }else
                uploadfiles.add(file);
        }

        final WebRequest we = new WebRequest();
        we.sendMediaFiles(mediaUrl, uploadfiles, new HttpRequest.OnResponseLister() {
            @Override
            public void onResponse(Object response) {
                String str = String.valueOf(response);
                final List<BaseFile> bas = new Gson().fromJson(str,new TypeToken<List<BaseFile>>() {}.getType());
                if(bas != null){
                    CaptureBean captureBean = new CaptureBean();
                    ArrayList<CaptureBean.CaptureItme> items = new ArrayList<>();
                    for (int i = 0; i < bas.size(); i++) {
                        BaseFile baseFile = bas.get(i);
                        CaptureBean.CaptureItme captureItme = new CaptureBean.CaptureItme();
                        captureItme.setPath(baseFile.getNewPath());
                        items.add(captureItme);
                    }
                    captureBean.setPics(items);
                    messageHandler.setRes(captureBean);
                    messageHandler.setRunning(false);
                    messageHandler.setSuccess(true);
                    webview.excuteJs(messageHandler);
                }

                //删除所有副本图片
                for (int i = 0; i < uploadfiles.size(); i++) {
                    String file = uploadfiles.get(i).toLowerCase();
                    if(file.endsWith("-temp.jpg")){
                        File file1 = new File(file);
                        file1.delete();
                    }
                }

            }

            @Override
            public void onError(Object errormsg) {
                ToastUtils.showShort(getApplication(),"附件上报失败");
            }

            @Override
            public void onDownLoad(float progress, long total) {

            }
        });
    }

    /**
     * 根据文件路径拷贝文件
     * @param src 源文件
     * @param destPath 目标文件路径
     * @return boolean 成功true、失败false
     */
    public boolean copyFile(File src, String destPath) {
        boolean result = false;
        if ((src == null) || (destPath== null)) {
            return result;
        }
        File dest= new File(destPath);
        if (dest!= null && dest.exists()) {
            dest.delete(); // delete file
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void sendVoiceFile(final MessageHandler messageHandler, String path, final long filesize){

        WebRequest we = new WebRequest();
        List<String> pics = new ArrayList<>();
        pics.add(path);
        we.sendMediaFiles(mediaUrl, pics, new HttpRequest.OnResponseLister() {
            @Override
            public void onResponse(Object response) {
                List<BaseFile> bas = new Gson().fromJson(String.valueOf(response),new TypeToken<List<BaseFile>>() {}.getType());
                if(bas != null && bas.size()>0){
                    VoiceBean pb = new VoiceBean();
                    pb.setMediaUrl(bas.get(0).getNewPath());
                    pb.setDuration(filesize/1000L);
                    messageHandler.setSuccess(true);
                    messageHandler.setRes(pb);
                    webview.excuteJs(messageHandler);
                }

            }

            @Override
            public void onError(Object errormsg) {
                messageHandler.setSuccess(false);
                messageHandler.setRes(errormsg);
                webview.excuteJs(messageHandler);
            }

            @Override
            public void onDownLoad(float progress, long total) {

            }
        });
    }
}

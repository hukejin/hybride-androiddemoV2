package cn.com.hesc.hybridev2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.com.hesc.tools.CheckPermissonUtils;
import cn.com.hesc.tools.ToastUtils;
import cn.com.hesc.zxinglibrary.android.CaptureActivity;
import cn.com.hesc.zxinglibrary.bean.ZxingConfig;
import cn.com.hesc.zxinglibrary.common.Constant;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckPermissonUtils checkPermissonUtils = new CheckPermissonUtils(this,permissions);
        checkPermissonUtils.checkPermission(permissions);

    }

    public void toDemo(View view){
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
        config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
        config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        if(content.contains("http") || content.contains("https")){
                            Intent it = new Intent(MainActivity.this, WebviewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url",content);
                            it.putExtras(bundle);
                            startActivity(it);
                        }else{
                            ToastUtils.showShort(MainActivity.this,"不是正常url");
                        }
                    }
                }
                break;
        }
    }
}

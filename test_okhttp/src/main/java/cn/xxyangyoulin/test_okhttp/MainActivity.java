package cn.xxyangyoulin.test_okhttp;

import android.Manifest;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import junit.framework.Test;

import cn.xxyangyoulin.library.LogUtil;
import cn.xxyangyoulin.library.RequestPermission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestPermission.with(MainActivity.this)
                        .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new RequestPermission.Callback() {
                            @Override
                            public void onGranted() {
                                new UseTest();
                            }

                            @Override
                            public void onDenied() {
                                LogUtil.e(this,"权限被拒绝");
                            }
                        });
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermission.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}

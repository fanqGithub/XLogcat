package com.fq.xlogcat;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.fq.xlogcat.logcat.VirtualTerminal;
import com.fq.xlogcat.service.LogWindowService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(MainActivity.this, LogWindowService.class);
                Toast.makeText(MainActivity.this, "已开启XLogcat", Toast.LENGTH_SHORT).show();
                startService(intent);
                finish();
            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(MainActivity.this, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        } else {
            //SDK在23以下，不用管.
            Intent intent = new Intent(MainActivity.this, LogWindowService.class);
            startService(intent);
            finish();
        }
    }
}

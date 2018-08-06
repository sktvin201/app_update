package cn.rokevin.updatedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.rokevin.app.update.AppUpdate;
import cn.rokevin.app.update.VersionData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VersionData versionData = null;

        versionData = new VersionData();
        versionData.setVersion("1.0.1");
        versionData.setUrl("http://hm.hlvan.cn/appdownload/stowage.apk");
        versionData.setForce(false);
//        versionData.setContent("1. 了深刻的记录上空间发牢骚深刻的记录上空间发 2. 牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚的飞");
        versionData.setContent("1. 修复若干bug<br> 2. 增加特殊功能");

        AppUpdate.update(MainActivity.this, versionData);
    }
}

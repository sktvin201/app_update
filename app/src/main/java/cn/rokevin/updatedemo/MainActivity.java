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
        versionData.setForce(true);
        versionData.setContent("更新内容");

        AppUpdate.update(MainActivity.this, versionData);
    }
}

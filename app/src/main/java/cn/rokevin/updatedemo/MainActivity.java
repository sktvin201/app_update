package cn.rokevin.updatedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.rokevin.app.upgrade.AppUpgrade;
import cn.rokevin.app.upgrade.DownloadInfo;
import cn.rokevin.app.upgrade.DownloadManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //        private static final String url = "http://imtt.dd.qq.com/16891/89E1C87A75EB3E1221F2CDE47A60824A.apk?fsname=com.snda.wifilocating_4.2.62_3192.apk&csr=1bbd";
    private static final String url = "http://hm.hlvan.cn/appdownload/stowage.apk";

    private Context mContext = MainActivity.this;

    private ProgressBar pbProgress;
    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnForceUpdate = findViewById(R.id.btn_force_update);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnPause = findViewById(R.id.btn_pause);
        btnUpdate.setOnClickListener(this);
        btnForceUpdate.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        pbProgress = findViewById(R.id.pb_progress);
        tvProgress = findViewById(R.id.tv_progress);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_update:
                update();
                break;

            case R.id.btn_force_update:
                forceUpdate();
                break;

            case R.id.btn_start:
                start();
                break;

            case R.id.btn_pause:
                pause();
                break;

            default:

                break;

        }
    }

    public void update() {

//        VersionData versionData = null;
//
//        versionData = new VersionData();
//        versionData.setVersion("1.0.1");
////        versionData.setUrl("http://hm.hlvan.cn/appdownload/stowage.apk");
//        versionData.setUrl(url);
//        versionData.setForce(false);
////        versionData.setContent("1. 了深刻的记录上空间发牢骚深刻的记录上空间发 2. 牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚了深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚深刻的记录上空间发牢骚的飞");
//        versionData.setContent("1. 修复若干bug<br> 2. 增加特殊功能");
//
//        AppUpdate.update(MainActivity.this, versionData);


//        AppUpgrade.update(MainActivity.this, url, false, "1.0.1", "1. 修复若干bug<br> 2. 增加特殊功能");

        AppUpgrade.update(MainActivity.this, false, url, "1.0.1", "1. 修复若干bug<br> 2. 增加特殊功能");
    }

    public void forceUpdate() {

//        AppUpgrade.update(MainActivity.this, url, true, "1.0.1", "1. 修复若干bug<br> 2. 增加特殊功能");

        AppUpgrade.update(MainActivity.this, true, url, "1.0.1", "1. 修复若干bug<br> 2. 增加特殊功能");
    }

    public void start() {

        // 太慢 7M app下载了好久
        String savePath = getExternalCacheDir().getAbsolutePath() + "/downloads/app/update.apk";
        Log.e(TAG, "start#savePath:" + savePath);
//        Network.testDownLoad(url, savePath);

        DownloadManager downloadManager = new DownloadManager(mContext);
        downloadManager.setProgressListener(new DownloadManager.ProgressListener() {
            @Override
            public void progressChanged(final DownloadInfo info) {

                int status = info.getStatus();

                switch (status) {

                    case DownloadInfo.DOWNLOADING: {

                        final int percent = info.getPercent();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvProgress.setText(percent + "%");
//                                pbProgress.setProgress(percent);
//                            }
//                        });

                        tvProgress.setText(percent + "%");
                        pbProgress.setProgress(percent);
                        break;
                    }
                    case DownloadInfo.FINISH: {

                        final int percent = info.getPercent();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvProgress.setText(info.getMessage());
                                pbProgress.setProgress(percent);
                            }
                        });
                        break;
                    }
                    case DownloadInfo.ERORR: {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvProgress.setText(info.getMessage());
                            }
                        });
                        break;
                    }
                    default:

                        break;

                }
            }
        });

        downloadManager.download(url, "xxxxx");
    }

    public void pause() {

    }
}

package cn.rokevin.app.upgrade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import cn.rokevin.app.R;
import cn.rokevin.app.update.IntentUtil;

public class DownloadService extends Service implements DownloadManager.ProgressListener {

    private static Context mContext;

    private String urlBundle;

    private DownloadManager downloadManager;

    private NotificationUtil notificationUtil = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        downloadManager = new DownloadManager(mContext);
        downloadManager.setProgressListener(this);

        notificationUtil = new NotificationUtil(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        DownloadInfo info = downloadManager.getInfo();
        int status = info.getStatus();

        if (status == DownloadInfo.DOWNLOADING) {
            ToastUtil.shortShow(mContext, "正在更新中...");
        } else {
            ToastUtil.shortShow(this, R.string.app_update_start);
            urlBundle = intent.getStringExtra(AppBundle.URL);
            downloadManager.download(urlBundle);
        }

        Log.e("onStartCommand", "DownloadInfo#status:" + status);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void progressChanged(DownloadInfo info) {

        switch (info.getStatus()) {

            case DownloadInfo.WAIT_DOWNLOAD:

                notificationUtil.sendNotification();
                break;

            case DownloadInfo.DOWNLOADING:

                notificationUtil.updateProgress(info.getPercent());
                break;

            case DownloadInfo.FINISH:

                notificationUtil.updateProgress(info.getPercent());

                String savePath = info.getSavePath();
                String name = info.getFileName();
                String fileType = info.getFileType();
                IntentUtil.instanll(new File(savePath, name + "." + fileType), mContext);
                stopSelf();
                break;

            case DownloadInfo.ERORR:

                notificationUtil.cancle();
                ToastUtil.shortShow(mContext, info.getMessage());
                stopSelf();
                break;

            default:

                break;
        }
    }
}

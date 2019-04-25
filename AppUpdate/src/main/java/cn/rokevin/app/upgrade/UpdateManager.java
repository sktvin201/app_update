package cn.rokevin.app.upgrade;

import android.app.Activity;
import android.content.Intent;

class UpdateManager {

    private Activity activity;
    private boolean force;
    private String url;
    private String version;
    private String content;
    private boolean isToast;

    public UpdateManager(Activity activity, boolean force, String url, String version, String content, boolean isToast) {
        this.activity = activity;
        this.force = force;
        this.url = url;
        this.version = version;
        this.content = content;
        this.isToast = isToast;
    }

    public void performUpdate() {

        if (force) {
            updateForce();
        } else {
            update();
        }
    }

    /**
     * 普通更新
     */
    public void update() {

        UpdateDialog updateDialog = new UpdateDialog(activity);
        updateDialog.setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
            @Override
            public void confirm() {

                Intent intent = new Intent(activity, DownloadService.class);
                intent.putExtra(AppBundle.URL, url);
                activity.startService(intent);
            }
        });
        updateDialog.showDialog(version, content);
    }

    /**
     * 强制更新
     */
    public void updateForce() {

        final UpdateForceDialog updateForceDialog = new UpdateForceDialog(activity);
        final DownloadManager downloadManager = new DownloadManager(activity);

        downloadManager.setProgressListener(new DownloadManager.ProgressListener() {
            @Override
            public void progressChanged(DownloadInfo info) {
                updateForceDialog.setProgress(info.getPercent());
            }
        });

        updateForceDialog.setOnUpdateForceListener(new UpdateForceDialog.OnUpdateForceListener() {
            @Override
            public void start() {
                downloadManager.download(url);
            }

            @Override
            public void install() {
                downloadManager.install();
            }
        });
        updateForceDialog.showDialog();
    }
}

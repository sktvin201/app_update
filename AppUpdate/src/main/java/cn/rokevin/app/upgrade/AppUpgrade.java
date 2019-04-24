package cn.rokevin.app.upgrade;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;

import cn.rokevin.app.update.DownloadService;
import cn.rokevin.app.update.IntentUtil;
import cn.rokevin.app.update.R;

/**
 * App更新工具类
 */
public class AppUpgrade {

    /**
     * 没有提示更新
     *
     * @param activity
     * @param url      请求地址
     * @param force    是否强制更新
     * @param version  版本
     * @param content  更新内容
     */
    public static void update(Activity activity, String url, boolean force, String version, String content) {

        updateToast(activity, url, force, version, content, false);
    }

    /**
     * 更新提示
     *
     * @param activity
     * @param url      请求地址
     * @param force    是否强制更新
     * @param version  版本
     * @param content  更新内容
     */
    public static void updateToast(final Activity activity, final String url, boolean force, String version, String content, boolean isToast) {

        if (TextUtils.isEmpty(url)) {
            if (isToast) {
                ToastUtil.shortShow(activity, R.string.app_update_url_not_empty);
            }
            return;
        }

        if (force) {

            // 强制更新
            //http://m.appchina.com/market/e/882602/0/16/44DFFDF6E89D0CC2F5CB41CE041E9BB7/packagename.apk?refererPage=m.cherry.soft_list
            UpdateManager mUpdateManager = new UpdateManager(activity);
            mUpdateManager.setApkUrl(url);
            mUpdateManager.checkUpdateInfo();

            final DownloadManager downloadManager = new DownloadManager(activity);

            final UpdateForceDialog updateForceDialog = new UpdateForceDialog(activity);
            updateForceDialog.setOnUpdateForceListener(new UpdateForceDialog.OnUpdateForceListener() {
                @Override
                public void onUpdateForce() {

                    downloadManager.download(url);
                }

                @Override
                public void onInstall() {

                    String filePath = downloadManager.getFilePath();
                    IntentUtil.instanll(new File(filePath), activity);
                }
            });
            updateForceDialog.showDialog();

            downloadManager.setProgressListener(new DownloadManager.ProgressListener() {
                @Override
                public void progressChanged(final DownloadInfo info) {

                    int status = info.getStatus();

                    switch (status) {

                        case DownloadInfo.DOWNLOADING: {

                            final int percent = info.getPercent();

                            updateForceDialog.setProgress(percent);

                            break;
                        }
                        case DownloadInfo.FINISH: {

                            final int percent = info.getPercent();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateForceDialog.setProgress(percent);
                                }
                            });
                            break;
                        }
                        case DownloadInfo.ERORR: {

                            final int percent = info.getPercent();

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateForceDialog.setProgress(percent);
                                }
                            });
                            break;
                        }
                        default:

                            break;

                    }
                }
            });

        } else {

            boolean isDownloading = DownloadService.isDownloading;

            if (isDownloading) {

                ToastUtil.shortShow(activity, "正在更新中...");

            } else {

                Intent intent = new Intent(activity, DownloadService.class);
                activity.startService(intent);

                // 普通更新
                UpdateDialog updateDialog = new UpdateDialog(activity);
                updateDialog.setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
                    @Override
                    public void onUpdateConfirm() {

                        Intent intent = new Intent(activity, DownloadService.class);
                        activity.startService(intent);

                        ToastUtil.shortShow(activity, "开始更新...");
                        DownloadService.downNewFile(url);
                    }
                });

                updateDialog.showDialog(version, content);
            }
        }
    }
}

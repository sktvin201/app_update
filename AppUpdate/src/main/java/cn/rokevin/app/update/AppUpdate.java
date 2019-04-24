package cn.rokevin.app.update;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import cn.rokevin.app.R;

/**
 * App更新工具类
 */
public class AppUpdate {

    /**
     * 没有提示更新
     *
     * @param activity
     * @param versionData
     */
    public static void update(Activity activity, VersionData versionData) {

        updateToast(activity, versionData, false);
    }

    /**
     * 更新提示
     *
     * @param activity
     * @param versionData
     */
    public static void updateToast(final Activity activity, VersionData versionData, boolean isToast) {

        if (versionData == null) {

            if (isToast) {
                ToastUtil.shortShow(activity, R.string.app_update_is_new);
            }

            return;
        }

        boolean force = versionData.isForce();
        final String url = versionData.getUrl();

        if (TextUtils.isEmpty(url)) {
            ToastUtil.shortShow(activity, R.string.app_update_url_not_empty);
            return;
        }

        if (force) {

            // 强制更新
            //http://m.appchina.com/market/e/882602/0/16/44DFFDF6E89D0CC2F5CB41CE041E9BB7/packagename.apk?refererPage=m.cherry.soft_list
            UpdateManager mUpdateManager = new UpdateManager(activity);
            mUpdateManager.setApkUrl(url);
            mUpdateManager.checkUpdateInfo();

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

                updateDialog.showDialog(versionData);
            }
        }
    }
}

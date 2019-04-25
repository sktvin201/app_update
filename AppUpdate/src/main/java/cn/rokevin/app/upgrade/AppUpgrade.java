package cn.rokevin.app.upgrade;

import android.app.Activity;
import android.text.TextUtils;

import cn.rokevin.app.R;

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
    public static void update(Activity activity, boolean force, String url, String version, String content) {

        updateToast(activity, force, url, version, content, false);
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
    public static void updateToast(final Activity activity, boolean force, final String url, String version, String content, boolean isToast) {

        if (TextUtils.isEmpty(url)) {
            if (isToast) {
                ToastUtil.shortShow(activity, R.string.app_update_url_not_empty);
            }
            return;
        }

        UpdateManager updateManager = new UpdateManager(activity, force, url, version, content, isToast);
        updateManager.performUpdate();
    }
}

package cn.rokevin.app.update;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by luokaiwen on 15/4/28.
 * <p/>
 * 吐丝帮助类
 */
class ToastUtil {

    public static final String TAG = ToastUtil.class.getSimpleName();

    public static void shortShow(Context context, String content) {

        if (null == context) {
            return;
        }

        if (TextUtils.isEmpty(content)) {
            return;
        }

        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context, String content) {

        if (null == context) {
            return;
        }

        if (TextUtils.isEmpty(content)) {
            return;
        }

        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static void shortShow(Context context, int content) {

        if (null == context) {
            return;
        }

        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context, int content) {

        if (null == context) {
            return;
        }

        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}

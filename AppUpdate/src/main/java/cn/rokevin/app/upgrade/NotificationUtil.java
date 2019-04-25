package cn.rokevin.app.upgrade;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.rokevin.app.BuildConfig;

public class NotificationUtil {

    private NotificationManager mNotificationManager;
    private static NotificationCompat.Builder builder;
    private NotificationChannel mNotificationChannel;

    private static final String CHANNEL_ID = "app_upgrade";
    private static final String CHANNEL_NAME = "应用更新";

    private static int notificationId;

    private Context context;

    public NotificationUtil(Context context) {

        this.context = context;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.enableLights(false);
            mNotificationChannel.enableVibration(false);
            mNotificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }

        notificationId = ((int) (System.currentTimeMillis() / 1000));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 发送通知
     */
    public void sendNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        builder.setSound(null).
                setVibrate(null).
                setLights(0, 0, 0).
                setContentTitle("下载中").
                setSmallIcon(android.R.drawable.stat_sys_download).
                setTicker("下载进度通知").
                setWhen(System.currentTimeMillis()).
                setProgress(100, 0, false);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        //发送一个通知
        mNotificationManager.notify(notificationId, notification);
    }

    /**
     * 更新通知进度
     *
     * @param percent
     */
    public void updateProgress(int percent) {

        if (percent == 100) {
            builder.setContentTitle("下载完成 " + percent + "%");
        } else {
            builder.setContentTitle("正在下载, 已下载" + percent + "%");
        }

        // 更新进度条
        builder.setProgress(100, percent, false);

        // 再次通知
        mNotificationManager.notify(notificationId, builder.build());

        if (percent == 100) {
            cancle();
        }
    }

    public void cancle() {
        mNotificationManager.cancel(notificationId);
    }

    /**
     * 判断是否开启通知
     *
     * @param context
     * @return
     */
    public static boolean isEnable(Context context) {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * NotificationManagerCompat中的方法areNotificationsEnabled自带判断版本功能
     *
     * @param context
     * @return
     */
    public boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return this.mNotificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return (Integer) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == 0;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException | ClassNotFoundException var9) {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 跳转至通知打开页
     *
     * @param context
     */
    public static void gotoNotificationSet(Context context) {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", BuildConfig.APPLICATION_ID);
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", BuildConfig.APPLICATION_ID);
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

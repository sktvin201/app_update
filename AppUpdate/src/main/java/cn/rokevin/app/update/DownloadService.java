package cn.rokevin.app.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class DownloadService extends Service {

    private static NotificationManager notificationManager;
    private static boolean cancelUpdate = false;
    private static MyHandler myHandler;
    private static ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
    private static Map<Integer, Integer> download = new HashMap<>();
    private static Context mContext;
    private static NotificationCompat.Builder builderProgress;
    private static int notificationId;

    private static String PUSH_CHANNEL_ID;
    private static final String PUSH_CHANNEL_NAME = "App更新库";

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
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PUSH_CHANNEL_ID = mContext.getPackageName() + ".app.update1000"; // 更新渠道

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        myHandler = new MyHandler(Looper.myLooper(), DownloadService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //    public static void downNewFile(final String url, final int notificationId, final String name) {
    public static void downNewFile(final String url) {

        notificationId = ((int) (System.currentTimeMillis() / 1000));

        Log.e("downFile", "notificationId:" + notificationId);

        String name = mContext.getPackageName();

        if (download.containsKey(notificationId))
            return;

        //进度条通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builderProgress = new NotificationCompat.Builder(mContext, PUSH_CHANNEL_ID);
        } else {
            builderProgress = new NotificationCompat.Builder(mContext);
        }

        builderProgress.setContentTitle("下载中");
        builderProgress.setSmallIcon(android.R.drawable.stat_sys_download);
        builderProgress.setTicker("进度条通知");
        builderProgress.setWhen(System.currentTimeMillis());
        builderProgress.setDefaults(Notification.DEFAULT_LIGHTS);
        builderProgress.setProgress(100, 0, false);
        final Notification notification = builderProgress.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        //发送一个通知
        notificationManager.notify(notificationId, notification);

        // test

        // showNotificationProgress(mContext);

        download.put(notificationId, 0);

        // 启动线程开始执行下载任务
        downFile(url, notificationId, name);
    }

    // 下载更新文件
    private static void downFile(final String url, final int notificationId, final String name) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                File tempFile = null;
                try {

                    InputStream is;
                    long length;
                    if (Build.VERSION.SDK_INT >= 9) {
                        URL parseUrl = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) parseUrl.openConnection();
                        conn.connect();
                        length = conn.getContentLength();
                        is = conn.getInputStream();
                    } else {
                        HttpClient client = new DefaultHttpClient();
                        // params[0]代表连接的url
                        HttpGet get = new HttpGet(url);
                        HttpResponse response = client.execute(get);
                        HttpEntity entity = response.getEntity();
                        length = entity.getContentLength();
                        is = entity.getContent();
                    }

                    if (is != null) {
                        File rootFile = new File(Environment.getExternalStorageDirectory(), "/temp_app_update");
                        if (!rootFile.exists() && !rootFile.isDirectory())
                            rootFile.mkdirs();

                        //tempFile = new File(Environment.getExternalStorageDirectory(), "/ddd/" + url.substring(url.lastIndexOf("/"), url.indexOf("?")) + "_" + notificationId + ".apk");
                        tempFile = new File(Environment.getExternalStorageDirectory(), "/temp_app_update/" + "app_update.apk");
                        if (tempFile.exists())
                            tempFile.delete();
                        tempFile.createNewFile();

                        // 已读出流作为参数创建一个带有缓冲的输出流
                        BufferedInputStream bis = new BufferedInputStream(is);

                        // 创建一个新的写入流，讲读取到的图像数据写入到文件中
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        // 已写入流作为参数创建一个带有缓冲的写入流
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        int read;
                        long count = 0;
                        int precent = 0;
                        byte[] buffer = new byte[1024];
                        while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
                            bos.write(buffer, 0, read);
                            count += read;
                            precent = (int) (((double) count / length) * 100);

                            // 每下载完成1%就通知任务栏进行修改下载进度
                            if (precent - download.get(notificationId) >= 1) {

                                download.put(notificationId, precent);

                                Message message = myHandler.obtainMessage(3, precent);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                message.setData(bundle);
                                message.arg1 = notificationId;
                                myHandler.sendMessage(message);
                            }
                        }
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();
                        is.close();
                        bis.close();
                    }

                    if (!cancelUpdate) {
                        Message message = myHandler.obtainMessage(2, tempFile);
                        message.arg1 = notificationId;
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    } else {
                        tempFile.delete();
                    }
                } catch (IOException e) {
                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
//                    Message message = myHandler.obtainMessage(4, name + "下载失败：网络异常！");
                    Message message = myHandler.obtainMessage(4, name + "下载失败：文件传输异常");
                    message.arg1 = notificationId;
                    myHandler.sendMessage(message);
                } catch (Exception e) {
                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
                    Message message = myHandler.obtainMessage(4, name + "下载失败," + e.getMessage());
                    message.arg1 = notificationId;
                    myHandler.sendMessage(message);
                }
            }
        });
    }

    /* 事件处理类 */
    class MyHandler extends Handler {
        private Context context;

        public MyHandler(Looper looper, Context c) {
            super(looper);
            this.context = c;
        }

        @Override
        public void handleMessage(Message msg) {
            PendingIntent contentIntent = null;
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        download.remove(msg.arg1);
                        break;
                    case 1:
                        break;
                    case 2:

                        //更新进度条
                        builderProgress.setProgress(100, 100, false);
                        //再次通知
                        notificationManager.notify(msg.arg1, builderProgress.build());

                        //进度条退出
                        notificationManager.cancel(notificationId);

                        // 下载完成后清除所有下载信息，执行安装提示
                        download.remove(msg.arg1);
                        notificationManager.cancel(msg.arg1);
                        IntentUtil.instanll((File) msg.obj, context);
                        break;
                    case 3:

                        int progress = download.get(msg.arg1);

                        Log.e("tag", "progress:" + progress);

                        if (progress <= 100) {

                            builderProgress.setContentTitle("正在下载, 已下载" + progress + "%");
                        }

                        //更新进度条
                        builderProgress.setProgress(100, progress, false);

                        //再次通知
                        notificationManager.notify(notificationId, builderProgress.build());

                        break;
                    case 4:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        download.remove(msg.arg1);
                        notificationManager.cancel(msg.arg1);
                        break;
                }
            }
        }
    }

    /**
     * 显示一个下载带进度条的通知 测试使用
     *
     * @param context 上下文
     */
    public static void showNotificationProgress(Context context) {
        //进度条通知
        builderProgress = new NotificationCompat.Builder(context, "2");
        builderProgress.setContentTitle("正在下载中");
        builderProgress.setSmallIcon(android.R.drawable.stat_sys_download);
        builderProgress.setTicker("进度条通知");
        builderProgress.setPriority(PRIORITY_HIGH);
        builderProgress.setProgress(100, 0, false);
        builderProgress.setChannelId("123");
        final Notification notification = builderProgress.build();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //发送一个通知
        notificationManager.notify(notificationId, notification);
        /**创建一个计时器,模拟下载进度**/
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            int progress = 0;
//
//            @Override
//            public void run() {
//                Log.i("progress", progress + "");
//                while (progress <= 100) {
//                    progress++;
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (progress <= 100) {
//
//                        builderProgress.setContentTitle("正在下载中, 已下载" + progress + "%");
//                    }
//
//                    //更新进度条
//                    builderProgress.setProgress(100, progress, false);
//                    //再次通知
//                    notificationManager.notify(notificationId, builderProgress.build());
//                }
//                //计时器退出
//                this.cancel();
//                //进度条退出
//                notificationManager.cancel(notificationId);
//                return;//结束方法
//            }
//        }, 0);
    }
}

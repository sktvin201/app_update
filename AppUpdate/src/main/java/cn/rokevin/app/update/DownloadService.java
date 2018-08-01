package cn.rokevin.app.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

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

public class DownloadService extends Service {

    private static NotificationManager nm;
    private static Notification notification;
    private static boolean cancelUpdate = false;
    private static MyHandler myHandler;
    private static ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
    public static Map<Integer, Integer> download = new HashMap<Integer, Integer>();
    public static Context mContext;

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
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myHandler = new MyHandler(Looper.myLooper(), DownloadService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void downNewFile(final String url, final int notificationId, final String name) {
        if (download.containsKey(notificationId))
            return;
        notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
        // notification.icon=android.R.drawable.stat_sys_download_done;
        notification.tickerText = name + "开始下载";
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //显示在“正在进行中”
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, new Intent(Intent.ACTION_VIEW), 0);
        notification.setLatestEventInfo(mContext, name, "0%", contentIntent);
        download.put(notificationId, 0);
        // 将下载任务添加到任务栏中
        nm.notify(notificationId, notification);
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
                        File rootFile = new File(Environment.getExternalStorageDirectory(), "/ddd");
                        if (!rootFile.exists() && !rootFile.isDirectory())
                            rootFile.mkdir();

                        //tempFile = new File(Environment.getExternalStorageDirectory(), "/ddd/" + url.substring(url.lastIndexOf("/"), url.indexOf("?")) + "_" + notificationId + ".apk");
                        tempFile = new File(Environment.getExternalStorageDirectory(), "/ddd/bm/" + "merchant.apk");
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
                } catch (ClientProtocolException e) {
                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
                    Message message = myHandler.obtainMessage(4, name + "下载失败：网络异常！");
                    message.arg1 = notificationId;
                    myHandler.sendMessage(message);
                } catch (IOException e) {
                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
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

    // 安装下载后的apk文件
    private void Instanll(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
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
                        contentIntent = PendingIntent.getActivity(DownloadService.this, msg.arg1, new Intent(Intent.ACTION_VIEW), 0);
                        notification.setLatestEventInfo(DownloadService.this, msg.getData().getString("name") + "下载完成", "100%", contentIntent);
                        nm.notify(msg.arg1, notification);
                        // 下载完成后清除所有下载信息，执行安装提示
                        download.remove(msg.arg1);
                        nm.cancel(msg.arg1);
                        Instanll((File) msg.obj, context);
                        break;
                    case 3:
                        contentIntent = PendingIntent.getActivity(DownloadService.this, msg.arg1, new Intent(Intent.ACTION_VIEW), 0);
                        notification.setLatestEventInfo(DownloadService.this, msg.getData().getString("name") + "正在下载", download.get(msg.arg1) + "%", contentIntent);
                        nm.notify(msg.arg1, notification);
                        break;
                    case 4:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        download.remove(msg.arg1);
                        nm.cancel(msg.arg1);
                        break;
                }
            }
        }
    }
}

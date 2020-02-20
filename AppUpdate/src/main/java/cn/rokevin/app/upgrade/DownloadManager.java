package cn.rokevin.app.upgrade;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

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

import cn.rokevin.app.R;
import cn.rokevin.app.update.IntentUtil;

/**
 * 下载管理
 */
public class DownloadManager implements DownloadProgressListener {

    private static final String TAG = DownloadManager.class.getSimpleName();
    private DownloadInfo info;
    private ProgressListener progressObserver;
    private static File pathFile;
    private static ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
    private static Map<Integer, Integer> download = new HashMap<>();
    private Context context;
    private String path;
    private String appName = "upgrade";
    private boolean cancel = false;

    private static MyHandler myHandler;

    public DownloadInfo getInfo() {
        return info;
    }

    public void setInfo(DownloadInfo info) {
        this.info = info;
    }

    public String getFilePath() {

        return path + "/" + info.getFileName() + "." + info.getFileType();
    }

    public DownloadManager(Context context) {

        this.context = context;
        path = context.getCacheDir().getAbsolutePath() + "/downloads";

        myHandler = new MyHandler(Looper.myLooper(), context);

        String name = context.getString(R.string.app_name);

        if (!TextUtils.isEmpty(name)) {
            appName = name;
        }

        Log.e(TAG, "appName:" + appName);

        info = new DownloadInfo();
        info.setPath(path);
        info.setFileName(appName);
        info.setFileType("apk"); // 默认写死
        info.setStatus(DownloadInfo.WAIT_DOWNLOAD);
        info.setMessage("等待下载");
    }

    public void cancel() {
        cancel = true;
    }

    // 下载更新文件
    public void download(String url) {

        download(url, "");
    }

    // 下载更新文件
    public void download(final String url, final String name) {

        if (!TextUtils.isEmpty(name)) {
            appName = name;
            info.setFileName(name);
        }

        final int downloadTimestamp = ((int) (System.currentTimeMillis() / 1000));

        if (download.containsKey(downloadTimestamp))
            return;

        sendMessage(info);

        download.put(downloadTimestamp, 0);

        info.setDownloadTimestamp(downloadTimestamp);
        info.setUrl(url);

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                File tempFile = null;

                try {

                    InputStream is;
                    long length;
                    URL parseUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) parseUrl.openConnection();
                    conn.connect();
                    length = conn.getContentLength();
                    is = conn.getInputStream();

                    // 设置下载总长度
                    info.setContentLength(length);

                    if (is != null) {

                        File dirFile = new File(info.getPath());

                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }

                        tempFile = new File(dirFile, info.getFileName() + "." + info.getFileType());
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
                        while ((read = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, read);
                            count += read;
                            precent = (int) (((double) count / length) * 100);

                            info.setReadLength(count);
                            info.setPercent(precent);
                            info.setStatus(DownloadInfo.DOWNLOADING);
                            info.setMessage("下载中...");

                            // 每下载完成1%就通知任务栏进行修改下载进度
                            if (precent - download.get(downloadTimestamp) >= 1) {

                                download.put(downloadTimestamp, precent);

                                sendMessage(info);
                            }
                        }
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();
                        is.close();
                        bis.close();
                    }

                    info.setPercent(100);
                    info.setStatus(DownloadInfo.FINISH);
                    info.setMessage("下载完成");

                    sendMessage(info);

                } catch (IOException e) {

                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
                    info.setStatus(DownloadInfo.ERORR);
                    info.setMessage("下载文件失败，文件传输异常");

                    sendMessage(info);

                } catch (Exception e) {

                    if (null != tempFile && tempFile.exists())
                        tempFile.delete();
                    info.setStatus(DownloadInfo.ERORR);
                    info.setMessage("下载文件失败，" + e.getMessage());

                    sendMessage(info);
                }
            }
        });
    }

    /**
     * 安装更新包
     */
    public void install() {
        String filePath = getFilePath();
        IntentUtil.instanll(new File(filePath), context);
    }

    @Override
    public void progress(DownloadInfo info) {

        long readLength = info.getReadLength();
        long contentLength = info.getContentLength();

        Log.e("progress : ", "read = " + readLength + "contentLength = " + contentLength);
        // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
        // 如果断电续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
        // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
        // 之前读取的加上现在读取的
        if (contentLength > contentLength) {
            readLength = readLength + (contentLength - contentLength);
        } else {
            info.setContentLength(contentLength);
        }
        info.setReadLength(readLength);

        sendMessage(info);
    }

    public void sendMessage(DownloadInfo info) {

        Message message = myHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppBundle.DOWNLOAD_INFO, info);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    /**
     * 监听下载信息
     */
    public interface ProgressListener {
        void progressChanged(DownloadInfo info);
    }

    public void setProgressListener(ProgressListener progressObserver) {
        this.progressObserver = progressObserver;
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
            super.handleMessage(msg);

            if (msg != null) {

                Bundle data = msg.getData();
                DownloadInfo info = (DownloadInfo) data.getSerializable(AppBundle.DOWNLOAD_INFO);

                if (progressObserver != null) {
                    progressObserver.progressChanged(info);
                }
            }
        }
    }
}

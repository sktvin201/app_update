package cn.rokevin.app.upgrade.net;

import android.os.Build;
import android.util.Log;

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

public class Network {

    private static final String TAG = Network.class.getSimpleName();

    public static InputStream getInputStream(String url) throws Exception {

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

        return is;
    }

    public static void testDownLoad(final String url, final String savePath) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(savePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;

                String HTTP_URL = url;

                try {
                    int contentLength = getConnection(HTTP_URL).getContentLength();
                    Log.e(TAG, "文件的大小是:" + contentLength);
                    if (contentLength > 32) {
                        InputStream is = getConnection(HTTP_URL).getInputStream();
                        bis = new BufferedInputStream(is);
                        FileOutputStream fos = new FileOutputStream(savePath);
                        bos = new BufferedOutputStream(fos);
                        int b = 0;
                        byte[] byArr = new byte[1024];
                        while ((b = bis.read(byArr)) != -1) {
                            bos.write(byArr, 0, b);
                        }
                        Log.e(TAG, "下载的文件的大小是----------------------------------------------:" + contentLength);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bis != null) {
                            bis.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public static HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.connect();
        return connection;
    }
}

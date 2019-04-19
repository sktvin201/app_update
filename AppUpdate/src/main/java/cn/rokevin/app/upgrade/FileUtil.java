package cn.rokevin.app.upgrade;

public class FileUtil {

    public static void saveFile() {

    }

//    public static void write(InputStream inputStream) {
//
//        if (is != null) {
//            File rootFile = new File();
//            if (!rootFile.exists() && !rootFile.isDirectory())
//                rootFile.mkdirs();
//
//            //tempFile = new File(Environment.getExternalStorageDirectory(), "/ddd/" + url.substring(url.lastIndexOf("/"), url.indexOf("?")) + "_" + notificationId + ".apk");
//            tempFile = new File(context.getExternalCacheDir(), "/downloads/app/" + "app_update.apk");
//            if (tempFile.exists())
//                tempFile.delete();
//            tempFile.createNewFile();
//
//            // 已读出流作为参数创建一个带有缓冲的输出流
//            BufferedInputStream bis = new BufferedInputStream(is);
//
//            // 创建一个新的写入流，讲读取到的图像数据写入到文件中
//            FileOutputStream fos = new FileOutputStream(tempFile);
//            // 已写入流作为参数创建一个带有缓冲的写入流
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//            int read;
//            long count = 0;
//            int precent = 0;
//            byte[] buffer = new byte[1024];
//            while ((read = bis.read(buffer)) != -1) {
//                bos.write(buffer, 0, read);
//                count += read;
//                precent = (int) (((double) count / length) * 100);
//
//                // 每下载完成1%就通知任务栏进行修改下载进度
//                if (precent - download.get(downloadTimestamp) >= 1) {
//
//                    download.put(downloadTimestamp, precent);
//                }
//            }
//            bos.flush();
//            bos.close();
//            fos.flush();
//            fos.close();
//            is.close();
//            bis.close();
//        }
//    }
}

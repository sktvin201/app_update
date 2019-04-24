package cn.rokevin.app.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class IntentUtil {

    // 安装下载后的apk文件
    public static void instanll(File file, Context context) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

//        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider.app.update", file);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        }

        // TODO: 2019/4/22 test
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

        context.startActivity(intent);
    }
}

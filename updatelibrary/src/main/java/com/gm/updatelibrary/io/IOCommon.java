package com.gm.updatelibrary.io;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by zhanggangmin on 2017/2/15.
 */

public class IOCommon {

    /**
     * @return 返回SD卡可用容量  如果没有权限返回-1
     */
    public static long getUsableStorage() {
        String sDcString = Environment.getExternalStorageState();
        // 判断SD卡是否存在，并且是否具有读写权限
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();

            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

            // 获取可供程序使用的Block的数量
            @SuppressWarnings("deprecation")
            long nAvailaBlock = statfs.getAvailableBlocks();

            @SuppressWarnings("deprecation")
            long nBlocSize = statfs.getBlockSize();

            // 计算 SDCard 剩余大小MB
            return nAvailaBlock * nBlocSize / 1024 / 1024;
        } else {
            return -1;
        }

    }


    public static File newfile(Context context) {
        String  s=Environment.getExternalStorageState();
        File updataFile = null;
        File saveFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
             updataFile = context.getExternalFilesDir("updata");
              saveFile=new File(updataFile,"1.apk");
//            String SD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//            PackageManager pm = context.getPackageManager();
//            String appName = context.getApplicationInfo().loadLabel(pm).toString();
//        final File file = new File(sd.getPath() + "/" + appName);
//            File file = new File(SD + appName);
//            file.mkdirs();
//            File file2 = new File(SD + appName + "/" + "1.jpg");
//            try {
//                file2.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return file;
        }
        return saveFile;
    }

//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path) {
        if (path == null) {
            return -1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        } else {
            if (!path.exists()) {
                return 0;
            } else {
                final StatFs stats = new StatFs(path.getPath());
                return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
            }
        }
    }

    /**
     * 启动安装替换apk
     *
     * @param activity 启动界面
     * @param apkpath  apk文件所在路径
     */
    public static void ReplaceLaunchApk(Activity activity, String apkpath) {
        File file = new File(apkpath);
        if (file.exists()) {
//            Log.d("update", file.getName());
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            activity.startActivity(intent);
            activity.finish();
            file = null;
        } else {
//            Log.e("update", "File not exsit:" + apkpath);
        }

    }
}

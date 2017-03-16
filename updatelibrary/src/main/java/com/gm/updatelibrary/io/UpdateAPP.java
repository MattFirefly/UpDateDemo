package com.gm.updatelibrary.io;

import android.content.Context;

import com.gm.updatelibrary.model.DownloadFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hugo.weaving.DebugLog;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zhanggangmin on 2017/2/14.
 */

public class UpdateAPP  {

    private String auth;
    private String path = "http://p.gdown.baidu.com/0a96a07252e5349b3f3d1b286d332a1a850ca6d0ba2e112e06616c97133af91b2103304f39e08443a087d0f434edad452d111ec1c15ac7a5faecdb8f7a41e6b4982ea74a21a68252e25255688ffc1cf3c637da5e2183f2aae2ea5cccece512c5d92fe4a99f0bfa6c695de398887ccb942439c81071935897f2133fdb92fb253d93a30f3d9bf9e54f1464bd871aac56dfc58f29b5df2eeb112ac01969bd8b07c5682b45931fb0a63328f6a93b03983bd8d3cc1510e87e4876fac2948e879fbf5bb14b801b8500fbf247430ecd1b6d85a5e9d8cdaebe150c64a394083c2ddab19382b99fd7d704f135";
//    private String img = "http://125.91.249.37/apk.r1.market.hiapk.com/data/upload/apkres/2017/2_23/15/com.tencent.tmgp.sgame_032612.apk?wsiphost=local";
    private int downloadLength;
    private boolean isCompleted;
    // 线程池中默认线程的个数为5
    private int worker_num = 4;

    public void setWorker_num(int worker_num) {
        this.worker_num = worker_num;
    }

    public void binding(final Context context) {
        //检查文件读取权限
        long storageSize = IOCommon.getUsableStorage();
        if (storageSize < 0) {//说明没有权限
            // TODO: 2017/3/1 提示或者弹窗，或者首次弹窗以后不弹窗，或者跳转到权限开启位置
            return;
        }


        // TODO: 2017/3/1 做容量大小判断
        new Thread(new Runnable() {
            Realm realm;

            @Override
            public void run() {
                final long fileSize = getFileSize(path);//第一次请求去获取头信息数据
                realm = Realm.getDefaultInstance();
                try {
                    RealmResults<DownloadFile> userList = realm.where(DownloadFile.class)
                            .equalTo("fileSize", fileSize).findAll();
                    // TODO: 2017/3/8 这个判断条件最好是md5
                    if (userList.size() > 0) { //有说明已经下载了
                        DownloadFile downloadFile = userList.get(0);
                        multithread(new File(downloadFile.getFilePath()), downloadFile.getUrl(), downloadFile.getThreadQuantity(), downloadFile.getFileSize());
                    } else {
                        final File file = IOCommon.newfile(context);//新建文件
                     //保存下载配置
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                DownloadFile downloadFile = realm.createObject(DownloadFile.class);
                                downloadFile.setFileSize(fileSize);
                                downloadFile.setUrl(path);
                                downloadFile.setThreadQuantity(worker_num);
                                downloadFile.setFilePath(file.getPath());
                            }
                        });
                        multithread(file, path, worker_num, fileSize);//下载
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }
        }).start();
    }

    /**
     * @param path 需要获取头信息的路径，就是下载的路径
     * @return 下载文件的大小
     */
    @DebugLog
    public long getFileSize(String path) {
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        try {
            url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);//设置连接主机超时（单位：毫秒）
            httpURLConnection.setReadTimeout(20000);//设置从主机读取数据超时（单位：毫秒）
            httpURLConnection.setRequestProperty("User-Agent", "NetFox");
            httpURLConnection.setRequestProperty("Cookie", "auth=" + auth);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                return httpURLConnection.getContentLength();//根据响应获取文件大小
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param outFile  输出的文件
     * @param startPos
     * @param endPos
     * @throws IOException            连接超时
     * @throws InterruptedIOException 用户终止了下载
     */
    private void download(File outFile, long startPos, long endPos,ProgressCallback callback) {
        long nowPos=startPos;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bis = null;
        BufferedRandomAccessFile raf = null;
        InputStream inputStream = null;
        // 通过文件创建输出流对象RandomAccessFile,r:读 w:写 d:删除
        try {
            raf = new BufferedRandomAccessFile(outFile, "rw");
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10 * 1000);//设置连接主机超时（单位：毫秒）
            httpURLConnection.setReadTimeout(10 * 1000);//设置从主机读取数据超时（单位：毫秒）
            httpURLConnection.setRequestProperty("User-Agent", "NetFox");
            //设置当前线程下载的起点、终点
            httpURLConnection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            httpURLConnection.setRequestProperty("Cookie", "auth=" + auth);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 206) {
                inputStream = httpURLConnection.getInputStream();
                if (inputStream == null) {
                    throw new RuntimeException("stream is null");
                }

                byte[] buffer = new byte[1024];
                bis = new BufferedInputStream(inputStream);
                raf.seek(startPos);
                int len = 0;

                while ((len = bis.read(buffer, 0, 1024)) != -1) {
                    raf.write(buffer, 0, len);
                    downloadLength += len;
                    nowPos+=len;
//                    onProgressCallback(downloadLength);//回调
                    callback.onProgressCallback(nowPos);// TODO: 2017/3/16 这个要回调两个参数了 一个now一个len
                }
                isCompleted = true;
            } else {
                throw new RuntimeException("文件下载失败 httpResponseCode=" + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            httpURLConnection.disconnect();
                        }
                    }
                }
            }
        }
    }

    public void multithread(File outFile, String path, int threadQuantity, long fileLength) {
        long block =  fileLength / threadQuantity; //计算线程下载量
//        if (fileLength % threadQuantity == 0) {
//            for (int i = 0; i < threadQuantity; i++) {
//                new DownLoadThread(outFile,i+1, (block * i), block + (block * i), path).start();
//            }
//        } else {
            for (int i = 0; i < threadQuantity - 1; i++) {
                new DownLoadThread(outFile, i+1, (block * i), block + (block * i)-1, path).start();
            }
            DownLoadThread downLoadThread  =new DownLoadThread(outFile,threadQuantity, block*(threadQuantity-1), fileLength, path);
            downLoadThread.start();
            // TODO: 2017/3/16 这个回调的地方判断一下如果等于目标大小，则表示下次完成 做完整性验证操作等其他业务 然后累计的地方加同步锁
//        }

    }


    private  class DownLoadThread extends Thread {
        private File outFile;
        private int threadID;
        private long startIndex;
        private long endIndex;
        //线程当前下载的位置
        private long currentPosition;
        //上一次下载的大小
        private long lastDownSize;
        private String url;

        public DownLoadThread(File outFile, int threadID, long startIndex, long endIndex, String URL) {
            this.outFile = outFile;
            this.threadID = threadID;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.url = URL;
            System.out.println("startIndex="+startIndex+" endIndex="+endIndex);
        }

        public void run() {
            try {
                download(outFile, startIndex, endIndex, new ProgressCallback() {
                    @Override
                    public void onProgressCallback(long Position) {
//                        System.out.println(DownLoadThread.this.toString()+" callback:"+Position);
                        // TODO: 2017/3/16 数据写入保存threadID 和当前Position到数据库 然后再回调外部len
                    }
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


}

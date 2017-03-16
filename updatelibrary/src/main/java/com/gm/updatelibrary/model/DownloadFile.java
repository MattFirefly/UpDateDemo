package com.gm.updatelibrary.model;

import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

/**
 * 项目名称：UpDateDemo
 * 类描述：
 * 创建人：zhanggangmin
 * 创建时间：2017/3/7 17:40
 * 修改人：zhanggangmin
 * 修改时间：2017/3/7 17:40
 * 修改备注：
 */
@RealmClass
public class DownloadFile implements RealmModel {

//    @PrimaryKey
    @Ignore
    private long id;
    private long fileSize;
    private String url;
    private String md5;
    private int threadQuantity;
    private String filePath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getThreadQuantity() {
        return threadQuantity;
    }

    public void setThreadQuantity(int threadQuantity) {
        this.threadQuantity = threadQuantity;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

package com.gm.updatelibrary.db;

import com.gm.updatelibrary.model.DownloadFile;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 项目名称：UpDateDemo
 * 类描述：
 * 创建人：zhanggangmin
 * 创建时间：2017/3/7 23:02
 * 修改人：zhanggangmin
 * 修改时间：2017/3/7 23:02
 * 修改备注：
 */
public class DBDownloadFIle {
    private static final String TAG = "REALM";
    private static DBDownloadFIle ourInstance = new DBDownloadFIle();
    private Realm realm;

    private DBDownloadFIle() {
        realm = Realm.getDefaultInstance();
    }

    public static DBDownloadFIle getInstance() {
        return ourInstance;
    }

    public void addDBDownFlie(final long size, final String url) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DownloadFile downloadFile = realm.createObject(DownloadFile.class);
                downloadFile.setFileSize(size);
                downloadFile.setUrl(url);
            }
        });
    }


    public boolean isDBDownFlieExist(long size){
        // TODO: 2017/3/7 应该判断md5的
        RealmResults<DownloadFile> userList = realm.where(DownloadFile.class)
                .equalTo("size", size).findAll();
        if (userList.size()>0){
            return true;
        }
        return false;
    }


    public void close() {
        // Close the Realm instance.
        realm.close();
    }
}

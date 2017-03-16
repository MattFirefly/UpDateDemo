package com.gm.updatelibrary;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.content.ContentValues.TAG;

/**
 * 项目名称：UpDateDemo
 * 类描述：
 * 创建人：zhanggangmin
 * 创建时间：2017/3/8 11:31
 * 修改人：zhanggangmin
 * 修改时间：2017/3/8 11:31
 * 修改备注：
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("myrealm.realm") //文件名
                .schemaVersion(0) //版本号
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //只是看个路径
        String str = Realm.getDefaultInstance().getPath();
        Log.i(TAG, str);
    }
}

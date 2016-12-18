package com.heiliuer.fuda_zaoan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.heiliuer.fuda_zaoan.dao.DaoMaster;
import com.heiliuer.fuda_zaoan.dao.DaoSession;
import com.heiliuer.fuda_zaoan.dao.SignRecordDao;


/**
 * Created by Administrator on 2016/5/12 0012.
 */
public class DbHandler {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public DbHandler(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "main", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public SignRecordDao getSoftwareDao() {
        return daoSession.getSignRecordDao();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

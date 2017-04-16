package cn.ucai.live.data.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.live.LiveApplication;

/**
 * Created by maestro on 17-4-16.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static DBHelper instance;

    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + GiftDao.GIFT_TABLE_NAME + " ("
            + GiftDao.GIFT_COLUMN_NAME + " TEXT, "
            + GiftDao.GIFT_COLUMN_URL + " TEXT, "
            + GiftDao.GIFT_COLUMN_PRICE + " INTEGER, "
            + GiftDao.GIFT_COLUMN_ID + " INTEGER PRIMARY KEY)";

    public DBHelper(Context context) {
        super(context, getUserDBName(), null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context con) {
        if (instance == null) {
            instance = new DBHelper(con.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDBName() {
        return LiveApplication.getInstance().getPackageName()+"_live.db";
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void closeDB() {
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
        }
    }
}

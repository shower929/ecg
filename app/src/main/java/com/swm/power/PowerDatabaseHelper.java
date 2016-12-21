package com.swm.power;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swm.training.TrainingModel;

/**
 * Created by yangzhenyu on 2016/10/18.
 */

class PowerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "swm_power.db";
    private static final int VERSION = 2;
    static final String INT_TYPE = " INTEGER ";
    static final String DATETIME_TYPE = " DATETIME ";
    static final String COMMA_SEP = " , ";

    PowerDatabaseHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    public PowerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    public PowerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PowerModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PowerModel.TABLE_NAME);
    }
}

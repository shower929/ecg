package com.swm.training;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yangzhenyu on 2016/10/15.
 */

class TrainingDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "swm_training.db";
    static final String TEXT_TYPE = " TEXT ";
    static final String INT_TYPE = " INTEGER ";
    static final String COMMA_SEP = ",";

    TrainingDatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, VERSION);
    }

    TrainingDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    TrainingDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TrainingModel.CREATE_TABLE);
        insertPrebuiltModel(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TrainingModel.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TrainingData.TABLE);
        onCreate(db);
    }

    private void insertPrebuiltModel(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TrainingModel.Column.TITLE, "Exercise your body");
        values.put(TrainingModel.Column.BRIEF, "Keeping exercise, keep your body active");
        values.put(TrainingModel.Column.DESC, "Keeping your exercise intensity to HARD as long as possible");
        db.insert(TrainingModel.TABLE, null, values);
    }
}

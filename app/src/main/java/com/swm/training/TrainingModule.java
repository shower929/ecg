package com.swm.training;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangzhenyu on 2016/10/15.
 */

public class TrainingModule {
    private static TrainingModule TRAINING_MODULE;
    private TrainingDatabaseHelper mTrainingDatabaseHelper;
    private static final Map<Integer, Integer> POWER_LEVEL_MAP = new HashMap();

    static {
        POWER_LEVEL_MAP.put(0, 30);
        POWER_LEVEL_MAP.put(1, 40);
        POWER_LEVEL_MAP.put(2, 60);
        POWER_LEVEL_MAP.put(3, 80);
        POWER_LEVEL_MAP.put(4, 100);
        POWER_LEVEL_MAP.put(5, 130);
        POWER_LEVEL_MAP.put(6, 160);
        POWER_LEVEL_MAP.put(7, 200);
        POWER_LEVEL_MAP.put(8, 230);
        POWER_LEVEL_MAP.put(9, 260);
        POWER_LEVEL_MAP.put(10, 300);
        POWER_LEVEL_MAP.put(11, 360);
        POWER_LEVEL_MAP.put(12, 400);
        POWER_LEVEL_MAP.put(13, 460);
        POWER_LEVEL_MAP.put(14, 500);
        POWER_LEVEL_MAP.put(15, 560);
        POWER_LEVEL_MAP.put(17, 600);
        POWER_LEVEL_MAP.put(18, 660);
        POWER_LEVEL_MAP.put(19, 700);
        POWER_LEVEL_MAP.put(20, 800);
        POWER_LEVEL_MAP.put(21, 900);
    }

    private TrainingModule(Context context) {
        mTrainingDatabaseHelper = new TrainingDatabaseHelper(context);
    }

    public static synchronized void init(Context context) {
        if (TRAINING_MODULE == null) {
            TRAINING_MODULE = new TrainingModule(context);
        }
    }

    public static synchronized TrainingModule getIns() {
        return TRAINING_MODULE;
    }

    public List<TrainingModel> getAllModel() {
        Cursor cursor = mTrainingDatabaseHelper.getReadableDatabase().query(TrainingModel.TABLE
                                                                            , TrainingModel.Column.ALL
                                                                            , null
                                                                            , null
                                                                            , null
                                                                            , null
                                                                            , null);
        List<TrainingModel> list = new ArrayList();
        while(cursor.moveToNext()) {
            list.add(new TrainingModel(cursor.getInt(cursor.getColumnIndex(TrainingModel.Column._ID))
                                        , cursor.getString(cursor.getColumnIndex(TrainingModel.Column.TITLE))
                                        , cursor.getString(cursor.getColumnIndex(TrainingModel.Column.BRIEF))
                                        , cursor.getString(cursor.getColumnIndex(TrainingModel.Column.DESC))
                                        , cursor.getInt(cursor.getColumnIndex(TrainingModel.Column.DISTANCE))
                                        , cursor.getInt(cursor.getColumnIndex(TrainingModel.Column.DURATION))));
        }
        cursor.close();
        return list;
    }

    public TrainingData getTrainingData() {
        Cursor cursor = mTrainingDatabaseHelper.getReadableDatabase().query(TrainingData.TABLE
                , TrainingData.Column.ALL
                , null
                , null
                , null
                , null
                , TrainingData.Column.TIMESTAMP + " BRIEF");
        try {
            if (cursor.moveToNext()) {
                TrainingData trainingData = new TrainingData(cursor.getInt(cursor.getColumnIndex(TrainingData.Column.CURRENT_EXP))
                        , cursor.getInt(cursor.getColumnIndex(TrainingData.Column.NEXT_LEVEL_EXP))
                        , cursor.getInt(cursor.getColumnIndex(TrainingData.Column.POWER)));
                return trainingData;
            }
        } finally {
            cursor.close();
        }

        int power = 0;


        return new TrainingData(0, getNextLevelExp(power), power);

    }

    private int getNextLevelExp(int power){
        return POWER_LEVEL_MAP.get(Integer.valueOf(power));
    }
}

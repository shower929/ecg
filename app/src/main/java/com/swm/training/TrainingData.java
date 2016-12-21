package com.swm.training;

import android.provider.BaseColumns;

import static com.swm.training.TrainingDatabaseHelper.COMMA_SEP;
import static com.swm.training.TrainingDatabaseHelper.INT_TYPE;

/**
 * Created by yangzhenyu on 2016/10/16.
 */

public class TrainingData {
    static final String TABLE = "training_data";
    static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
            + Column._ID + INT_TYPE + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
            + Column.CURRENT_EXP + INT_TYPE + COMMA_SEP
            + Column.NEXT_LEVEL_EXP + INT_TYPE + COMMA_SEP
            + Column.POWER + INT_TYPE + COMMA_SEP
            + Column.TIMESTAMP + ")";

    static class Column implements BaseColumns {
        static final String[] ALL = {Column.CURRENT_EXP, Column.NEXT_LEVEL_EXP, Column.POWER};
        static final String CURRENT_EXP = "current_exp";
        static final String POWER = "power";
        static final String NEXT_LEVEL_EXP = "next_level_exp";
        static final String TIMESTAMP = "timestamp";
    }

    public final int currentExp;
    public final int nextLevelExp;
    public final int power;

    TrainingData(int currentExp, int nextLevelExp, int power) {
        this.currentExp = currentExp;
        this.nextLevelExp = nextLevelExp;
        this.power = power;
    }
}

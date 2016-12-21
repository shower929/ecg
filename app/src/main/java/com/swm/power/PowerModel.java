package com.swm.power;

import android.provider.BaseColumns;

import static com.swm.power.PowerDatabaseHelper.COMMA_SEP;
import static com.swm.power.PowerDatabaseHelper.DATETIME_TYPE;
import static com.swm.power.PowerDatabaseHelper.INT_TYPE;

/**
 * Created by yangzhenyu on 2016/10/18.
 */
public class PowerModel {
    public final int power;
    public final int distance;
    public final int elapse;
    public final long timestamp;

    PowerModel(int power, int distance, int elapse, long timestamp) {
        this.power = power;
        this.distance = distance;
        this.elapse = elapse;
        this.timestamp = timestamp;
    }

    static class Columns implements BaseColumns {
        static final String POWER = "power";
        static final String DISTANCE = "distance";
        static final String ELAPSE = "elapse";
        static final String TIMESTAMP = "timestamp";
        static final String[] ALL = {POWER, DISTANCE, ELAPSE, TIMESTAMP};
    }
    static final String TABLE_NAME = "power";
    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + Columns.POWER + INT_TYPE + COMMA_SEP
            + Columns.DISTANCE + INT_TYPE + COMMA_SEP
            + Columns.ELAPSE + INT_TYPE + COMMA_SEP
            + Columns.TIMESTAMP + DATETIME_TYPE
            + ")";
}

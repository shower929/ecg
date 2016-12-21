package com.swm.training;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import static com.swm.training.TrainingDatabaseHelper.COMMA_SEP;
import static com.swm.training.TrainingDatabaseHelper.INT_TYPE;
import static com.swm.training.TrainingDatabaseHelper.TEXT_TYPE;

/**
 * Created by yangzhenyu on 2016/10/13.
 */

public class TrainingModel implements Parcelable{

    static final String TABLE = "training_model";

    protected TrainingModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        brief = in.readString();
        description = in.readString();
        distance = in.readInt();
        duration = in.readInt();
    }

    public static final Creator<TrainingModel> CREATOR = new Creator<TrainingModel>() {
        @Override
        public TrainingModel createFromParcel(Parcel in) {
            return new TrainingModel(in);
        }

        @Override
        public TrainingModel[] newArray(int size) {
            return new TrainingModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(brief);
        dest.writeString(description);
        dest.writeInt(distance);
        dest.writeInt(duration);
    }

    static class Column implements BaseColumns {

        static final String TITLE = "title";
        static final String BRIEF = "brief";
        static final String DESC = "desc";
        static final String DISTANCE = "distance";
        static final String DURATION = "duration";

        static final String[] ALL = {_ID
                , TITLE
                , BRIEF
                , DESC
                , DISTANCE
                , DURATION};
    }
    static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
            + Column._ID + INT_TYPE + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
            + Column.TITLE + TEXT_TYPE + COMMA_SEP
            + Column.BRIEF + TEXT_TYPE + COMMA_SEP
            + Column.DESC + TEXT_TYPE + COMMA_SEP
            + Column.DISTANCE + INT_TYPE + COMMA_SEP
            + Column.DURATION + INT_TYPE + ")";

    public final int id;
    public final String title;
    public final String brief;
    public final String description;
    public final int distance;
    public final int duration;

    TrainingModel(int id, String title, String brief, String description, int distance, int duration) {
        this.id = id;
        this.title = title;
        this.brief = brief;
        this.description = description;
        this.distance = distance;
        this.duration = duration;
    }

}

package com.swm.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangzhenyu on 2016/10/30.
 */

public class SwmUser implements Parcelable {
    public final String name;
    public final String email;
    public final String tel;
    final String uid;
    public static final String KEY = "swm_user";
    public static final String UID = "swm_user_id";
    SwmUser(String name, String email, String tel, String uid) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.uid = uid;
    }

    protected SwmUser(Parcel in) {
        name = in.readString();
        email = in.readString();
        tel = in.readString();
        uid = in.readString();
    }

    public static final Creator<SwmUser> CREATOR = new Creator<SwmUser>() {
        @Override
        public SwmUser createFromParcel(Parcel in) {
            return new SwmUser(in);
        }

        @Override
        public SwmUser[] newArray(int size) {
            return new SwmUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(tel);
        dest.writeString(uid);
    }
}

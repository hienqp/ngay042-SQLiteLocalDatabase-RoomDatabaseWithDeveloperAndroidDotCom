package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.reactivex.rxjava3.annotations.NonNull;

@Entity(tableName = "users", indices = {@Index(value = {"first_name", "last_name"}, unique = true)})
public class User {
    private static final String DEFAULT_PASSWORD = "1234567890";

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "first_name")
    private String mFirstName;

    @ColumnInfo(name = "last_name")
    private String mLastName;

    @ColumnInfo(name = "password")
    private String mPassword;

    @Embedded
    private Place mPlace;

    public User() {

    }

    @Ignore
    public User(String mFirstName, String mLastName) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = DEFAULT_PASSWORD;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        if (mPlace != null) {
            return mFirstName + " " + mLastName + "\n" + mPlace.getmName();
        }

        return mFirstName + " " + mLastName;
    }
}

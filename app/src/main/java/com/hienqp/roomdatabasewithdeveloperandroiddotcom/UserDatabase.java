package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import static com.hienqp.roomdatabasewithdeveloperandroiddotcom.UserDatabase.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = DATABASE_VERSION)
public abstract class UserDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "Room-database";
    public static final int DATABASE_VERSION = 1;

    private static UserDatabase sUserDatabase;

    public abstract UserDao userDao();

    public static UserDatabase getInstance(Context context) {
        if (sUserDatabase == null) {
            sUserDatabase = Room.databaseBuilder(context, UserDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sUserDatabase;
    }
}

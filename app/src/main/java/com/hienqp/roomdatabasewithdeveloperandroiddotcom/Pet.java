package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Pet {
    @PrimaryKey
    public int petId;

    public String name;

    @ColumnInfo(name = "user_id")
    public int userId;
}

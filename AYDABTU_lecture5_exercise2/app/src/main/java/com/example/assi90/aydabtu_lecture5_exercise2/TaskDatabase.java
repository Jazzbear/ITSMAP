package com.example.assi90.aydabtu_lecture5_exercise2;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.assi90.aydabtu_lecture5_exercise2.models.Task;

@Database(entities = {Task.class}, version = 5)
@TypeConverters({Converters.class})
public abstract class TaskDatabase extends RoomDatabase {
    public abstract MyTaskDao myTaskDao();
}

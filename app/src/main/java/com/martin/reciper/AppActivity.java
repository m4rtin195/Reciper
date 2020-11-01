package com.martin.reciper;

import android.app.Application;

import androidx.room.Room;

public class AppActivity extends Application
{
    static AppDatabase db;

    @Override
    public void onCreate()
    {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Reciper_db").allowMainThreadQueries().build();
    }

    public static AppDatabase getDatabase()
    {
        return db;
    }
}

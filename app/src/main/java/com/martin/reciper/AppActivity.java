package com.martin.reciper;

import android.app.Application;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


public class AppActivity extends Application
{
    static AppDatabase db;

    static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Recipe "
                    +"ADD COLUMN videoURL TEXT");

        }
    };
    @Override
    public void onCreate()
    {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Reciper_db").allowMainThreadQueries().addMigrations(MIGRATION_1_2).build();
    }

    public static AppDatabase getDatabase()
    {
        return db;
    }
}

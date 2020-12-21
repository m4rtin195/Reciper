package com.martin.reciper;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.martin.reciper.database.AppDatabase;

import java.io.File;
import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppActivity extends Application
{
    static AppDatabase db;
    static Retrofit retrofit;

    static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("ALTER TABLE Recipe ADD COLUMN videoURL TEXT");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Recipe2` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipeName` TEXT, `recipeRating` REAL NOT NULL, `procedureText` TEXT, `ingredients` TEXT, `mediaURL` TEXT)");
            database.execSQL("INSERT INTO Recipe2 SELECT * FROM Recipe");
            database.execSQL("DROP TABLE Recipe");
            database.execSQL("ALTER TABLE Recipe2 RENAME TO Recipe");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3, 4)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Recipe2` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipeName` TEXT, `recipeRating` REAL NOT NULL, `procedureText` TEXT, `ingredients` TEXT, `mediaURI` TEXT)");
            database.execSQL("INSERT INTO Recipe2 SELECT * FROM Recipe");
            database.execSQL("DROP TABLE Recipe");
            database.execSQL("ALTER TABLE Recipe2 RENAME TO Recipe");
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Reciper_db")
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                //.fallbackToDestructiveMigration()
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public boolean repopulateDatabase(File file) //todo move to AppDatabase.java
    {
        if(file == null) {Log.e("daco", "db file null"); return false;}
        if(!file.exists()) {Log.e("daco", "empty db file"); return false;}

        Log.w("daco","going to repopulate!!!");
        /*db = null;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Reciper_db")
                .createFromFile(file)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build();

        Log.i("daco", "populated database");
        return true;*/
        return false;
    }

    public void deletedb(){Log.i("daco", "deleting db"); db=null;}

    public static AppDatabase getDatabase() {return db;}
    public static Retrofit getRetrofit() {return retrofit;}
}

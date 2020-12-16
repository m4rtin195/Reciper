package com.martin.reciper;

import android.app.Application;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.martin.reciper.database.AppDatabase;

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
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Reciper_db").allowMainThreadQueries().addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static AppDatabase getDatabase() {return db;}
    public static Retrofit getRetrofit() {return retrofit;}
}

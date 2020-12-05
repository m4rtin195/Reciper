package com.martin.reciper.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.martin.reciper.models.Recipe;

@Database(entities = {Recipe.class}, version = 2)
@TypeConverters({dbConverters.class})
public abstract class AppDatabase extends RoomDatabase
{
    public abstract dbDAO DAO();
}

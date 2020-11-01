package com.martin.reciper;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.martin.reciper.ui.dbRecipeDAO;

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({dbConverters.class})
public abstract class AppDatabase extends RoomDatabase
{
    public abstract dbRecipeDAO recipeDAO();
}

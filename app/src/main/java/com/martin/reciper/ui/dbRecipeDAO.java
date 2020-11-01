package com.martin.reciper.ui;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.martin.reciper.Recipe;

import java.util.List;

@Dao
public interface dbRecipeDAO
{
    @Insert
    void insert(Recipe recipe);

    @Query("DELETE FROM Recipe")
    void deleteAll();

    @Query("SELECT * FROM Recipe ORDER BY id ASC")
    List<Recipe> getAllRecipes();
 }

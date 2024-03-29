package com.martin.reciper.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.martin.reciper.models.Recipe;

import java.util.List;

@Dao
public interface dbDAO
{
    @Insert
    long insert(Recipe recipe);

    @Query("DELETE FROM Recipe")
    void deleteAll();

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM Recipe ORDER BY id ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM Recipe WHERE id IN (:recipeId)")
    Recipe getRecipe(int recipeId);

    @Update
    void updateRecipe(Recipe recipe);
 }

package com.martin.reciper;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Recipe
{
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "recipeName")
    private String recipeName;

    @ColumnInfo(name = "recipeRating")
    private float recipeRating;

    @ColumnInfo(name = "procedureText")
    private String procedureText;

    @ColumnInfo(name = "ingredients")
    private ArrayList<String> ingredients;


    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }

    public String getRecipeName()
    {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
    public Recipe _setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        return this;
    }

    public float getRecipeRating()
    {
        return recipeRating;
    }
    public void setRecipeRating(float recipeRating) {
        this.recipeRating = recipeRating;
    }
    public Recipe _setRecipeRating(float recipeRating) {
        this.recipeRating = recipeRating;
        return this;
    }

    public String getProcedureText()
    {
        return procedureText;
    }
    public void setProcedureText(String procedureText) {
        this.procedureText = procedureText;
    }
    public Recipe _setProcedureText(String procedureText) {
        this.procedureText = procedureText;
        return this;
    }

    public ArrayList<String> getIngredients()
    {
        return ingredients;
    }
    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
    public Recipe _setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}

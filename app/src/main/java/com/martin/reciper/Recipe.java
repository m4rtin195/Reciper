package com.martin.reciper;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Recipe implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "recipeName")
    private String recipeName;

    @ColumnInfo(name = "recipeRating")
    private float recipeRating;

    @ColumnInfo(name = "procedureText")
    private String recipeText;

    @ColumnInfo(name = "ingredients")
    private ArrayList<String> ingredients;

    @ColumnInfo(name = "videoURL")
    private String videoURL;

    public Recipe()
    {
        recipeName = "";
        recipeRating = 0.0f;
        recipeText = "";
        ingredients = new ArrayList<>();
        videoURL = "";
    }

    public Recipe(Parcel incoming)
    {
        id = incoming.readLong();
        recipeName = incoming.readString();
        recipeRating = incoming.readFloat();
        recipeText = incoming.readString();
        ingredients = incoming.createStringArrayList();
        videoURL = incoming.readString();
    }

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

    public String getRecipeText()
    {
        return recipeText;
    }
    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }
    public Recipe _setRecipeText(String procedureText) {
        this.recipeText = procedureText;
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

    public String getVideoURL() {
        return videoURL;
    }
    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outcoming, int flags)
    {
        outcoming.writeLong(id);
        outcoming.writeString(recipeName);
        outcoming.writeFloat(recipeRating);
        outcoming.writeString(recipeText);
        outcoming.writeStringList(ingredients);
        outcoming.writeString(videoURL);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>()
    {
        @Override
        public Recipe[] newArray(int size)
        {
            return new Recipe[size];
        }

        @Override
        public Recipe createFromParcel(Parcel incoming)
        {
            return new Recipe(incoming);
        }
    };
}

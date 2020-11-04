package com.martin.reciper;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class dbConverters
{
    //String to ArrayList
    @TypeConverter
    public static ArrayList<String> fromString(String value)
    {
        //Log.i("daco", "Som v konvertori String to ArrayList");
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    //ArrayList to String
    @TypeConverter
    public static String fromArrayList(ArrayList<String> list)
    {
        Log.i("daco", "Som v konvertori ArrayList to String");
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
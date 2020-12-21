package com.martin.reciper.models;

import android.util.Pair;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class Units
{
    public static class Unit
    {
        private final String name;
        private final float ratio;
        private final int id;

        public Unit(String name, float ratio, int id)
        {
            this.name = name;
            this.ratio = ratio;
            this.id = id;
        }

        public String getName() {return name;}
        public float getRatio() {return ratio;}
        public int getId() {return id;}
    }

    //attributes of Units
    public static Unit grams = new Unit("grams", 1.0f, 1);
    public static Unit decagrams = new Unit("decagrams", 10.0f, 2);
    public static Unit cups = new Unit("cups", 200.0f, 3);
    public static Unit ounces = new Unit("ounces", 28.0f, 4);

    public static Unit mililiters = new Unit("mililiters", 1.0f, 5);
    public static Unit pints = new Unit("pints", 568.0f, 6);
    public static Unit tablespoons = new Unit("table spoons", 15.0f, 7);
    public static Unit teaspoons = new Unit("tea spoons", 5.0f, 8);

    private static final int idDevider = 4;
    private static final List<Unit> units = new ArrayList<>(Arrays.asList(grams, decagrams, cups, ounces, mililiters, pints, tablespoons, teaspoons));

    //methods of Units
    public static List<String> toStringArray()
    {
        List<String> list = new ArrayList<>();
        for(Unit u:units)
            list.add(u.getName());
        return list;
    }

    public static List<Unit> getWeightUnits()
    {
        List<Unit> list = new ArrayList<>();
        for(Unit u:units)
        {
            if(u.getId() <= idDevider)
                list.add(u);
        }
        return list;
    }

    public static List<Unit> getVolumeUnits()
    {
        List<Unit> list = new ArrayList<>();
        for(Unit u:units)
        {
            if(u.getId() > idDevider)
                list.add(u);
        }
        return list;
    }

    public static int getId(Unit u)
    {
        return units.get(units.indexOf(u)).getId();
    }

    public static Unit getById(int i)
    {
        for(Unit u:units)
            if(u.getId() == i) return u;
        return null; //units.get(i);
    }
}
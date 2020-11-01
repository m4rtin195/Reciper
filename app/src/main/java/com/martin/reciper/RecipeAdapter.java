package com.martin.reciper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends ArrayAdapter<Recipe>
{
    private ArrayList<Recipe> array;

    public RecipeAdapter(Context context, int viewResourceId, ArrayList<Recipe> array)
    {
        super(context, -1, array);
        this.array = array;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view == null)
        {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.row_recipe, null);
        }

        Recipe object = array.get(position);
        if(object != null)
        {
            TextView text_recipeName = view.findViewById(R.id.text_recipeName);
            TextView text_recipeRating = view.findViewById(R.id.text_recipeRating);
            if(text_recipeName !=null) text_recipeName.setText(object.getRecipeName());
            if(text_recipeRating != null) text_recipeRating.setText(Float.toString(object.getRecipeRating()));
        }
        return view;
    }
}

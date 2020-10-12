package com.martin.reciper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends ArrayAdapter<RecipeRcrd>
{
    private ArrayList<RecipeRcrd> records;

    public RecipeAdapter(Context context, int textViewResourceId, ArrayList<RecipeRcrd> items)
    {
        super(context, textViewResourceId, items);
        this.records = records;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.recipe_row, null);
        }
        RecipeRcrd o = records.get(position);
        if (o != null)
        {
            TextView nazov = (TextView) v.findViewById(R.id.text_nazovReceptu);
            TextView hodnotenie = (TextView) v.findViewById(R.id.text_Hodnotenie);
            if (nazov != null) {
                nazov.setText("Name: "+o.getRecipeName());                            }
            if(hodnotenie != null){
                hodnotenie.setText("Status: "+ o.getRecipeRating());
            }
        }
        return v;
    }
}

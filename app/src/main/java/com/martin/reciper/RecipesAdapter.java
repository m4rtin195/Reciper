package com.martin.reciper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends ArrayAdapter<Recipe> implements Filterable
{
    private ArrayList<Recipe> array;
    private ArrayList<Recipe> originalArray;

    public RecipesAdapter(Context context, ArrayList<Recipe> array)
    {
        super(context, -1, array);
        this.array = array;
        this.originalArray = array;
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

    @Override
    public int getCount()
    {
        return array.size();
    }

    @Override
    public Recipe getItem (int i)
    {
        return array.get(i);
    }

    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence input)
            {
                String constr = input.toString().toLowerCase();
                array = originalArray;

                ArrayList<Recipe> filteredArray = new ArrayList<>();
                for(int i=0; i<array.size(); i++)
                {
                    Recipe rcpt = array.get(i);
                    if(rcpt.getRecipeName().toLowerCase().startsWith(constr))
                        filteredArray.add(rcpt);
                }

                FilterResults results = new FilterResults();
                results.count = filteredArray.size();
                results.values = filteredArray;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if(results.count == 0)
                    notifyDataSetInvalidated();
                else
                {
                    array = (ArrayList<Recipe>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }
}

package com.martin.reciper.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.martin.reciper.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> array;

    public IngredientAdapter(Context context, ArrayList<String> array)
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
            view = vi.inflate(R.layout.row_ingredient, null);
        }

        String object = array.get(position);
        if(object != null)
        {
            TextView text_ingredient = view.findViewById(R.id.text_ingredient);
            if(text_ingredient != null) text_ingredient.setText("\u2022  " + object);

        }
        return view;
    }
}

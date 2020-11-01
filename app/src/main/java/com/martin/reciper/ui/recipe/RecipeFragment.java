package com.martin.reciper.ui.recipe;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.martin.reciper.R;

public class RecipeFragment extends Fragment
{
    private RecipeViewModel recipeViewModel;

    public static RecipeFragment newInstance() {return new RecipeFragment();}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        final ImageButton editBtn = (ImageButton) view.findViewById(R.id.button_edit);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                edit();
            }
        });

        return view;
    }

    public void edit()
    {
        Log.i("abcde","im here2");
        TextView nazov = (TextView) getView().findViewById(R.id.edit_recipeName);
        TextView postup = (TextView) getView().findViewById(R.id.edit_recipeText);
        ImageButton editBtn = (ImageButton) getView().findViewById(R.id.button_edit);

        nazov.setFocusable(true);
        nazov.setFocusableInTouchMode(true);
        nazov.setClickable(true);

        postup.setFocusable(true);
        postup.setFocusableInTouchMode(true);
        postup.setClickable(true);

        //editBtn.setImageIcon(R.drawable.abc_vector_test); //@android:drawable/ic_menu_edit
        //editBtn.setBackgroundResource(R.drawable.abc_vector_test);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        // TODO: Use the ViewModel
    }
}
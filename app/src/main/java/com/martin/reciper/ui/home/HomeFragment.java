package com.martin.reciper.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.martin.reciper.AppActivity;
import com.martin.reciper.AppDatabase;
import com.martin.reciper.R;
import com.martin.reciper.Recipe;
import com.martin.reciper.RecipeAdapter;
import com.martin.reciper.ui.recipe.RecipeFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = view.findViewById(R.id.text_home); //zostatok zo sablony
        final Button searchBtn = view.findViewById(R.id.button_search);
        final EditText queryEdt = view.findViewById(R.id.edit_query);

        final Button deleteBtn = view.findViewById(R.id.button2); //zmazat
        deleteBtn.setVisibility(View.GONE);
        final View textfield = view.findViewById(R.id.textView31);

        textfield.setOnClickListener(view1 ->
        {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, new RecipeFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //getActivity().findViewById(R.id.textView31).setVisibility(View.GONE);
                //container.removeView(textfield);
                ((ViewGroup)container.getParent()).removeView(textfield);
            }
        });

        ListView list_recipes = view.findViewById(R.id.list_recipes);
        ArrayList<Recipe> recepty = new ArrayList<>();
        recepty.add(new Recipe()._setRecipeName("Kolac")._setRecipeRating(1.2f)
                ._setIngredients(new ArrayList<>(Arrays.asList("bravcove maso", "skorica", "mrkva"))));
        recepty.add(new Recipe()._setRecipeName("Omeleta")._setRecipeRating(1.2f));
        Log.i("daco", recepty.get(0).getRecipeName()+recepty.get(0).getRecipeRating());

        AppDatabase db = AppActivity.getDatabase();
        db.recipeDAO().insert(recepty.get(0));


        RecipeAdapter adapter = new RecipeAdapter(getActivity(), R.layout.row_recipe, recepty);
        list_recipes.setAdapter(adapter);

/*
        adapter.notifyDataSetChanged();
*/



        ///OK
        searchBtn.setOnClickListener(view3 ->
        {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", queryEdt.getText().toString());
            startActivity(intent);
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return view;
    }
}
package com.martin.reciper.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.martin.reciper.AppActivity;
import com.martin.reciper.AppDatabase;
import com.martin.reciper.R;
import com.martin.reciper.Recipe;
import com.martin.reciper.RecipesAdapter;
import com.martin.reciper.ui.recipe.RecipeFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    NavController navController;
    RecipesAdapter adapter;

    EditText edit_query;
    SearchView search_filter;
    Button button_search;
    ListView list_recipes;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        navController = NavHostFragment.findNavController(this);

        edit_query = view.findViewById(R.id.edit_query);
        button_search = view.findViewById(R.id.button_search);
        search_filter = view.findViewById(R.id.edit_filter);
        list_recipes = view.findViewById(R.id.list_recipes);


        adapter = new RecipesAdapter(getActivity(), (ArrayList) db.DAO().getAllRecipes());
        list_recipes.setAdapter(adapter);

        adapter.add(new Recipe()._setRecipeName("Polievka")._setRecipeRating(1.2f)
                ._setIngredients(new ArrayList<>(Arrays.asList("bravcove maso", "skorica", "mrkva"))));
        adapter.notifyDataSetChanged();

        button_search.setOnClickListener(view3 ->
        {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", edit_query.getText().toString());
            startActivity(intent);
        });

        list_recipes.setOnItemClickListener((adapterView, view1, i, id) ->
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("recipe", adapter.getItem(i));
            navController.navigate(R.id.action_navigation_home_to_fragment_recipe, bundle);
        });

        search_filter.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        return view;
    }
}
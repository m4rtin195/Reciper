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
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.martin.reciper.AppActivity;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.R;
import com.martin.reciper.Recipe;
import com.martin.reciper.adapters.RecipesAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    LayoutInflater inflater;
    NavController navController;
    RecipesAdapter recipesAdapter;

    EditText edit_query;
    SearchView search_filter;
    Button button_search;
    ListView list_recipes;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.inflater = inflater;
        navController = NavHostFragment.findNavController(this);

        edit_query = view.findViewById(R.id.edit_query);
        button_search = view.findViewById(R.id.button_search);
        search_filter = view.findViewById(R.id.edit_filter);
        list_recipes = view.findViewById(R.id.list_recipes);


        recipesAdapter = new RecipesAdapter(getActivity(), (ArrayList<Recipe>) db.DAO().getAllRecipes());
        list_recipes.setAdapter(recipesAdapter);

        View footerView =  inflater.inflate(R.layout.row_footer, list_recipes, false);
        list_recipes.addFooterView(footerView);

        button_search.setOnClickListener(view3 ->
        {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", edit_query.getText().toString());
            startActivity(intent);
        });

        list_recipes.setOnItemClickListener((adapterView, view1, i, id) ->
        {
            if(id>=0)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("recipe", recipesAdapter.getItem(i));
                navController.navigate(R.id.action_navigation_home_to_fragment_recipe, bundle);
            }
            else //footer
            {
                onNewRecipe();
            }
        });
        list_recipes.setOnItemLongClickListener((adapterView, view12, i, l) ->
        {
            Log.i("daco", "long lsitener");
            if(l == -1) return false; //footer

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Recipe?");
            builder.setPositiveButton("Yes", (dialog, which) ->
            {
                Recipe rcpt = recipesAdapter.getItem(i);
                recipesAdapter.remove(rcpt);
                recipesAdapter.notifyDataSetChanged();
                db.DAO().delete(rcpt);
            });
            builder.setNegativeButton("No",null);
            builder.show();
            return true;
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
                recipesAdapter.getFilter().filter(query);
                return false;
            }
        });

        return view;
    }

    public void onNewRecipe()
    {
        onNewRecipe(new String(), new String());
    }

    public void onNewRecipe(String name, String videoURL)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add new Recipe");
        final EditText input = new EditText(getContext());
        if(!name.isEmpty())
            input.setText(name);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) ->
        {
            Recipe rcpt = new Recipe();
            //TODO check that is not empty
            rcpt.setRecipeName(input.getText().toString());
            rcpt.setVideoURL(videoURL);
            recipesAdapter.add(rcpt);
            recipesAdapter.notifyDataSetChanged();
            db.DAO().insert(rcpt);
            Bundle bundle = new Bundle();
            bundle.putParcelable("recipe", rcpt);
            bundle.putBoolean("isNew", true);
            navController.navigate(R.id.action_navigation_home_to_fragment_recipe, bundle);
        });
        builder.show();
    }
}
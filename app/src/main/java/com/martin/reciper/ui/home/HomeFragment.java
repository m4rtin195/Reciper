package com.martin.reciper.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martin.reciper.AppActivity;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.R;
import com.martin.reciper.models.Recipe;
import com.martin.reciper.adapters.RecipesAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    SharedPreferences settings;

    NavController navController;
    RecipesAdapter recipesAdapter;

    EditText edit_query; //todo on enter listener
    SearchView search_filter;
    Button button_search;
    ListView list_recipes;
    FloatingActionButton fab_addNew;
    Group group_youtube;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        navController = NavHostFragment.findNavController(this);

        edit_query = view.findViewById(R.id.edit_query);
        button_search = view.findViewById(R.id.button_search);
        search_filter = view.findViewById(R.id.edit_filter);
        list_recipes = view.findViewById(R.id.list_recipes);
        fab_addNew = view.findViewById(R.id.fab_addNew);
        group_youtube = view.findViewById(R.id.group_youtube);

        recipesAdapter = new RecipesAdapter(getActivity(), (ArrayList<Recipe>) db.DAO().getAllRecipes());
        list_recipes.setAdapter(recipesAdapter);

        //View footerView =  inflater.inflate(R.layout.row_footer, list_recipes, false);
        //list_recipes.addFooterView(footerView);


        group_youtube.setVisibility(settings.getBoolean("youtube_enabled", true) ? View.VISIBLE : View.GONE);

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

        fab_addNew.setOnClickListener(view13 ->
        {
            onNewRecipe();
        });

        return view;
    } //onCreateView


    public void onNewRecipe()
    {
        onNewRecipe(new String(), new String());
    }

    public void onNewRecipe(String name, String videoURL)
    {
        View content = getLayoutInflater().inflate(R.layout.dialog_recipe,null);
        EditText input = content.findViewById(R.id.editbox);
        if(!name.isEmpty())
            input.setText(name);

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle("Add new Recipe");
        dialog.setView(content);
        dialog.setPositiveButton("Add", (dialog2, which) ->
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
        dialog.show();
    }
}
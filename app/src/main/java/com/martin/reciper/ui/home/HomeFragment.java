package com.martin.reciper.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martin.reciper.AppActivity;
import com.martin.reciper.R;
import com.martin.reciper.adapters.RecipesAdapter;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.models.Recipe;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    SharedPreferences settings;

    Toolbar toolbar;
    NavController navController;
    RecipesAdapter recipesAdapter;

    EditText edit_ytQuery;
    SearchView search_filter;
    Button button_search;
    ListView list_recipes;
    FloatingActionButton fab_addNew;
    Group group_youtube;

    boolean youtubeVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        settings = PreferenceManager.getDefaultSharedPreferences(requireContext());

        youtubeVisible = settings.getBoolean("youtube_enabled", true);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar = requireActivity().findViewById(R.id.toolbar);
        navController = NavHostFragment.findNavController(this);

        group_youtube = view.findViewById(R.id.group_youtube);
            group_youtube.setVisibility(youtubeVisible ? View.VISIBLE : View.GONE);
        edit_ytQuery = view.findViewById(R.id.edit_query);
            edit_ytQuery.setOnEditorActionListener(onYtQueryEditorActionListener);
        button_search = view.findViewById(R.id.button_search);
            button_search.setOnClickListener(view1 -> onYoutubeSearch());

        list_recipes = view.findViewById(R.id.list_recipes);
            recipesAdapter = new RecipesAdapter(getActivity(), (ArrayList<Recipe>) db.DAO().getAllRecipes());
            list_recipes.setAdapter(recipesAdapter);

        fab_addNew = view.findViewById(R.id.fab_addNew);
            fab_addNew.setOnClickListener(view1 -> onNewRecipe());

        //search_filter = view.findViewById(R.id.search_filter);

        //View footerView =  inflater.inflate(R.layout.row_footer, list_recipes, false); list_recipes.addFooterView(footerView);

        return view;
    } //onCreateView

    Button abc;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_home, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_home_search);
        search_filter = (SearchView) searchItem.getActionView();
        search_filter.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override public boolean onQueryTextSubmit(String s) {return false;}
            @Override public boolean onQueryTextChange(String query)
            {
                recipesAdapter.getFilter().filter(query);
                return true;
            }
        });

        list_recipes.setOnItemClickListener(onListRecipesItemClickListener);
        list_recipes.setOnItemLongClickListener(onListRecipesItemLongClickListener);

        abc = new Button(getContext());
        abc.setText(getString(R.string.add));
        abc.setTag("mytag");
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.END;
        abc.setLayoutParams(params);
        //toolbar.addView(abc);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.menu_home_youtube)
        {
            youtubeVisible = !youtubeVisible;
            group_youtube.setVisibility(youtubeVisible ? View.VISIBLE : View.GONE);
        }
        if(item.getItemId() == R.id.menu_home_converter)
            navController.navigate(R.id.action_navigation_home_to_converter);
        if(item.getItemId() == R.id.menu_home_settings)
            navController.navigate(R.id.action_navigation_home_to_settings);

        return true;
    }

    @Override
    public void onDestroyView()
    {
        //toolbar.removeView(abc);
        super.onDestroyView();
    }


    private void onYoutubeSearch()
    {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", edit_ytQuery.getText().toString());
        startActivity(intent);
    }

    public void onNewRecipe()
    {
        onNewRecipe(new String(), null);
    }

    public void onNewRecipe(String name, Uri mediaURI)
    {
        View content = getLayoutInflater().inflate(R.layout.dialog_recipe,null);
        EditText input = content.findViewById(R.id.editbox);
        if(!name.isEmpty())
            input.setText(name);

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(getString(R.string.recipe_create));
        dialog.setView(content);
        dialog.setPositiveButton(getString(R.string.create), (dialog2, which) ->
        {
            Recipe rcpt = new Recipe();
            //TODO check that is not empty
            rcpt.setRecipeName(input.getText().toString());
            if(mediaURI != null)
                rcpt.setMediaURI(mediaURI.toString());

            recipesAdapter.add(rcpt);
            recipesAdapter.notifyDataSetChanged();
            long id = db.DAO().insert(rcpt);
            rcpt.setId(id);

            Bundle bundle = new Bundle();
            bundle.putParcelable("recipe", rcpt);
            bundle.putBoolean("isNew", true);
            navController.navigate(R.id.action_navigation_home_to_recipe, bundle);
        });
        dialog.show();
    }


    EditText.OnEditorActionListener onYtQueryEditorActionListener = (textView, action, keyEvent) ->
    {
        if(action == EditorInfo.IME_ACTION_GO)
            if(!edit_ytQuery.getText().toString().isEmpty())
            {onYoutubeSearch(); return true;}

        InputMethodManager inputManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        return false;
    };

    AdapterView.OnItemClickListener onListRecipesItemClickListener = (adapterView, view, i, id) ->
    {
        if(id >= 0) //not footer
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("recipe", recipesAdapter.getItem(i));
            navController.navigate(R.id.action_navigation_home_to_recipe, bundle);
        }
    };

    AdapterView.OnItemLongClickListener onListRecipesItemLongClickListener = (adapterView, view, i, l) ->
    {
        if(l == -1) return false; //footer

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
    };
}
package com.martin.reciper.ui.recipe;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.martin.reciper.AppActivity;
import com.martin.reciper.AppDatabase;
import com.martin.reciper.IngredientAdapter;
import com.martin.reciper.R;
import com.martin.reciper.Recipe;

public class RecipeFragment extends Fragment
{
    private RecipeViewModel recipeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    Recipe rcpt;
    private boolean editMode = false;
    IngredientAdapter ingredients;

    ImageButton button_edit;
    TextView text_recipeName, text_recipeText;
    EditText edit_recipeName, edit_recipeText;
    RatingBar rating_recipeRating;
    ListView list_ingredients;
    ScrollView scrollView_text_recipeText, scrollView_edit_recipeText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        rcpt = getArguments().getParcelable("recipe");
        if(rcpt == null) rcpt = new Recipe();

        button_edit = view.findViewById(R.id.button_edit);
        text_recipeName = view.findViewById(R.id.text_recipeName);
            text_recipeName.setText(rcpt.getRecipeName());
        edit_recipeName = view.findViewById(R.id.edit_recipeName);
            edit_recipeName.setText(rcpt.getRecipeName());
        text_recipeText = view.findViewById(R.id.text_recipeText);
            text_recipeText.setText(rcpt.getRecipeText());
        edit_recipeText = view.findViewById(R.id.edit_recipeText);
            edit_recipeText.setText(rcpt.getRecipeText());
        rating_recipeRating = view.findViewById(R.id.rating_recipeRating);
            rating_recipeRating.setRating(rcpt.getRecipeRating());
        list_ingredients = view.findViewById(R.id.list_ingredients);
            list_ingredients.setAdapter(ingredients = new IngredientAdapter(getActivity(), rcpt.getIngredients()));
        scrollView_text_recipeText = view.findViewById(R.id.scrollView_text_recipeText);
        scrollView_edit_recipeText = view.findViewById(R.id.scrollView_edit_recipeText);

        button_edit.setOnClickListener(view1 -> {onEditMode();});

        View footerView =  inflater.inflate(R.layout.row_footer, null, false);
        list_ingredients.addFooterView(footerView);
        list_ingredients.removeFooterView(footerView);
        list_ingredients.setOnItemClickListener((adapterView, view12, i, l) ->
        {
            if(editMode)
            {
                if(l == -1) //footer
                {
                    //ingredients.add()
                }
                else
                {
                    String text = "";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Title");
                    final EditText input = new EditText(getContext());
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String aa = input.getText().toString();
                        }
                    });
                    builder.show();
                }
            }
        });

        return view;
    }

    public void onEditMode()
    {
        editMode = !editMode;
        if(editMode)
        {
            button_edit.setImageResource(R.drawable.ic_save);

            text_recipeName.setVisibility(View.INVISIBLE);
            edit_recipeName.setVisibility(View.VISIBLE);
            rating_recipeRating.setIsIndicator(false);
            scrollView_text_recipeText.setVisibility(View.GONE);
            scrollView_edit_recipeText.setVisibility(View.VISIBLE);
        }
        else //dokonceny edit
        {
            button_edit.setImageResource(R.drawable.ic_edit);

            text_recipeName.setVisibility(View.VISIBLE);
            edit_recipeName.setVisibility(View.INVISIBLE);
            rating_recipeRating.setIsIndicator(true);
            scrollView_text_recipeText.setVisibility(View.VISIBLE);
            scrollView_edit_recipeText.setVisibility(View.GONE);

            rcpt.setRecipeName(edit_recipeName.getText().toString());
            rcpt.setRecipeRating(rating_recipeRating.getRating());
            rcpt.setRecipeText(edit_recipeText.getText().toString());

            text_recipeName.setText(edit_recipeName.getText().toString());
            text_recipeText.setText(edit_recipeText.getText().toString());
            db.DAO().updateRecipe(rcpt);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        // TODO: Use the ViewModel
    }
}
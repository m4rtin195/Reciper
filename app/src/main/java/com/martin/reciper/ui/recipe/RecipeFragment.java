package com.martin.reciper.ui.recipe;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.martin.reciper.AppActivity;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.adapters.IngredientAdapter;
import com.martin.reciper.R;
import com.martin.reciper.Recipe;

public class RecipeFragment extends Fragment
{
    RecipeViewModel recipeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    LayoutInflater inflater;
    Recipe rcpt;
    IngredientAdapter ingredients;

    ImageButton button_edit, button_share;
    TextView text_recipeName, text_recipeText;
    EditText edit_recipeName, edit_recipeText;
    RatingBar rating_recipeRating;
    ListView list_ingredients;
    ScrollView scrollView_text_recipeText, scrollView_edit_recipeText;
    WebView web_video;

    View footerView;

    boolean editMode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        this.inflater = inflater;
        footerView =  inflater.inflate(R.layout.row_footer, null, false);

        rcpt = getArguments().getParcelable("recipe");
        if(rcpt == null) rcpt = new Recipe();

        button_edit = view.findViewById(R.id.button_edit);
        button_share = view.findViewById(R.id.button_share);
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
        web_video = view.findViewById(R.id.web_video);
            //web_video.loadData("<iframe width=\\\"280\\\" height=\\\"158\\\" src=\\\"https://www.youtube.com/embed/g6BFZ3e69ms\\\" frameborder=\\\"0\\\" allowfullscreen></iframe>", "text/html", "utf-8");
        list_ingredients = view.findViewById(R.id.list_ingredients);
            list_ingredients.setAdapter(ingredients = new IngredientAdapter(getActivity(), rcpt.getIngredients()));
        scrollView_text_recipeText = view.findViewById(R.id.scrollView_text_recipeText);
        scrollView_edit_recipeText = view.findViewById(R.id.scrollView_edit_recipeText);

        web_video.getSettings().setJavaScriptEnabled(true);
        web_video.setWebChromeClient(new WebChromeClient());
        web_video.loadData("<iframe width=\"311\" height=\"166\" src=\"https://www.youtube.com/embed/g6BFZ3e69ms\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", "utf-8");
        web_video.setForeground(null);

        button_edit.setOnClickListener(view1 -> {onEditMode();});
        button_share.setOnClickListener(view1 ->{onRecipeShare();});

        list_ingredients.setOnItemClickListener((adapterView, view12, i, l) ->
        {
            if(editMode)
            {
                if(l == -1) //footer
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Add ingredient");
                    final EditText input = new EditText(getContext());
                    builder.setView(input);
                    builder.setPositiveButton("Add", (dialog, which) -> ingredients.add(input.getText().toString()));
                    builder.show();
                }
                else //surovina
                {
                    String word = ingredients.getItem(i);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Edit ingredient");
                    final EditText input = new EditText(getContext());
                    input.setText(word);
                    builder.setView(input);
                    builder.setPositiveButton("Save", (dialog, which) -> {ingredients.remove(word); ingredients.insert(input.getText().toString(),i);});
                    builder.setNegativeButton("Delete", (dialog, which) -> ingredients.remove(word));
                    builder.show();
                }
            }
        });

        if(getArguments().getBoolean("isNew",false)) onEditMode();
        return view;
    }

    private void onEditMode()
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

            list_ingredients.setEnabled(true);
            list_ingredients.addFooterView(footerView);
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

            list_ingredients.setEnabled(false);
            list_ingredients.removeFooterView(footerView);

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

    private void onRecipeShare()
    {
        Log.i("daco", "som v share");
        String content = "";
        String newline = "\n";

        // name
        content += rcpt.getRecipeName() + newline;
        // stars
        for(int i=0; i<rcpt.getRecipeRating(); i++)
            content += '★';
        if(rcpt.getRecipeRating()%1 != 0)   //TODO check this
            content += '/';
        for(int i=5; i>Math.ceil(rcpt.getRecipeRating()); i--)
            content += '☆';
        content += newline + newline;
        // ingredients
        content += "✓ " + getResources().getString(R.string.ingredients) + newline;
        for(String ingr : rcpt.getIngredients())
            content +=  "• " + ingr + newline;
        content += newline;
        // procedure
        content += "✓ " + getResources().getString(R.string.procedure) + newline;
        content += rcpt.getRecipeText() + newline + newline;
        // video
        if(!rcpt.getVideoURL().isEmpty())
            content += "✓ " + getResources().getString(R.string.video) + newline + rcpt.getVideoURL() + newline;
        // footer
        content += newline + "(Shared from Reciper app \u2764)";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
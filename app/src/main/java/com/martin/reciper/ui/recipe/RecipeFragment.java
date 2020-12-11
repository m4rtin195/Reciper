package com.martin.reciper.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martin.reciper.AppActivity;
import com.martin.reciper.MainActivity;
import com.martin.reciper.R;
import com.martin.reciper.adapters.IngredientAdapter;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.models.Recipe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecipeFragment extends Fragment
{
    RecipeViewModel recipeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    Menu menu;
    Recipe rcpt;
    IngredientAdapter ingredients;

    ImageButton button_edit, button_share;
    TextView text_recipeName, text_recipeText;
    EditText edit_recipeName, edit_recipeText;
    RatingBar rating_recipeRating;
    ListView list_ingredients;
    ScrollView scrollView_text_recipeText, scrollView_edit_recipeText;
    WebView web_video;

    View footerView_ingredients;

    boolean editMode = false;

    ImageView image_photo; //todo defence delete this

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        footerView_ingredients =  inflater.inflate(R.layout.row_footer, null, false);

        assert getArguments() != null;
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
        int n = rcpt.getVideoURL().lastIndexOf('/');
        String videoID = rcpt.getVideoURL().substring(n+1);
        String iframe = "<iframe width=\"311\" height=\"166\" src=\"https://www.youtube.com/embed/" + videoID + "\" frameborder=\"0\" allowfullscreen></iframe>";
        web_video.loadData(iframe, "text/html", "utf-8");
        web_video.setForeground(null);

        button_edit.setOnClickListener(view1 -> {onEditMode();});
        button_share.setOnClickListener(view1 ->{onRecipeShare();});

        list_ingredients.setOnItemClickListener((adapterView, view12, i, l) ->
        {
            if(editMode)
            {
                if(l == -1) //footer
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle(getString(R.string.add_ingredient));

                    EditText input = new EditText(getContext());
                    builder.setView(input);
                    builder.setPositiveButton(getString(R.string.add), (dialog, which) -> ingredients.add(input.getText().toString()));
                    builder.show();
                }
                else //surovina
                {
                    String word = ingredients.getItem(i);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                    builder.setTitle(getString(R.string.edit_ingredient));
                    EditText input = new EditText(getContext());
                    input.setText(word);
                    builder.setView(input);
                    builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {ingredients.remove(word); ingredients.insert(input.getText().toString(),i);});
                    builder.setNegativeButton(getString(R.string.delete), (dialog, which) -> ingredients.remove(word));
                    builder.show();
                }
            }
        });

        assert getArguments() != null;
        if(getArguments().getBoolean("isNew",false)) onEditMode();


        // defense 3
        image_photo = view.findViewById(R.id.image_photo);

        takePicture();
        return view;
    } //onCreateView

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_recipe, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.menu_recipe_converter)
            onConverterOpen();
        if(item.getItemId() == R.id.menu_recipe_edit)
            onEditMode();
        if(item.getItemId() == R.id.menu_recipe_share)
            onRecipeShare();

        return true;
    }

    private void onConverterOpen()
    {
        Toast.makeText(getContext(), "converterFragment", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle("Units Converter");

        dialog.setView(R.layout.fragment_converter);
        dialog.setPositiveButton("Close", (dialog2, which) ->
        {

        });
        dialog.show();

        DialogFragment frg = new DialogFragment();
        //frg.show()
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ((MainActivity)getActivity()).getExternalFilesDir((Environment.DIRECTORY_PICTURES));
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        String currentPhotoPath = image.getAbsolutePath();
        Log.i("daco", currentPhotoPath);
        return image;
    }

    private void onEditMode()
    {
        editMode = !editMode;
        if(editMode)
        {
            button_edit.setImageResource(R.drawable.ic_save);
            menu.findItem(R.id.menu_recipe_edit).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_save));
            menu.findItem(R.id.menu_recipe_edit).setIconTintList(null);

            text_recipeName.setVisibility(View.INVISIBLE);
            edit_recipeName.setVisibility(View.VISIBLE);
            rating_recipeRating.setIsIndicator(false);
            scrollView_text_recipeText.setVisibility(View.GONE);
            scrollView_edit_recipeText.setVisibility(View.VISIBLE);

            list_ingredients.setEnabled(true);
            list_ingredients.addFooterView(footerView_ingredients);
        }
        else //dokonceny edit
        {
            button_edit.setImageResource(R.drawable.ic_edit);
            menu.findItem(R.id.menu_recipe_edit).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_edit));
            menu.findItem(R.id.menu_recipe_edit).setIconTintList(null);

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
            list_ingredients.removeFooterView(footerView_ingredients);

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

//    @Override
//    public void onActivityResult(int rc, int resultCode, Intent data)
//    {
//        if(rc == 1 && resultCode == RESULT_OK)
//        {
//            Bundle extras = data.getExtras();
//            Bitmap image = (Bitmap) extras.get("data");
//            image_photo.setImageBitmap(image);
//
//        }
//    }

    void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            if(photoFile != null)
            {
//                Uri photoURI = FileProvider.getUriForFile(requireContext(), "com.martin.reciper.fileprovider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
}
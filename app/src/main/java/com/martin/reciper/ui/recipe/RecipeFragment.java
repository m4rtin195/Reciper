package com.martin.reciper.ui.recipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martin.reciper.AppActivity;
import com.martin.reciper.R;
import com.martin.reciper.adapters.IngredientAdapter;
import com.martin.reciper.database.AppDatabase;
import com.martin.reciper.models.Recipe;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class RecipeFragment extends Fragment
{
    RecipeViewModel recipeViewModel;
    AppDatabase db = AppActivity.getDatabase();
    Menu menu;
    Recipe rcpt;
    IngredientAdapter ingredients;

    TextView text_recipeName, text_recipeText;
    EditText edit_recipeName, edit_recipeText;
    RatingBar rating_recipeRating;
    ListView list_ingredients;
    View footerView_ingredients;

    ConstraintLayout layout_media;
    ImageView image_media;
    VideoView video_media;
    WebView web_video;
    FloatingActionButton fab_converter;

    MenuItem menu_editIcon;
    MenuItem menu_mediaIcon;

    boolean editMode = false;
    Uri tempURI;

    static final int CAPTURE_PHOTO = 1;
    static final int CAPTURE_VIDEO = 2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        if(getArguments() != null)
            rcpt = getArguments().getParcelable("recipe");
        if(rcpt == null)
            rcpt = new Recipe();
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        text_recipeName = view.findViewById(R.id.text_recipeName);
            text_recipeName.setText(rcpt.getRecipeName());
        edit_recipeName = view.findViewById(R.id.edit_recipeName);
            edit_recipeName.setText(rcpt.getRecipeName());
        rating_recipeRating = view.findViewById(R.id.rating_recipeRating);
            rating_recipeRating.setRating(rcpt.getRecipeRating());

        layout_media = view.findViewById(R.id.layout_media);
        image_media = view.findViewById(R.id.image_media);
        video_media = view.findViewById(R.id.video_media);
        web_video = view.findViewById(R.id.web_video);

        list_ingredients = view.findViewById(R.id.list_ingredients);
            list_ingredients.setAdapter(ingredients = new IngredientAdapter(getActivity(), rcpt.getIngredients()));
            list_ingredients.setOnItemClickListener(onListIngredietnsItemClickListener);
            list_ingredients.invalidate(); //todo prekresli layout
            footerView_ingredients =  inflater.inflate(R.layout.row_footer, null, false);
        text_recipeText = view.findViewById(R.id.text_recipeText);
            text_recipeText.setText(rcpt.getRecipeText());
        edit_recipeText = view.findViewById(R.id.edit_recipeText);
            edit_recipeText.setText(rcpt.getRecipeText());

        fab_converter = view.findViewById(R.id.fab_openConverter);
            fab_converter.setOnClickListener(view1 -> onConverterOpen());

        loadMedia();

        return view;
    } //onCreateView

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_recipe, menu);
        this.menu = menu;

        menu_mediaIcon = menu.findItem(R.id.menu_recipe_media);
            menu_mediaIcon.setEnabled(false);
        menu_editIcon = menu.findItem(R.id.menu_recipe_edit);
            menu_editIcon.setIconTintList(null);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu)
    {
        if(getArguments() != null)
            if(getArguments().getBoolean("isNew",false)) onEditMode();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.menu_recipe_media)
            onMediaChange();
        if(item.getItemId() == R.id.menu_recipe_converter)
            onConverterOpen();
        if(item.getItemId() == R.id.menu_recipe_edit)
            onEditMode();
        if(item.getItemId() == R.id.menu_recipe_share)
            onRecipeShare();

        return true;
    }

    private void loadMedia()
    {
        image_media.setVisibility(View.GONE);
        video_media.setVisibility(View.GONE);
        web_video.setVisibility(View.GONE);

        if(rcpt.getMediaURI().isEmpty()) return;

        Uri mediaURI = Uri.parse(rcpt.getMediaURI());
        switch(mediaURI.getScheme())
        {
            case "content":  //local image or video
            {
                if(mediaURI.getLastPathSegment().endsWith(".jpg"))  //local image
                {
                    image_media.setVisibility(View.VISIBLE);
                    image_media.setImageURI(mediaURI);
                    break;
                }
                if(mediaURI.getLastPathSegment().endsWith(".mp4"))  //local video
                {
                    video_media.setVisibility(View.VISIBLE);
                    video_media.setVideoURI(mediaURI);
                    video_media.setMediaController(new MediaController(requireContext()));
                    video_media.setAudioFocusRequest(AudioManager.AUDIOFOCUS_GAIN);
                    video_media.seekTo(1);
                    video_media.requestFocus();
                    break;
                }
                Toast.makeText(getContext(), "Unknown local media", Toast.LENGTH_SHORT).show();
                break;
            }
            case "http":
            case "https":
            {
                if(mediaURI.getAuthority().contains("youtu.be") || mediaURI.getAuthority().contains("youtube.com"))
                {
                    web_video.setVisibility(View.VISIBLE);
                    web_video.getSettings().setJavaScriptEnabled(true);
                    web_video.setWebChromeClient(new WebChromeClient());
                    int n = rcpt.getMediaURI().lastIndexOf('/');
                    String videoID = rcpt.getMediaURI().substring(n+1);
                    String iframe = "<iframe width=\"311\" height=\"166\" src=\"https://www.youtube.com/embed/" + videoID + "\" frameborder=\"0\" allowfullscreen></iframe>";
                    web_video.loadData(iframe, "text/html", "utf-8");
                    web_video.setForeground(null);
                }
                else    //page, try to resolve image
                {
                    ;
                }
                break;
            }
            default:
                Toast.makeText(getContext(), "Unknown media", Toast.LENGTH_SHORT).show();
        }
    }

    void captureMedia(int action)
    {
        Intent intent;
        String suffix;

        switch(action)
        {
            case CAPTURE_PHOTO: {intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); suffix = ".jpg"; break;}
            case CAPTURE_VIDEO: {intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); suffix = ".mp4"; break;}
            default: return;
        }

        if(intent.resolveActivity(requireActivity().getPackageManager()) != null)
        {
            Uri mediaFileURI = createMediaFile(suffix);
            if(mediaFileURI != null)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileURI);
                startActivityForResult(intent, action);
            }
            else
                Toast.makeText(getContext(), "Failed to crate a file", Toast.LENGTH_LONG).show();
        }
    }

    private Uri createMediaFile(String suffix)
    {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "MEDIA_" + "_";
        File storageDir = requireActivity().getExternalFilesDir(null); //Environment.DIRECTORY_PICTURES;
        Uri fileURI = null;
        try
        {
            File file = File.createTempFile(fileName, suffix, storageDir);
            fileURI = FileProvider.getUriForFile(requireContext(), "com.martin.reciper.fileprovider", file);
            tempURI = fileURI;
        }
        catch(IOException e) {e.printStackTrace();}

        return fileURI;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == CAPTURE_PHOTO || requestCode == CAPTURE_VIDEO)
        {
            if(resultCode == RESULT_OK)
            {
                rcpt.setMediaURI(tempURI.toString());
                image_media.setImageURI(Uri.parse(tempURI.toString()));
            }
            else
                Toast.makeText(getContext(), "Failed to capture media, " + resultCode, Toast.LENGTH_SHORT).show();

            tempURI = null;
            loadMedia();
        }
    }

    private void onMediaChange()
    {
        String url = rcpt.getMediaURI();
        EditText input = new EditText(getContext());
        input.setText(url);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(getString(R.string.media_descriptor));
        builder.setView(input);
        builder.setNegativeButton(getString(R.string.photo), (dialog, which) -> captureMedia(CAPTURE_PHOTO));
        builder.setPositiveButton(getString(R.string.video), (dialog, which) -> captureMedia(CAPTURE_VIDEO));
        builder.setNeutralButton(getString(R.string.save), (dialog, which) -> rcpt.setMediaURI(input.getText().toString()));
        builder.show();
    }

    private void onConverterOpen()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(getString(R.string.converter));
        dialog.setView(R.layout.fragment_converter);
        dialog.setNegativeButton("Close", (dialog2, which) -> {});
        dialog.show();
        DialogFragment frg = new DialogFragment();
        //frg.show()
    }

    private void onEditMode()
    {
        editMode = !editMode;
        if(editMode)
        {
            menu_editIcon.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_save));
            menu_mediaIcon.setEnabled(true);

            text_recipeName.setVisibility(View.INVISIBLE);
            edit_recipeName.setVisibility(View.VISIBLE);
            rating_recipeRating.setIsIndicator(false);
            text_recipeText.setVisibility(View.GONE);
            edit_recipeText.setVisibility(View.VISIBLE);

            list_ingredients.setEnabled(true);
            list_ingredients.addFooterView(footerView_ingredients);
        }
        else //dokonceny edit
        {
            menu_editIcon.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit));
            menu_mediaIcon.setEnabled(false);

            text_recipeName.setVisibility(View.VISIBLE);
            edit_recipeName.setVisibility(View.INVISIBLE);
            rating_recipeRating.setIsIndicator(true);
            text_recipeText.setVisibility(View.VISIBLE);
            edit_recipeText.setVisibility(View.GONE);

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

    private void onRecipeShare()
    {
        StringBuilder content = new StringBuilder();
        String newline = "\n";

        // name
        content.append(rcpt.getRecipeName()).append(newline);
        // stars
        for(int i=0; i<Math.floor(rcpt.getRecipeRating()); i++)
            content.append('★');
        if(rcpt.getRecipeRating()%1 != 0)
            content.append('/');
        for(int i=5; i>Math.ceil(rcpt.getRecipeRating()); i--)
            content.append('☆');
        content.append(newline).append(newline);
        // ingredients
        content.append("✓ ").append(getResources().getString(R.string.ingredients)).append(newline);
        for(String ingr : rcpt.getIngredients())
            content.append("• ").append(ingr).append(newline);
        content.append(newline);
        // procedure
        content.append("✓ ").append(getResources().getString(R.string.procedure)).append(newline);
        content.append(rcpt.getRecipeText()).append(newline).append(newline);
        // video
        if(!rcpt.getMediaURI().isEmpty())
            content.append("✓ ").append(getResources().getString(R.string.link)).append(newline).append(rcpt.getMediaURI()).append(newline);
        // footer
        content.append(newline).append("(Shared from Reciper app \u2764)");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    AdapterView.OnItemClickListener onListIngredietnsItemClickListener = (adapterView, view, i, l) ->
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
    };
}
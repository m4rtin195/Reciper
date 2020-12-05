package com.martin.reciper;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.martin.reciper.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        //mySetLocale(new Locale("sk"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("daco", "--------------------------------------");
        LocaleList current = getResources().getConfiguration().getLocales();
        Log.i("daco", "current locale: " + current.toString());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navBar = findViewById(R.id.nav_bar);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_converter, R.id.navigation_settings).build();

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration); //ActionBar setup
        NavigationUI.setupWithNavController(navBar, navController); //NavBar setup

        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener); //keyboard listener
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Intent intent = getIntent();
        if(intent.getAction().equals(Intent.ACTION_SEND))
        {
            resolveIntent(intent);
            setIntent(new Intent());
        }

/*        Log.i("daco", "onStart()");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.fragment_home);
        HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if(hf == null) Log.e("daco", "HF null");
        else Log.i("daco", hf.toString());*/
    }


    @SuppressWarnings("deprecation") //progress dialog
    protected void resolveIntent(Intent intent)
    {
        AtomicReference<String> videoURL = new AtomicReference<>(new String());
        AtomicReference<String> videoTitle = new AtomicReference<>(new String());

        ClipData clipData = intent.getClipData();
        if(clipData.getItemCount() > 0)
        {
            if(clipData.getDescription().getMimeType(0).equals(ClipDescription.MIMETYPE_TEXT_PLAIN))
            {
                String content = intent.getClipData().getItemAt(0).getText().toString();
                if(content.contains("https://youtu.be/") || content.contains("https://www.youtube.com/watch"))
                {
                    videoURL.set(content);
                    Log.i("daco", "content resolved: " + videoURL);
                }
                else //not youtube url
                {
                    Toast.makeText(this, "Content doesn't contains YouTube video URL", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            else //not text-plain
            {
                Log.i("daco", "Wrong content");
                Toast.makeText(this, "Unknown intent mime-type", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else //empty clipdata
        {
            Log.i("daco", "Wrong content");
            Toast.makeText(this, "Empty intent clip content", Toast.LENGTH_LONG).show();
            return;
        }

        ///// have URL, get Title

        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching data. Please wait...");
        dialog.show();



        Thread thr = new Thread(() ->
        {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try
            {

                URL url = new URL("https://www.youtube.com/oembed?url=" + videoURL.get() + "&format=json");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    stringBuilder.append(line);

                JSONObject json = new JSONObject(stringBuilder.toString());

                videoTitle.set(json.getString("title"));
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                Log.w("daco", "nespravna URL");
            }
            catch (IOException | JSONException e)
            {
                e.printStackTrace();
            } finally
            {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thr.start();
        try {thr.join();} catch(InterruptedException e) {e.printStackTrace();}

        dialog.dismiss();
        Toast.makeText(this, videoTitle.get(), Toast.LENGTH_LONG).show();
        Log.i("daco", "vysledok mimo thready: " + videoTitle.get());


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        try
        {
            //TODO ziskat korektnou cestou
            // alebo premiestnit onNewRecipe do aktivity
            assert navHostFragment != null;
            HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            Log.i("daco", "mam instanciu");
            if(hf != null)
                hf.onNewRecipe(videoTitle.get(), videoURL.get());
        }
        catch(Exception e)
        {
            Log.e("daco", "WORNG FRAGMENT ASSIGMENT !!!");
            e.printStackTrace();
        }
    }


    public void skuska()
    {
        Log.i("daco", "skuska()");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.fragment_home);
        HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if(hf == null) Log.e("daco", "HF null");
        else Log.i("daco", hf.toString());
    }

    public void onContactDeveloper()
    {
        Log.i("daco", "--- contact developer ---");
        Toast.makeText(this, "Contacting developer", Toast.LENGTH_LONG).show();

        String body = null;
        try
        {
            body = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        }
        catch(PackageManager.NameNotFoundException e)
        {}

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO); //todo or just _send?
        emailIntent.setType("message/rfc822");
        //emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"martin.timko@centrum.sk", "martin.timko@ktu.edu"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from Reciper app");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_client)));
    }

    public void mySetLocale(Locale locale)
    {
        Log.i("daco", "menim locale: " + locale.toString());
        //Locale locale = new Locale(languageCode);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        createConfigurationContext(configuration);
        recreate();

//        finish();
//        startActivity(getIntent());
    }

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        boolean isOpened = false;

        @Override
        public void onGlobalLayout()
        {
            View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);

            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if(heightDiff > 300)  // difference between RootView a FragmentView, incluing all actionbar
            {
                if(!isOpened)   //just opened
                {
                    navBar.setVisibility(View.GONE);
                    isOpened = true;
                }
            }
            else if(isOpened)   //just closed
            {
                navBar.setVisibility(View.VISIBLE);
                isOpened = false;
            }
        }
    };
}
package com.martin.reciper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.martin.reciper.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    SharedPreferences settings;
    BottomNavigationView navbar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        Log.i("daco", "--------------------------------------");
        //LocaleList current = getResources().getConfiguration().getLocales();
        //Log.i("daco", "current locale: " + current.toString());

        navbar = findViewById(R.id.navbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //set toolbar as actionbar

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavController navController = ((NavHostFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))).getNavController();
        navController.addOnDestinationChangedListener(onDestinationChangedListener);

        //Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home).build();

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration); //ActionBar setup
        NavigationUI.setupWithNavController(navbar, navController); //Navbar setup
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration); //Toolbar setup //prepisovanie title

        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener); //keyboard listener

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Intent intent = getIntent();
        if(intent.getAction() != null)
        {
            if (intent.getAction().equals(Intent.ACTION_SEND))
            {
                resolveIntent(intent);
                setIntent(new Intent());
            }
        }

/*      Log.i("daco", "onStart()");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.fragment_home);
        HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if(hf == null) Log.e("daco", "HF null");
        else Log.i("daco", hf.toString());*/
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if(!settings.getString("language", "system").equals("system"))
            applyOverrideConfiguration(new Configuration());
    }

    @Override
    public void applyOverrideConfiguration(Configuration newConfig)
    {
        super.applyOverrideConfiguration(updateConfigurationLanguage(newConfig));
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("ConstantConditions")
    private Configuration updateConfigurationLanguage(Configuration config)
    {
        if (Build.VERSION.SDK_INT >= 24)
        {
            if (!config.getLocales().isEmpty())
                return config;
        }
        else
        {
            if (config.locale != null)
                return config;
        }

        String languageStr = settings.getString("language", "system");
        Locale newLocale = stringToLocale(languageStr);
        if(newLocale != null)
            config.setLocale(newLocale);

        return config;
    }

    private Locale stringToLocale(String s)
    {
        Log.i("daco", "Changing language to: " + s);
        StringTokenizer tempStringTokenizer = new StringTokenizer(s,"_");
        String language = new String();
        String country = new String();
        if(tempStringTokenizer.hasMoreTokens())
            language = (String) tempStringTokenizer.nextElement();
        if(tempStringTokenizer.hasMoreTokens())
            country = (String) tempStringTokenizer.nextElement();
        return new Locale(language, country);
    }

    @SuppressWarnings("deprecation") //progress dialog
    protected void resolveIntent(Intent intent)
    {
        //Log.i("daco", "global: " + intent.getStringExtra(Intent.EXTRA_TEXT));

        AtomicReference<Uri> AmediaURI = new AtomicReference<>();
        AtomicReference<String> AvideoTitle = new AtomicReference<>(new String());

        ClipData clipData = intent.getClipData();
        if(intent.getStringExtra(Intent.EXTRA_TEXT) != null)
        {
                Uri content = Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT));
                content.normalizeScheme();
                if(content.getAuthority() != null) // is URL
                {
                    if (content.getAuthority().contains("youtu.be") || content.getAuthority().contains("youtube.com"))
                    {
                        AmediaURI.set(content);
                        Log.i("daco", "content resolved: " + AmediaURI);
                    }
                    else //not youtube url
                    {
                        AmediaURI.set(content);
                        //Toast.makeText(this, "Content doesn't contains YouTube video URL", Toast.LENGTH_SHORT).show();
                        Log.i("daco", "not-youtube resolved: " + AmediaURI);
                    }
                }
                else
                {
                    Toast.makeText(this, "Not URL, text content", Toast.LENGTH_LONG).show();
                    return;
                }
        }
        else //empty extra text
        {
            Log.i("daco", "Wrong content");
            Toast.makeText(this, "Empty extra text content", Toast.LENGTH_LONG).show();
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
                URL url = new URL("https://www.youtube.com/oembed?url=" + AmediaURI.get() + "&format=json");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    stringBuilder.append(line);

                JSONObject json = new JSONObject(stringBuilder.toString());

                AvideoTitle.set(json.getString("title"));
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
        }); //thread
        thr.start();
        try {thr.join();} catch(InterruptedException e) {e.printStackTrace();}

        //dialog.dismiss(); // ??

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        try
        {
            //TODO ziskat korektnou cestou
            // alebo premiestnit onNewRecipe do aktivity
            assert navHostFragment != null;
            HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            Log.i("daco", "mam instanciu");
            if(hf != null)
                hf.onNewRecipe(AvideoTitle.get(), AmediaURI.get());
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
        String body = null;
        try
        {
            body = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        }
        catch(PackageManager.NameNotFoundException ignored) {}

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"martin.timko@centrum.sk", "martin.timko@ktu.edu"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from Reciper app");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_client)));
    }

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        boolean isOpened = false;

        @Override
        public void onGlobalLayout()
        {
            View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);

            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if(heightDiff > 500)  // difference between RootView a FragmentView, incluing all actionbar
            {
                if(!isOpened)   //just opened
                {
                    navbar.setVisibility(View.GONE);
                    isOpened = true;
                }
            }
            else if(isOpened)   //just closed
            {
                //navbar.setVisibility(View.VISIBLE);
                isOpened = false;
            }
        }
    };

    NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener()
    {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments)
        {
            //Toast.makeText(MainActivity.this, "navigation event", Toast.LENGTH_SHORT).show();
        }
    };
}
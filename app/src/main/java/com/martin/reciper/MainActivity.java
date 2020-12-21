package com.martin.reciper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.martin.reciper.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    AppActivity appActivity;

    SharedPreferences settings;
    BottomNavigationView navbar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        appActivity = new AppActivity();
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
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getAction() != null)
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

    protected void resolveIntent(Intent intent)
    {
        //Log.i("daco", "global: " + intent.getStringExtra(Intent.EXTRA_TEXT));

        Uri resolvedURI = null;
        String resolvedTitle = new String();

        if(intent.getStringExtra(Intent.EXTRA_TEXT) != null)
        {
                Uri contentURI = Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT));
                contentURI.normalizeScheme();
                if(contentURI.getAuthority() != null) // is URL
                {
                    if (contentURI.getAuthority().contains("youtu.be") || contentURI.getAuthority().contains("youtube.com"))
                    {
                        resolvedURI = contentURI;
                        resolvedTitle = resolveYoutubeTitle(resolvedURI);

                        Log.i("daco", "content resolved: " + resolvedURI);
                    }
                    else //not youtube url
                    {
                        HashMap<String, String> webPageResolved = resolveWebpage(contentURI);

                        resolvedTitle = webPageResolved.get("title");
                        resolvedURI = Uri.parse(webPageResolved.get("imageUri"));

                        Log.i("daco", "not-youtube resolved: " + resolvedURI);
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


        /// resolved, show NewRecipe dialog

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        try
        {
            //TODO ziskat korektnou cestou
            // alebo premiestnit onNewRecipe do aktivity
            assert navHostFragment != null;
            HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            Log.i("daco", "mam instanciu");
            if(hf != null)
                hf.onNewRecipe(resolvedTitle, resolvedURI);
        }
        catch(Exception e)
        {
            Log.e("daco", "WORNG FRAGMENT ASSIGMENT !!!");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private String resolveYoutubeTitle(Uri uri)
    {
        AtomicReference<String> resolvedTitle = new AtomicReference<>(new String());

        ProgressDialog loadingDialog; //todo inak?
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Fetching data. Please wait...");
        loadingDialog.show();

        Thread thr = new Thread(() ->
        {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try
            {
                URL url = new URL("https://www.youtube.com/oembed?url=" + uri + "&format=json");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    stringBuilder.append(line);

                JSONObject json = new JSONObject(stringBuilder.toString());
                resolvedTitle.set(json.getString("title"));
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                Log.w("daco", "nespravna URL");
            }
            catch (IOException | JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(connection != null) connection.disconnect();
                try {if(reader != null) reader.close();}
                catch(IOException e) {e.printStackTrace();}
            }
            //try {Thread.sleep(5000);}
            //catch(InterruptedException e) {e.printStackTrace();}
        }); //thread
        thr.start();
        try {thr.join();} catch(InterruptedException e) {e.printStackTrace();}

        loadingDialog.dismiss();
        return resolvedTitle.get();
    }

    private HashMap<String, String> resolveWebpage(Uri uri)
    {
        AtomicReference<String> title = new AtomicReference<>();
        AtomicReference<String> imageUri = new AtomicReference<>();

        Thread thr = new Thread(() ->
        {
            Document document;
            try {document = Jsoup.connect(uri.toString()).get();}
            catch (IOException e) {e.printStackTrace(); return;}

            title.set(getMetaTag(document, "og:title"));
            String ogImage = getMetaTag(document, "og:image");
            Log.i("daco", "image url: " + ogImage);

            InputStream inStream = null; //data from internet
            OutputStream outStream = null; //file
            try
            {
                inStream = new URL(ogImage).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                Uri mediaFileURI = createMediaFile(".jpg");
                outStream = getContentResolver().openOutputStream(mediaFileURI); //open stream from file uri
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream);
                imageUri.set(mediaFileURI.toString());
            }
            catch(IOException e)
            {
                Log.i("daco", "Streams fail");
                e.printStackTrace();
            }
            finally
            {
                try {inStream.close(); outStream.close();}
                catch(IOException e) {e.printStackTrace();}
            }
        });
        thr.start();
        try {thr.join();} catch(InterruptedException e) {e.printStackTrace();}

        return new HashMap<String, String>()
        {{
            put("title", title.get());
            put("imageUri", imageUri.get());
        }};
    }

    String getMetaTag(Document document, String attr) //todo to lambda
    {
        Elements elements = document.select("meta[name=" + attr + "]");
        for(Element element : elements)
        {
            final String s = element.attr("content");
            if(s != null) return s;
        }
        elements = document.select("meta[property=" + attr + "]");
        for(Element element : elements)
        {
            final String s = element.attr("content");
            if(s != null) return s;
        }
        return null;
    }

    public Uri createMediaFile(String suffix)
    {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "MEDIA_" + "_";
        File storageDir = getExternalFilesDir("media"); //Environment.DIRECTORY_PICTURES;
        Uri fileURI = null;
        try
        {
            File file = File.createTempFile(fileName, suffix, storageDir);
            fileURI = FileProvider.getUriForFile(this, "com.martin.reciper.fileprovider", file);
        }
        catch(IOException e) {e.printStackTrace();}

        return fileURI;
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


    public void onBackup()
    {
        Log.i("daco","backup run.");
        File storageDir = getExternalFilesDir("db");
        try
        {
            File db = getDatabasePath("Reciper_db");
            File copy = new File(storageDir + "/backup.db");
            Log.i("daco","here1");
            Files.copy(db.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Log.i("daco","here2");

            File tmp = new File(getExternalFilesDir("db")+"/backup.db");
            Log.i("daco", "exist: " + tmp.exists());
        }
        catch(IOException e) {e.printStackTrace();}

        /*File fileMetadata = new File();
        fileMetadata.setName("databaza.db");
        fileMetadata.setParents(Collections.singletonList("appDataFolder"));
        java.io.File filePath = new java.io.File("files/config.json");
        FileContent mediaContent = new FileContent("application/json", filePath);
        File file = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
        System.out.println("File ID: " + file.getId());
        Log.i("daco", "File ID: " + file.getId());*/

        return;
    }

    /*
    public void onRestore()
    {
        FileList files = driveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name)")
                .setPageSize(10)
                .execute();
        for (File file : files.getFiles()) {
            System.out.printf("Found file: %s (%s)\n",
                    file.getName(), file.getId());
        }
    }*/

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

    public AppActivity getAppActivity() {return appActivity;}

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
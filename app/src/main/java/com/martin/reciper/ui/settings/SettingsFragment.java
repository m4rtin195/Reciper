package com.martin.reciper.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.view.CameraView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.martin.reciper.MainActivity;
import com.martin.reciper.R;
import com.martin.reciper.models.PostsModel;
import com.martin.reciper.models.Units;
import com.martin.reciper.ui.MyProgressBar;
import com.martin.reciper.ui.MyView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat
{
    Preference preference_language;
    Preference preference_version;
    Preference preference_contactDeveloper;
    ListPreference pref_fav_weight_unit;
    ListPreference pref_fav_volume_unit;

    int counter = 0;

    //bordel
    SharedPreferences settings;
    boolean ok = false;
    Button tester;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    //GoogleMap googleMap;
    //CameraView camera;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        preference_language = findPreference("language");
        preference_version = findPreference("version");
        preference_contactDeveloper = findPreference("contact_developer");
        pref_fav_weight_unit = findPreference("fav_weight_unit");
        pref_fav_volume_unit = findPreference("fav_volume_unit");

        setUnitsLists();

        preference_contactDeveloper.setOnPreferenceClickListener(preference ->
        {
            ((MainActivity) requireActivity()).onContactDeveloper();
            return true;
        });
        preference_language.setOnPreferenceChangeListener((preference, newValue) ->
        {
            requireActivity().recreate();
            return true;
        });
        preference_version.setOnPreferenceClickListener((preference) ->
        {
            counter++;
            if (counter >= 5)
            {
                Toast.makeText(requireContext(), getString(R.string.easter_egg), Toast.LENGTH_LONG).show();
                counter = Integer.MIN_VALUE;
            }
            return true;
        });

        //return onCreateView1(inflater, container, savedInstanceState);  //todo remove this
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    private void setUnitsLists()
    {
        List<Units.Unit> list = Units.getWeightUnits();
        CharSequence[] entries = new CharSequence[list.size()];
        CharSequence[] values = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            entries[i] = list.get(i).getName();
            values[i] = Integer.toString(list.get(i).getId());
        }
        pref_fav_weight_unit.setEntries(entries);
        pref_fav_weight_unit.setEntryValues(values);

        list = Units.getVolumeUnits();
        entries = new CharSequence[list.size()];
        values = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            entries[i] = list.get(i).getName();
            values[i] = Integer.toString(list.get(i).getId());
        }
        pref_fav_volume_unit.setEntries(entries);
        pref_fav_volume_unit.setEntryValues(values);
    }

    public View onCreateView1(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settings = requireActivity().getPreferences(Context.MODE_PRIVATE);

        tester = view.findViewById(R.id.button_tester);
        tester.setOnClickListener(testerListener);


        /// defense 1

        EditText editText = view.findViewById(R.id.defenseEditText);
        TextView defenseTextView = view.findViewById(R.id.defenseTextView);
        Button button = view.findViewById(R.id.defenseButton);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String input = charSequence.toString();
                if (input.length() == 0)
                {
                    editText.setBackgroundResource(R.drawable.border_gray);
                    editText.setTextColor(Color.GRAY);
                    defenseTextView.setTextColor(Color.GRAY);
                    ok = false;
                } else
                {
                    editText.setBackgroundResource(R.drawable.border_red);
                    editText.setTextColor(Color.RED);
                    defenseTextView.setTextColor(Color.RED);
                    ok = false;
                    if (input.length() >= 8)
                    {
                        if (input.matches(".*\\d.*"))
                        {
                            if (input.matches(".*[A-Z].*"))
                            {
                                editText.setBackgroundResource(R.drawable.border_green);
                                editText.setTextColor(Color.GREEN);
                                defenseTextView.setTextColor(Color.GREEN);
                                ok = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        button.setOnClickListener(view1 ->
        {
            if (ok == true)
                Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        });

        /// defense end
        /// lab 2

        MyView indikator = view.findViewById(R.id.myview);

        int a = 0;
        button.setOnClickListener(view12 ->
        {
            ConstraintLayout layout = view.findViewById(R.id.layout);
            FrameLayout layout2 = view.findViewById(R.id.fullscr);

            ProgressBar abc = view.findViewById(R.id.progressBar);
            //layout.setVisibility();

            if (layout.indexOfChild(abc) > 0)
                layout.removeView(abc);
            else
                layout2.addView(abc);
            //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(getContext(),layout2.getWidth(),layout2.getHeight(),);
            //new FrameLayout.LayoutParams()
            //abc.setLayoutParams();

            //((ViewGroup)abc.getParent()).removeView(abc);

        /*Log.i("daco", "spustam sietovy request");
        Animation rotacia = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        Animation rotacia2 = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotacia2.setInterpolator(getContext(),android.R.anim.linear_interpolator);
        rotacia2.setDuration(1500);
        indikator.startAnimation(rotacia2);
        Toast.makeText(getContext(), "spustam asynctask", Toast.LENGTH_SHORT).show();
        prgbar.setProgress(30,true);

        new downloadTask(this).execute();
        MyProgressBar myPrgBar = view.findViewById(R.id.myPrgBar);
        myPrgBar.setProgress(80);*/
        });

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity) getActivity()).skuska();
            }
        });

        //indikator.setState(MyView.SUCCESS);

        MyProgressBar myPrgBar = view.findViewById(R.id.myPrgBar);
        myPrgBar.setProgress(20);


        //lab 3
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);


        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent)
            {
                Sensor sensor = sensorEvent.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    //textView1.setText(String.valueOf(sensorEvent.values[0]));
                    //textView2.setText(String.valueOf(sensorEvent.values[1]));
                    //textView3.setText(String.valueOf(sensorEvent.values[2]));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i)
            {
            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

/*
        camera = view.findViewById(R.id.camera);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getContext(), "Camera permission not granted!", Toast.LENGTH_LONG).show();
        }
        camera.bindToLifecycle(this);
        camera.enableTorch(true);*/

        return view;
    } //onCreateView1

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override public void onProviderEnabled(String s) {}
            @Override public void onProviderDisabled(String s) {}
            @Override public void onLocationChanged(Location location)
            {
                Log.i("daco", "onLocationChanged()");
                if (location != null)
                {
                    textView1.setText(Double.toString(location.getLatitude()));
                    textView2.setText(Double.toString(location.getLongitude()));
                }
            }
        };

     /*   if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(callback);
        }*/
    }

    public void onDownloadCompleted(boolean success, List<PostsModel> prispevky)
    {
        MyView indikator = requireActivity().findViewById(R.id.myview);

        if(success)
        {
            Log.i("daco", "prvy prispevok: " + prispevky.get(1).getBody());
            Toast.makeText(getContext(), "prvy prispevok: " + prispevky.get(1).getBody(), Toast.LENGTH_LONG).show();

            indikator.setState(MyView.SUCCESS);
            indikator.invalidate();
        }
        else
        {
            Log.i("daco", "result wrong ");
            Toast.makeText(getContext(), "Probleeem", Toast.LENGTH_LONG).show();
            indikator.setState(MyView.FAILED);
            indikator.invalidate();
        }
    }


    View.OnClickListener testerListener = view ->
    {
        //requireActivity().recreate();

        /*Uri locationUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/

        ((MainActivity)getActivity()).getAppActivity().deletedb();
        //((MainActivity)getActivity()).getAppActivity().repopulateDatabase(new File(requireContext().getExternalFilesDir("db")+"/backup.db");
        ((MainActivity)getActivity()).onBackup();
    };

/*
    private OnMapReadyCallback callback = new OnMapReadyCallback()
    {
        @Override
        public void onMapReady(GoogleMap gm)
        {
            Log.i("daco","mapReady");
            googleMap = gm;
            LatLng kaunas = new LatLng(54.898, 23.903);
            googleMap.addMarker(new MarkerOptions().position(kaunas).title("Marker in Kaunas"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kaunas, 12.0f));
        }
    };*/
}
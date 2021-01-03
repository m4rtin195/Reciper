package com.martin.reciper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.fragment.NavHostFragment;

import com.martin.reciper.models.PostsModel;
import com.martin.reciper.ui.MyProgressBar;
import com.martin.reciper.ui.MyView;
import com.martin.reciper.ui.home.HomeFragment;

import java.util.List;

@SuppressWarnings("DanglingJavadoc")
public class TestingActivity extends AppCompatActivity
{
    SharedPreferences settings;
    boolean ok = false;
    Button tester;
    TextView textView1;
    TextView textView2;
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        settings = getPreferences(Context.MODE_PRIVATE);

        tester = findViewById(R.id.button_tester);
        tester.setOnClickListener(testerButtonListener);


        /** defense 1 **/
        EditText editText = findViewById(R.id.defenseEditText);
        TextView defenseTextView = findViewById(R.id.defenseTextView);
        Button defenseButton = findViewById(R.id.defenseButton);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String input = charSequence.toString();
                if(input.length() == 0)
                {
                    editText.setBackgroundResource(R.drawable.border_gray);
                    editText.setTextColor(Color.GRAY);
                    defenseTextView.setTextColor(Color.GRAY);
                    ok = false;
                }
                else
                {
                    editText.setBackgroundResource(R.drawable.border_red);
                    editText.setTextColor(Color.RED);
                    defenseTextView.setTextColor(Color.RED);
                    ok = false;
                    if(input.length() >= 8)
                    {
                        if(input.matches(".*\\d.*"))
                        {
                            if(input.matches(".*[A-Z].*"))
                            {
                                editText.setBackgroundResource(R.drawable.border_green);
                                editText.setTextColor(Color.GREEN);
                                defenseTextView.setTextColor(Color.GREEN);
                                ok = true;
        }}}}}});

        defenseButton.setOnClickListener(view1 ->
        {
            if (ok == true)
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        });

        /** end defense 1 **/

        /** lab 2 **/
        Button button2 = findViewById(R.id.button2);
        MyView indikator = findViewById(R.id.myview);
        MyProgressBar myPrgBar = findViewById(R.id.myPrgBar);
            myPrgBar.setProgress(20);

        button2.setOnClickListener(view12 ->
        {
            /*Log.i("daco", "spustam sietovy request");
            Animation rotacia = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
            Animation rotacia2 = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotacia2.setInterpolator(getContext(), android.R.anim.linear_interpolator);
            rotacia2.setDuration(1500);
            indikator.startAnimation(rotacia2);

            Toast.makeText(getContext(), "spustam asynctask", Toast.LENGTH_SHORT).show();
            prgbar.setProgress(30,true);
            new downloadTask(this).execute();

            MyProgressBar myPrgBar = view.findViewById(R.id.myPrgBar);
            myPrgBar.setProgress(80);*/
        });
        /** end lab 2 **/

        /** lab 3 **/
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener()
        {
            @Override public void onAccuracyChanged(Sensor sensor, int i) {}
            @Override public void onSensorChanged(SensorEvent sensorEvent)
            {
                Sensor sensor = sensorEvent.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    //textView1.setText(String.valueOf(sensorEvent.values[0]));
                    //textView2.setText(String.valueOf(sensorEvent.values[1]));
                    //textView3.setText(String.valueOf(sensorEvent.values[2]));
                }
            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        /** end lab 3 **/
    }

    public void onDownloadCompleted(boolean success, List<PostsModel> prispevky)
    {
        MyView indikator = findViewById(R.id.myview);

        if(success)
        {
            Log.i("daco", "prvy prispevok: " + prispevky.get(1).getBody());
            Toast.makeText(this, "prvy prispevok: " + prispevky.get(1).getBody(), Toast.LENGTH_LONG).show();

            indikator.setState(MyView.SUCCESS);
            //indikator.invalidate();
        }
        else
        {
            Log.i("daco", "result wrong ");
            Toast.makeText(this, "Probleeem", Toast.LENGTH_LONG).show();
            indikator.setState(MyView.FAILED);
            //indikator.invalidate();
        }
    }


    View.OnClickListener testerButtonListener = view ->
    {
        //switch to TestingActivity
        Intent switchActIntent = new Intent(this, ScrollingActivity.class);
        startActivity(switchActIntent);

        //skuska();


        //requireActivity().recreate();

        /*Uri locationUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/

        //((MainActivity)getActivity()).getAppActivity().deletedb();
        //((MainActivity)getActivity()).getAppActivity().repopulateDatabase(new File(requireContext().getExternalFilesDir("db")+"/backup.db");
        //((MainActivity)getActivity()).onBackup();


        ConstraintLayout layout = findViewById(R.id.layout);
        FrameLayout layout2 = findViewById(R.id.fullscr);

        ProgressBar abc = findViewById(R.id.progressBar);
        //layout.setVisibility();
        if (layout.indexOfChild(abc) > 0)
            layout.removeView(abc);
        else
            layout2.addView(abc);
        //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(getContext(),layout2.getWidth(),layout2.getHeight(),);
        //new FrameLayout.LayoutParams()
        //abc.setLayoutParams();

        //((ViewGroup)abc.getParent()).removeView(abc);
    };


    public void skuska()
    {
        Log.i("daco", "skuska()");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.fragment_home);
        HomeFragment hf = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if(hf == null) Log.e("daco", "HF null");
        else Log.i("daco", hf.toString());
    }
}
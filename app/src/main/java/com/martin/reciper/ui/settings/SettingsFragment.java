package com.martin.reciper.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.martin.reciper.MainActivity;
import com.martin.reciper.models.Units;
import com.martin.reciper.ui.MyProgressBar;
import com.martin.reciper.ui.MyView;
import com.martin.reciper.models.PostsModel;
import com.martin.reciper.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat
{
    private SettingsViewModel settingsViewModel;
    SharedPreferences settings;
    SharedPreferences.Editor settingsEditor;
    boolean ok = false;

    Button tester;
    SwitchCompat switch1;
    Spinner spinnerLanguage;

    Preference preference_language;
    Preference preference_version;
    Preference preference_contactDeveloper;
    ListPreference pref_fav_weight_unit;
    ListPreference pref_fav_volume_unit;

    int counter = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

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
            if(counter >= 5)
            {
                Toast.makeText(requireContext(), getString(R.string.easter_egg), Toast.LENGTH_LONG).show();
                counter = Integer.MIN_VALUE;
            }
            return true;
        });

        //return onCreateView1(inflater, container, savedInstanceState);  //todo remove this
        return view;
    }

    private void setUnitsLists()
    {
        List<Units.Unit> list = Units.getWeightUnits();
        CharSequence[] entries = new CharSequence[list.size()];
        CharSequence[] values = new CharSequence[list.size()];
        for(int i=0; i<list.size(); i++)
        {
            entries[i] = list.get(i).getName();
            values[i] = Integer.toString(list.get(i).getId());
        }
        pref_fav_weight_unit.setEntries(entries);
        pref_fav_weight_unit.setEntryValues(values);

        list = Units.getVolumeUnits();
        entries = new CharSequence[list.size()];
        values = new CharSequence[list.size()];
        for(int i=0; i<list.size(); i++)
        {
            entries[i] = list.get(i).getName();
            values[i] = Integer.toString(list.get(i).getId());
        }
        pref_fav_volume_unit.setEntries(entries);
        pref_fav_volume_unit.setEntryValues(values);
    }

    public View onCreateView1(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settings = requireActivity().getPreferences(Context.MODE_PRIVATE);

        tester = view.findViewById(R.id.button_tester);
        tester.setOnClickListener(testerListener);

        spinnerLanguage = view.findViewById(R.id.spinner_language);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, Arrays.asList(getResources().getStringArray(R.array.languages_entries)));
        languagesAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerLanguage.setAdapter(languagesAdapter);
        spinnerLanguage.setSelection(settings.getInt("language", 0));
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                settingsEditor = settings.edit();
                settingsEditor.putInt("language", i).apply();

            }
        });

        final TextView textView = view.findViewById(R.id.text_notifications);
        settingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });


        switch1 = view.findViewById(R.id.switch1);
        switch1.setChecked(settings.getBoolean("test",false));
        switch1.setOnCheckedChangeListener((compoundButton, state) ->
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("test", state).apply();
        });


        /// defense

        EditText editText = view.findViewById(R.id.defenseEditText);
        TextView textView2 = view.findViewById(R.id.defenseTextView);
        Button button = view.findViewById(R.id.defenseButton);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String input = charSequence.toString();
                if(input.length()==0)
                {
                    editText.setBackgroundResource(R.drawable.border_gray);
                    editText.setTextColor(Color.GRAY);
                    textView2.setTextColor(Color.GRAY);
                    ok = false;
                }
                else
                {
                    editText.setBackgroundResource(R.drawable.border_red);
                    editText.setTextColor(Color.RED);
                    textView2.setTextColor(Color.RED);
                    ok = false;
                    if (input.length() >= 8)
                    {
                        if (input.matches(".*\\d.*"))
                        {
                            if (input.matches(".*[A-Z].*"))
                            {
                                editText.setBackgroundResource(R.drawable.border_green);
                                editText.setTextColor(Color.GREEN);
                                textView2.setTextColor(Color.GREEN);
                                ok = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        button.setOnClickListener(view1 ->
        {
            if(ok==true)
                Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        });

        /// defense end
        /// lab 2

        MyView indikator = view.findViewById(R.id.myview);
        ProgressBar prgbar = view.findViewById(R.id.progressBar2);

         int a=0;
        button.setOnClickListener(view12 ->
        {
            ConstraintLayout layout = view.findViewById(R.id.layout);
            FrameLayout layout2 = view.findViewById(R.id.fullscr);

            ProgressBar abc = view.findViewById(R.id.progressBar2);
            //layout.setVisibility();

            if(layout.indexOfChild(abc)>0)
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
                ((MainActivity)getActivity()).skuska();
            }
        });

        //indikator.setState(MyView.SUCCESS);

        MyProgressBar myPrgBar = view.findViewById(R.id.myPrgBar);
        myPrgBar.setProgress(20);

        //defense 3

        return view;
    }

    public void onDownloadCompleted(boolean success, List<PostsModel> prispevky)
    {
        MyView indikator = requireActivity().findViewById(R.id.myview);
        ProgressBar prgbar = requireActivity().findViewById(R.id.progressBar);

        if(success)
        {
            Log.i("daco", "prvy prispevok: " + prispevky.get(1).getBody());
            Toast.makeText(getContext(), "prvy prispevok: " + prispevky.get(1).getBody(), Toast.LENGTH_LONG).show();

            prgbar.setProgress(0, false);
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
        requireActivity().recreate();
    };
}
package com.martin.reciper.ui.settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.martin.reciper.MainActivity;
import com.martin.reciper.R;
import com.martin.reciper.models.Units;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat
{
    Preference preference_language;
    Preference preference_version;
    Preference preference_contactDeveloper;
    ListPreference pref_fav_weight_unit;
    ListPreference pref_fav_volume_unit;

    int counter = 0;

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
            onContactDeveloper();
            return true;
        });
        preference_language.setOnPreferenceChangeListener((preference, newValue) ->
        {
            requireActivity().recreate(); //todo potrebne?
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

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey);
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

    public void onContactDeveloper()
    {
        String body = null;
        try
        {
            body = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0).versionName;
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
}
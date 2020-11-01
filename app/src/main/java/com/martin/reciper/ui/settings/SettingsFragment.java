package com.martin.reciper.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.martin.reciper.R;
import com.martin.reciper.ui.recipe.RecipeFragment;

public class SettingsFragment extends Fragment
{
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = view.findViewById(R.id.text_notifications);
        settingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });

        final Button button = (Button) view.findViewById(R.id.stlacadlo);
        final TextView txt = view.findViewById(R.id.text_abcd);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new RecipeFragment());
                fragmentTransaction.commit();
                button.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);

            }
        });

        return view;
    }
}
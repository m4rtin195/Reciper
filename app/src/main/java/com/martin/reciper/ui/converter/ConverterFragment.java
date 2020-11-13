package com.martin.reciper.ui.converter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.martin.reciper.R;
import com.martin.reciper.ui.home.HomeFragment;

public class ConverterFragment extends Fragment
{
    private ConverterViewModel converterViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        converterViewModel = new ViewModelProvider(this).get(ConverterViewModel.class);
        View view = inflater.inflate(R.layout.fragment_converter, container, false);
        final TextView textView = view.findViewById(R.id.text_dashboard);
        converterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return view;
    }
}
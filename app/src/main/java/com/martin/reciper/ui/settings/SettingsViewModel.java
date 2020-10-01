package com.martin.reciper.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel
{
    private MutableLiveData<String> mText;

    public SettingsViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("settings fragment");
    }

    public LiveData<String> getText() { return mText; }
}
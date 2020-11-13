package com.martin.reciper.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class HomeViewModel extends ViewModel
{
    private MutableLiveData<String> mText;

    public HomeViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("home fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}
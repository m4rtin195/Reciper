package com.martin.reciper.ui.converter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConverterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ConverterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("converter fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
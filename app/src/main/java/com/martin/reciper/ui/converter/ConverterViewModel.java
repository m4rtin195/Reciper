package com.martin.reciper.ui.converter;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConverterViewModel extends ViewModel
{
    private MutableLiveData<String> mText;
    private MutableLiveData<Button> tlacitko;

    //final Button stlacadlo = (Button) findViewbyId()

    public ConverterViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("converter fragment");

        //tlacitko.seto
    }

    public LiveData<String> getText() {
        return mText;
    }
}
package com.martin.reciper.ui.converter;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConverterViewModel extends ViewModel
{
    private MutableLiveData<String> myText;
    private Bundle stateBundle;

    public ConverterViewModel()
    {
        myText = new MutableLiveData<>();
        //myText.setValue("converter fragment");

        //stateBundle = new Bundle();
    }

    public LiveData<String> getText() {return myText;}
    public Bundle getStateBundle() {return stateBundle;}
    public void setStateBundle(Bundle stateBundle) {this.stateBundle = stateBundle;}
}
package com.martin.reciper;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.martin.reciper.models.PostsModel;
import com.martin.reciper.ui.settings.SettingsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

@Deprecated
public class downloadTask extends AsyncTask<Void, Integer, List<PostsModel>>
{
    SettingsFragment caller;
    List<PostsModel> data;
    boolean success = false;

    public downloadTask(SettingsFragment caller)
    {
        this.caller = caller;
    }

    @Override
    protected List<PostsModel> doInBackground(Void...nn)
    {
        Log.i("daco","som v async");
        Retrofit retrofit = AppActivity.getRetrofit();
        placeholderApi api = retrofit.create(placeholderApi.class);

        Call<List<PostsModel>> call = api.getPosts();
        try
        {
            Response<List<PostsModel>> response = call.execute();

            if(response.isSuccessful())
            {
                Log.i("daco", "poziadavka uspesna, HTTP: " + response.code());
                data = response.body();
                success = true;
            }
            else
            {
                Log.i("daco", "poziadavka zlyhala, HTTP: " + response.code());
            }
        }
        catch (IOException e)
        {
            Log.i("daco", "error in Retrofit callback: " + e.getMessage());
            data = new ArrayList<PostsModel>();
            e.printStackTrace();
        }

        Log.i("daco", "request skoncil, size: " + data.size());
        SystemClock.sleep(2000);
        return data;
    }

    @Override
    protected void onPostExecute(List<PostsModel> results)
    {
        Log.i("daco", "som v postexecute, size: " + results.size());
        //caller.onDownloadCompleted(success, results);
    }
}

package com.martin.reciper;

import com.martin.reciper.models.PostsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

@Deprecated
public interface placeholderApi
{
    @GET("posts")
    Call<List<PostsModel>> getPosts();
}

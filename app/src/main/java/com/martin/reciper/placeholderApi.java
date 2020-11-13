package com.martin.reciper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface placeholderApi
{
    @GET("posts")
    Call<List<PostsModel>> getPosts();
}

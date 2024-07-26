package com.example.thirdAndroidApp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("users")
    Call<UserResponse> getUsers(@Query("limit") int limit, @Query("skip") int skip);
}

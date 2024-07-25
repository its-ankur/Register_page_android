package com.example.thirdAndroidApp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("users")
    Call<UserResponse> getUsers(); // Removed pagination parameters
}

package com.example.thirdAndroidApp;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstandroidapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api extends AppCompatActivity {
    private static final String API_URL = "https://dummyjson.com/";
    private List<UserModel> allUsersList = new ArrayList<>();
    private List<UserModel> displayedUsersList = new ArrayList<>();
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 5;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        userAdapter = new UserAdapter(displayedUsersList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        fetchAllUsers();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        isLoading = true;
                        loadMoreUsers();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchAllUsers() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<UserResponse> call = apiInterface.getUsers(); // No pagination parameters
        Log.d("api", "Request URL: " + call.request().url());

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse.User> apiUsers = response.body().getUsers();

                    for (UserResponse.User apiUser : apiUsers) {
                        String hairColor = apiUser.getHair().getColor();
                        String address = apiUser.getAddress().getAddress();
                        String companyDepartment = apiUser.getCompany().getDepartment();
                        String companyAddress = apiUser.getCompany().getAddress().getAddress();
                        String imageUrl=apiUser.getImage();

                        UserModel userModel = new UserModel(
                                apiUser.getFirstName(),
                                apiUser.getMaidenName(),
                                apiUser.getLastName(),
                                hairColor,
                                address,
                                companyDepartment,
                                companyAddress,
                                imageUrl
                        );

                        allUsersList.add(userModel);
                    }

                    loadMoreUsers();
                } else {
                    Log.e("api", "onResponse: Response failed, code: " + response.code() + ", message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Log.e("api", "onFailure: " + throwable.getLocalizedMessage());
                isLoading = false;
            }
        });
    }

    private void loadMoreUsers() {
        int start = displayedUsersList.size();
        int end = Math.min(start + PAGE_SIZE, allUsersList.size());

        for (int i = start; i < end; i++) {
            displayedUsersList.add(allUsersList.get(i));
        }

        userAdapter.notifyDataSetChanged();
        isLoading = false;
    }
}

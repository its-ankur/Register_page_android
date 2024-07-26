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
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isLoading = false;

    private List<UserModel> allUsersList = new ArrayList<>();
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar, progressBarLoad;
    private boolean loadBar = false;

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
        progressBarLoad = findViewById(R.id.progressBarLoad);
        userAdapter = new UserAdapter(this, allUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        // Fetch initial users
        fetchUsers(loadBar);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();

                    Log.d("ScrollListener", "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisibleItems: " + pastVisibleItems);

                    // Trigger fetch before reaching the end
                    if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount - 5) {
                        Log.d("ScrollListener", "Fetching next page: " + (currentPage + 1));
                        isLoading = true;
                        currentPage++;
                        loadBar = true;
                        fetchUsers(loadBar);
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

    private void fetchUsers(boolean loadBar) {
        if (loadBar) {
            progressBarLoad.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        int skip = currentPage * PAGE_SIZE;
        Log.d("fetchUsers", "Fetching users with skip: " + skip);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<UserResponse> call = apiInterface.getUsers(PAGE_SIZE, skip);
        Log.d("api", "Request URL: " + call.request().url());

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (loadBar) {
                    progressBarLoad.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                isLoading = false;  // Reset loading state
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse.User> apiUsers = response.body().getUsers();
                    Log.d("fetchUsers", "Received " + apiUsers.size() + " users");

                    if (!apiUsers.isEmpty()) {
                        for (UserResponse.User apiUser : apiUsers) {
                            String id = String.valueOf(apiUser.getId()); // Ensure id is a String
                            String hairColor = apiUser.getHair().getColor();
                            String address = apiUser.getAddress().getAddress();
                            String companyDepartment = apiUser.getCompany().getDepartment();
                            String companyAddress = apiUser.getCompany().getAddress().getAddress();
                            String imageUrl = apiUser.getImage();

                            UserModel userModel = new UserModel(
                                    id,
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
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("fetchUsers", "No more users to load");
                    }
                } else {
                    Log.e("api", "onResponse: Response failed, code: " + response.code() + ", message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                if (loadBar) {
                    progressBarLoad.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                isLoading = false;  // Reset loading state
                Log.e("api", "onFailure: " + throwable.getLocalizedMessage());
            }
        });
    }
}

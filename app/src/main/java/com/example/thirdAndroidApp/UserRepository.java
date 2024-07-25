package com.example.thirdAndroidApp;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final int PAGE_SIZE = 5;

    public DataSource<Integer, ApiUser> getUserDataSource() {
        return new PageKeyedDataSource<Integer, ApiUser>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ApiUser> callback) {
                // Fetch initial data from API or database
                List<ApiUser> users = fetchUsersFromApi(1, PAGE_SIZE);
                callback.onResult(users, null, 2); // Pass initial data and next page key
            }

            @Override
            public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ApiUser> callback) {
                // Fetch data for previous pages if needed
            }

            @Override
            public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ApiUser> callback) {
                // Fetch data for the next page
                List<ApiUser> users = fetchUsersFromApi(params.key, PAGE_SIZE);
                callback.onResult(users, params.key + 1); // Pass next page key
            }
        };
    }

    private List<ApiUser> fetchUsersFromApi(int page, int pageSize) {
        // Implement API call to fetch user data
        return new ArrayList<>();
    }
}

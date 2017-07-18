package com.ik.bakingapp.recipeList;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ik.bakingapp.common.RecipeApiService;
import com.ik.bakingapp.common.Util;
import com.ik.bakingapp.common.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


class RecipeViewModel extends AndroidViewModel {

    private MutableLiveData<List<Recipe>> mRecipeList = new MutableLiveData<>();
    private MutableLiveData<String> mRecipeListResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsOnline = new MutableLiveData<>();
    {
        mRecipeList.setValue(null);
        mRecipeListResponse.setValue(null);
    }

    private final RecipeApiService apiService;

    private RecipeViewModel(Application application, RecipeApiService apiService) {
        super(application);
        this.apiService = apiService;
    }

    LiveData<String> observeRecipeListResponse() {
        return mRecipeListResponse;
    }

    LiveData<List<Recipe>> fetchRecipeList() {
        if (mRecipeList.getValue() == null && Util.isOnline(this.getApplication())) {
            fetchList();
        }
        return mRecipeList;
    }

    void fetchList(){
        Call<List<Recipe>> recipeList = apiService.getRecipeList();
        recipeList.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                List<Recipe> retrievedList = response.body();
                Timber.i("Number of recipes: " + retrievedList.size());
                mRecipeList.setValue(retrievedList);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Timber.i("Error fetching recipe api: " + t.getMessage());
                mRecipeListResponse.setValue(t.getMessage());
            }
        });
    }
    Recipe getRecipeAtPosition(int position) {
        List<Recipe> recipeList = mRecipeList.getValue();
        return recipeList.get(position);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application application;
        private final RecipeApiService apiService;
        private static RecipeViewModel recipeViewModel;

        public Factory(@NonNull Application application, @NonNull RecipeApiService apiService) {
            this.application = application;
            this.apiService = apiService;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (recipeViewModel == null) {
                recipeViewModel = new RecipeViewModel(application, apiService);
            }
            return (T) recipeViewModel;
        }
    }
}

package com.ik.bakingapp.common;

import com.ik.bakingapp.common.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeApiService {
    @GET("baking.json")
    Call<List<Recipe>> getRecipeList();
}

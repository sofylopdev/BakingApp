package com.sofialopes.android.bakingapp.data.retrofit;

import com.sofialopes.android.bakingapp.data.models.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sofia on 4/14/2018.
 */

public interface RecipesService {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipesList();
}

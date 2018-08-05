package com.sofialopes.android.bakingapp.data.retrofit;

/**
 * Created by Sofia on 4/14/2018.
 */

public class RetrofitUtils {
    private static final String BASE_RECIPES_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static RecipesService getRecipesService(){
        return RetrofitClient.getClient(BASE_RECIPES_URL).create(RecipesService.class);
    }
}

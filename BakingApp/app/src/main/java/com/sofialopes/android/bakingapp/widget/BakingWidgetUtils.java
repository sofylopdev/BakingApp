package com.sofialopes.android.bakingapp.widget;

import android.content.Context;
import android.content.SharedPreferences;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Ingredient;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.INGREDIENTS_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_ID_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_NAME_PREFS;


/**
 * Created by Sofia on 4/22/2018.
 */

public class BakingWidgetUtils {

   public static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(appWidgetId), MODE_PRIVATE);
        String titleValue = prefs.getString(RECIPE_NAME_PREFS, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    public static String loadIngredients(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(appWidgetId), MODE_PRIVATE);
        String ingredientsList = prefs.getString(INGREDIENTS_PREFS, null);
        if (ingredientsList != null) {
            return ingredientsList;
        } else {
            return " ";
        }
    }

   public static int loadId(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(appWidgetId), MODE_PRIVATE);
        int recipeId = prefs.getInt(RECIPE_ID_PREFS, -1);
        return recipeId;
    }

   public static void removeWidgetFromSharedPreferences(int appWidgetId) {
        String filePath  = "data/data/com.sofialopes.android.bakingapp/shared_prefs/"
                + String.valueOf(appWidgetId)+".xml";

        File deletePrefFile = new File(filePath );
        deletePrefFile.delete();
    }

    public static String getIngredients(ArrayList<Ingredient> ingredientsList) {
        StringBuilder allIngredients = new StringBuilder();
        for (Ingredient ingredient : ingredientsList) {
            String eachIngredient = "- " + ingredient.getIngredient()
                    + " (" + ingredient.getQuantity() + ingredient.getMeasure() + ")"
                    + "\n";
            allIngredients.append(eachIngredient);
        }

        return allIngredients.toString();
    }
}

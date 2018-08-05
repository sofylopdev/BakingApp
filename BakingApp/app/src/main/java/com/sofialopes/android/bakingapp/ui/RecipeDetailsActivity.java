package com.sofialopes.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Recipe;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_INTENT_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_NAME_TESTING;


public class RecipeDetailsActivity extends AppCompatActivity {

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Intent intent = getIntent();
        mRecipe = intent.getParcelableExtra(RECIPE_INTENT_EXTRA);

        String nameForTest = intent.getStringExtra(RECIPE_NAME_TESTING);
        String mRecipeName = mRecipe.getName();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mRecipeName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_INTENT_EXTRA)) {
            mRecipe = savedInstanceState.getParcelable(RECIPE_INTENT_EXTRA);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Updating the recipe in the fragment
        FragmentManager frgManager = getSupportFragmentManager();
        Fragment staticFragment = frgManager.findFragmentById(R.id.static_fragment_container);

        if (staticFragment != null) {
            ((RecipeDetailsFragment) staticFragment).getRecipeFromActivity(mRecipe);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_INTENT_EXTRA, mRecipe);
    }
}

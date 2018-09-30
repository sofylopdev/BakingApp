package com.sofialopes.android.bakingapp.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.adapters.MainAdapter;
import com.sofialopes.android.bakingapp.data.models.Ingredient;
import com.sofialopes.android.bakingapp.data.models.Recipe;
import com.sofialopes.android.bakingapp.data.retrofit.RecipesService;
import com.sofialopes.android.bakingapp.data.retrofit.RetrofitUtils;
import com.sofialopes.android.bakingapp.widget.BakingWidgetUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.INGREDIENTS_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPES_LIST;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_ID_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_INTENT_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_NAME_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_NAME_TESTING;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.SAVING_RECYCLER_POSITION;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.WIDGET_ID;


public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler {

    @BindView(R.id.recipes_recycler)
    RecyclerView mRecipesRecycler;
    @BindView(R.id.recipe_list_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message)
    TextView mErrorMessage;

    private RecipesService mRecipesService;
    private ArrayList<Recipe> mDownloadedList;
    private MainAdapter mAdapter;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Bundle mExtras;

    @Nullable
    private CountingIdlingResource espressoTestIdlingResource;

    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource() {
        if (espressoTestIdlingResource == null) {
            espressoTestIdlingResource = new CountingIdlingResource("Network_Call");
        }
        return espressoTestIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getIdlingResource();

        mRecipesService = RetrofitUtils.getRecipesService();

        GridLayoutManager layoutManager;
        int nrOfColumns = getResources().getInteger(R.integer.main_grid_nr_columns);
        layoutManager = new GridLayoutManager(this, nrOfColumns);
        mRecipesRecycler.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        mExtras = intent.getExtras();

        //Starting from the widget
        if (mExtras != null && mExtras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            mAppWidgetId = mExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            //Setting the recipe in the widget for the first time
            // activity shows the add widget button:
            mAdapter = new MainAdapter(this, null, this, true);
            mRecipesRecycler.setAdapter(mAdapter);

        } else {
            //Not starting the activity from the widget
            mAdapter = new MainAdapter(this, null, this, false);
            mRecipesRecycler.setAdapter(mAdapter);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVING_RECYCLER_POSITION)) {
            if (mDownloadedList == null || mDownloadedList.isEmpty()) {
                if (savedInstanceState.containsKey(RECIPES_LIST)) {
                    mDownloadedList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
                    mAdapter.updateRecipesList(mDownloadedList);
                }
                Parcelable recyclerState = savedInstanceState.getParcelable(SAVING_RECYCLER_POSITION);
                mRecipesRecycler.getLayoutManager().onRestoreInstanceState(recyclerState);
            }
        } else {
            makeRecyclerVisible(false);
            downloadList();
        }
    }

    private void downloadList() {
        Timber.d(mRecipesService.getRecipesList().request().url().toString());

        if (espressoTestIdlingResource != null) {
            // incrementing idling resource to tell Espresso to wait for the RetroFit network's call
            espressoTestIdlingResource.increment();
        }

        Call<ArrayList<Recipe>> call = mRecipesService.getRecipesList();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Timber.i("Got response");

                if (espressoTestIdlingResource != null) {
                    // decrementing the idling resource tells Espresso that the
                    // Retrofit Network call has been completed
                    espressoTestIdlingResource.decrement();
                }

                if (response.isSuccessful()) {
                    makeRecyclerVisible(true);

                    mDownloadedList = response.body();
                    Timber.d("Successful response: " + mDownloadedList);

                    if (mExtras != null) {
                        //Starting from the widget
                        if (mExtras.containsKey(RECIPE_ID_PREFS)) {
                            int RecipeId = mExtras.getInt(RECIPE_ID_PREFS);
                            if (RecipeId == -1) {
                                //when the widget is created for the first time he doesn't have
                                // the correct id value in the provider
                                RecipeId = BakingWidgetUtils.loadId(getApplicationContext(),
                                        mExtras.getInt(WIDGET_ID));
                            }
                            //When clicking on the widget, we go to the RecipeDetailsActivity for
                            //the corresponding recipe type
                            Intent goToSpecificRecipe = new Intent(getApplicationContext(),
                                    RecipeDetailsActivity.class);
                            for (Recipe eachRecipe : mDownloadedList) {
                                if (eachRecipe.getId() == RecipeId) {
                                    goToSpecificRecipe.putExtra(RECIPE_INTENT_EXTRA, eachRecipe);
                                    startActivity(goToSpecificRecipe);
                                    finish();
                                    return;
                                }
                            }
                        } else {
                            Timber.d("Doesn't have the recipe extra.");
                            //The widget was created and user needs to select one of the available
                            // recipes
                            mAdapter.updateRecipesList(mDownloadedList);

                        }
                    } else {
                        //Not starting from the widget
                        mAdapter.updateRecipesList(mDownloadedList);
                    }

                } else {
                    Timber.d("Error with response: " + response.code());
                    setErrorMessage(getString(R.string.response_unsuccessful));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

                if (espressoTestIdlingResource != null)
                    espressoTestIdlingResource.decrement();

                Timber.d("Failed to get response: " + t.getMessage());
                setErrorMessage(getString(R.string.call_failed));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVING_RECYCLER_POSITION, mRecipesRecycler.getLayoutManager()
                .onSaveInstanceState());
        outState.putParcelableArrayList(RECIPES_LIST, mDownloadedList);
    }

    @Override
    public void onRecipeClick(boolean fromWidget, Recipe recipe) {

        if (fromWidget) {
            //Saving the recipe in Shared Preferences
            SharedPreferences.Editor prefs = this.getSharedPreferences(
                    String.valueOf(mAppWidgetId),
                    MODE_PRIVATE)
                    .edit();
            prefs.putString(RECIPE_NAME_PREFS, recipe.getName());
            prefs.putString(INGREDIENTS_PREFS,
                    BakingWidgetUtils.getIngredients((ArrayList<Ingredient>) recipe.getIngredients()));
            prefs.putInt(RECIPE_ID_PREFS, recipe.getId());
            prefs.apply();
            prefs.commit();

            //Setting the widget
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.baking_widget);
            views.setTextViewText(R.id.appwidget_title, recipe.getName());
            views.setTextViewText(R.id.appwidget_ingredients,
                    BakingWidgetUtils.getIngredients((ArrayList<Ingredient>) recipe.getIngredients()));

            //Intent that starts the activity after the widget was created
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(WIDGET_ID, mAppWidgetId);
            intent.putExtra(RECIPE_ID_PREFS, recipe.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, mAppWidgetId, intent, 0);
            views.setOnClickPendingIntent(R.id.appwidget, pendingIntent);

            //Update in widget manager
            widgetManager.updateAppWidget(mAppWidgetId, views);

            //Close the activity after choosing the recipe
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();

        } else {
            Intent intent = new Intent(this, RecipeDetailsActivity.class);
            intent.putExtra(RECIPE_INTENT_EXTRA, recipe);
            intent.putExtra(RECIPE_NAME_TESTING, recipe.getName());
            startActivity(intent);
        }
    }

    private void makeRecyclerVisible(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecipesRecycler.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecipesRecycler.setVisibility(View.INVISIBLE);
        }
    }

    private void setErrorMessage(String message) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(message);
    }
}

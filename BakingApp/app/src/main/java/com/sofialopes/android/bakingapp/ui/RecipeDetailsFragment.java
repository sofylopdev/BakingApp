package com.sofialopes.android.bakingapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.adapters.IngredientsAdapter;
import com.sofialopes.android.bakingapp.adapters.StepsAdapter;
import com.sofialopes.android.bakingapp.data.models.Ingredient;
import com.sofialopes.android.bakingapp.data.models.Recipe;
import com.sofialopes.android.bakingapp.data.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_INTENT_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.SAVING_RECYCLER_POSITION;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_CLICKED_INDEX;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_DETAILS_BUNDLE_AND_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.TAG_TWO_PANE;


/**
 * Created by Sofia on 4/14/2018.
 */

public class RecipeDetailsFragment extends Fragment
        implements StepsAdapter.StepsAdapterOnClickHandler {

    private Recipe mRecipe;
    private ArrayList<Ingredient> mIngredientsList;
    private ArrayList<Step> mStepsList;

    @BindView(R.id.recycler_recipe_ingredients)
    RecyclerView ingredientsRV;
    @BindView(R.id.recycler_recipe_steps)
    RecyclerView mStepsRV;
    @BindView(R.id.steps_label)
    TextView mStepsLabel;

    private IngredientsAdapter mIngredientsAdapter;
    private StepsAdapter mStepsAdapter;

    private int mStepClickedIndex = -1;

    public RecipeDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_recipe_details,
                container,
                false);

        ButterKnife.bind(this, rootView);

        settingTheUi();

        return rootView;
    }

    public void getRecipeFromActivity(Recipe recipe) {
        mRecipe = recipe;
    }

    @Override
    public void onResume() {
        super.onResume();

        //This is called after getRecipeFromActivity to update the recipe
        if (mRecipe != null) {
            mIngredientsList = (ArrayList<Ingredient>) mRecipe.getIngredients();
            mStepsList = (ArrayList<Step>) mRecipe.getSteps();

            mIngredientsAdapter.updateIngredientsList(mIngredientsList);
            mStepsAdapter.updateStepsList(mStepsList);
        }

        mStepsLabel.setText(
                getContext().getResources().getString(R.string.step_list_label, mStepsList.size()));
    }

    private void settingTheUi() {
        if (mRecipe != null) {
            mIngredientsList = (ArrayList<Ingredient>) mRecipe.getIngredients();
            mStepsList = (ArrayList<Step>) mRecipe.getSteps();
        } else {
            mIngredientsList = new ArrayList<>();
            mStepsList = new ArrayList<>();
        }

        LinearLayoutManager ingredientsManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        ingredientsRV.setLayoutManager(ingredientsManager);
        mIngredientsAdapter = new IngredientsAdapter(getContext(), mIngredientsList);
        ingredientsRV.setAdapter(mIngredientsAdapter);

        LinearLayoutManager stepsManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mStepsRV.setLayoutManager(stepsManager);
        mStepsAdapter = new StepsAdapter(getContext(), mStepsList, this);

        mStepsRV.setAdapter(mStepsAdapter);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STEP_CLICKED_INDEX)) {
                mStepClickedIndex = savedInstanceState.getInt(STEP_CLICKED_INDEX);

                //Restoring the color in the step clicked
                if (mStepClickedIndex != -1 && mStepsRV != null) {
                    mStepsRV.getLayoutManager()
                            .scrollToPosition(mStepClickedIndex);

                    //Delay necessary because the viewHolder takes a bit of time to be created
                    // and that leads to NullPointerException
                    mStepsRV.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View view = mStepsRV
                                    .findViewHolderForAdapterPosition(mStepClickedIndex).itemView;
                            view.setBackgroundColor(
                                    getResources().getColor(R.color.colorAccent));

                            TextView textView = (TextView) view.findViewById(R.id.step);
                            textView.setBackgroundColor(
                                    getResources().getColor(R.color.colorAccent));
                            textView.setTextColor(
                                    getResources().getColor(R.color.colorBackground));
                        }
                    }, 50);
                }
            }

            Parcelable recyclerState = savedInstanceState.getParcelable(SAVING_RECYCLER_POSITION);
            mStepsRV.getLayoutManager().onRestoreInstanceState(recyclerState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_CLICKED_INDEX, mStepClickedIndex);
        outState.putParcelable(SAVING_RECYCLER_POSITION, mStepsRV.getLayoutManager()
                .onSaveInstanceState());
    }

    @Override
    public void onStepClick(View v, Step step, int position) {
        //Set normal colors to all views (if one view was clicked before, this will 'reset' that)
        for (int i = 0; i < mStepsRV.getLayoutManager().getChildCount(); i++) {
            View view = mStepsRV.getLayoutManager().getChildAt(i);

            view.setBackgroundColor(
                    getContext().getResources().getColor(R.color.colorPrimary));
            TextView textView = (TextView) view.findViewById(R.id.step);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(
                    getResources().getColor(R.color.colorPrimary));
        }

        //Two Pane
        if (getResources().getBoolean(R.bool.twoPane)) {
            mStepClickedIndex = position;

            v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            TextView textView = (TextView) v.findViewById(R.id.step);
            textView.setTextColor(getResources().getColor(R.color.colorBackground));
            textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            StepDetailsFragment stepDetailsFragment =
                    StepDetailsFragment.newInstance(step, mStepClickedIndex, -1);
            FragmentManager frgManager = getActivity().getSupportFragmentManager();
            frgManager.beginTransaction()
                    .replace(R.id.step_details_container, stepDetailsFragment, TAG_TWO_PANE)
                    .commit();

            //Single Pane
        } else {
            Intent intent = new Intent(getContext(), StepDetailsActivity.class);
            intent.putExtra(RECIPE_INTENT_EXTRA, mRecipe);
            intent.putExtra(STEP_DETAILS_BUNDLE_AND_EXTRA, step);
            startActivity(intent);
        }
    }
}

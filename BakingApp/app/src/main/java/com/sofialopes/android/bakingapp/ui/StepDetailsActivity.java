package com.sofialopes.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Recipe;
import com.sofialopes.android.bakingapp.data.models.Step;
import com.sofialopes.android.bakingapp.interfaces.PlayerListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.PLAYER_POSITION_DETAILS_ACTIVITY;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_INTENT_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_DETAILS_BUNDLE_AND_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_SELECTED_DETAILS_ACTIVITY;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.TAG_LANDSCAPE;


public class StepDetailsActivity extends AppCompatActivity
        implements PlayerListener {

    @Nullable
    @BindView(R.id.pager)
    ViewPager mPager;

    @Nullable
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private MyPagerAdapter mPagerAdapter;

    private Recipe mRecipe;
    private ArrayList<Step> mStepsList;
    private int mNumPages = 0;

    private int mStepSelected;
    private long mPlayerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Step step = intent.getParcelableExtra(STEP_DETAILS_BUNDLE_AND_EXTRA);
        mRecipe = intent.getParcelableExtra(RECIPE_INTENT_EXTRA);
        String recipeName = mRecipe.getName();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.steps_activity_title, recipeName));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mStepsList = (ArrayList<Step>) mRecipe.getSteps();
        mNumPages = mStepsList.size();
        mStepSelected = step.getId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        //Portrait
        if (!getResources().getBoolean(R.bool.isLandscape)) {

            Bundle bundle = new Bundle();

            if (savedInstanceState != null) {

                //when back to portrait, removing the landscape fragment
                // because it was causing the videos to be run twice
                Fragment fragmentToRemove = fragmentManager.findFragmentByTag(TAG_LANDSCAPE);
                fragmentManager.beginTransaction()
                        .remove(fragmentToRemove)
                        .commitNow();

                settingStepAndPlayerValuesFromSavedBundle(savedInstanceState);

                bundle.putLong(PLAYER_POSITION_DETAILS_ACTIVITY, mPlayerPosition);
                bundle.putParcelable(STEP_DETAILS_BUNDLE_AND_EXTRA, mStepsList.get(mStepSelected));
                bundle.putInt(STEP_SELECTED_DETAILS_ACTIVITY, mStepSelected);

                //Replacing data in the bundle in the fragment
                for (Fragment each : fragmentManager.getFragments()) {
                    Bundle eachArguments = each.getArguments();

                    Timber.d("Fragment in manager: " + each.getTag() + ", "
                            + ((StepDetailsFragment) each).getFragId());

                    if (eachArguments != null
                            && eachArguments.containsKey(STEP_DETAILS_BUNDLE_AND_EXTRA)) {

                        //If the id of the step in the fragment matches the step selected,
                        // setting the bundle values, from the landscape fragment, in the
                        // fragment in the viewpager
                        if (((Step) eachArguments.getParcelable(STEP_DETAILS_BUNDLE_AND_EXTRA))
                                .getId() == mStepSelected) {
                            each.setArguments(bundle);
                        } else {
                            //for the rest of the fragments, resetting the current selected tab
                            Bundle bundleOtherFrags = each.getArguments();
                            bundleOtherFrags.putInt(STEP_SELECTED_DETAILS_ACTIVITY, -1);
                            each.setArguments(bundleOtherFrags);
                        }
                    }
                }
            }

            //Setting the adapter, tabLayout listener and current item
            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(null);
            mPager.setAdapter(mPagerAdapter);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mStepSelected = tab.getPosition();
                    mPagerAdapter.playVideoInFragment(mStepSelected);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    mPagerAdapter.pauseCurrentVideoInFragment(tab.getPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            mPager.setCurrentItem(mStepSelected);
            tabLayout.setupWithViewPager(mPager);

            //Landscape
        } else {
            if (savedInstanceState == null) {
                creatingLandscapeFragment(false, fragmentManager, step);

            } else {
                settingStepAndPlayerValuesFromSavedBundle(savedInstanceState);

                creatingLandscapeFragment(true, fragmentManager, step);
            }
        }
    }

    @Override
    public void getPlayerPosition(long playerPosition) {
        this.mPlayerPosition = playerPosition;
        Timber.d("Player position: " + playerPosition);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYER_POSITION_DETAILS_ACTIVITY, mPlayerPosition);
        outState.putInt(STEP_SELECTED_DETAILS_ACTIVITY, mStepSelected);
    }

    private void settingStepAndPlayerValuesFromSavedBundle(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STEP_SELECTED_DETAILS_ACTIVITY)) {
            mStepSelected = savedInstanceState.getInt(STEP_SELECTED_DETAILS_ACTIVITY);
        }
        if (savedInstanceState.containsKey(PLAYER_POSITION_DETAILS_ACTIVITY)) {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION_DETAILS_ACTIVITY);
        }
    }

    private void creatingLandscapeFragment(boolean hasSavedInstanceState,
                                           FragmentManager fragmentManager,
                                           Step step) {
        StepDetailsFragment stepFragment;

        if (hasSavedInstanceState) {
            stepFragment = StepDetailsFragment.newInstance(
                    mStepsList.get(mStepSelected),
                    mStepSelected,
                    mPlayerPosition);
        } else {
            stepFragment = StepDetailsFragment.newInstance(
                    step,
                    mStepSelected,
                    -1);
        }

        fragmentManager.beginTransaction()
                .replace(R.id.land_frag_container, stepFragment, TAG_LANDSCAPE)
                .commitNow();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public StepDetailsFragment getItem(int position) {
            return StepDetailsFragment.newInstance(
                    mStepsList.get(position),
                    mStepSelected,
                    mPlayerPosition);
        }

        public void playVideoInFragment(int tabSelected) {
            for (Fragment eachFragment : mFragmentManager.getFragments()) {
                ((StepDetailsFragment) eachFragment).tabClicked(tabSelected);
            }
        }

        public void pauseCurrentVideoInFragment(int tabUnselected) {
            for (Fragment eachFragment : mFragmentManager.getFragments()) {
                //if the fragment has an id equal to the tab unselected, activate method tabUnclicked
                if (((StepDetailsFragment) eachFragment).getFragId() == tabUnselected) {
                    ((StepDetailsFragment) eachFragment).tabUnclicked(tabUnselected);
                }
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.step_title_tab, mStepsList.get(position).getId());
        }

        @Override
        public int getCount() {
            return mNumPages;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mRecipe != null) {
                    Intent intent = new Intent(this, RecipeDetailsActivity.class);
                    intent.putExtra(RECIPE_INTENT_EXTRA, mRecipe);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }
}

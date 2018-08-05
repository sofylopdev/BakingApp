package com.sofialopes.android.bakingapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.AmbiguousViewMatcherException;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.sofialopes.android.bakingapp.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static android.support.test.runner.lifecycle.Stage.STARTED;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Sofia on 4/26/2018.
 */
@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {

    public static final String STEPS_LABEL_TEXT = "Steps (10):";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private CountingIdlingResource espressoTestIdlingResource;


    @Before
    public void registerIdlingResource() {
        espressoTestIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(espressoTestIdlingResource);
    }

    @Test
    public void checkingRecyclerComponents(){

        onView(withId(R.id.recipes_recycler))
                .check(matches(atPosition(0, hasDescendant(withText("Nutella Pie")))));

        onView(withId(R.id.recipes_recycler))
                .check(matches(atPosition(1, hasDescendant(withText("Brownies")))));

        onView(withId(R.id.recipes_recycler))
                .check(matches(atPosition(2, hasDescendant(withText("Yellow Cake")))));

        onView(withId(R.id.recipes_recycler))
                .check(matches(atPosition(3, hasDescendant(withText("Cheesecake")))));
    }

    @Test
    public void checkingRecipeDetailsActivity() {

        //Clicking Brownies
        onView(withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        //Displays view ingredients_label
        onView(withId(R.id.ingredients_label))
                .check(matches(isDisplayed()));

        //Displays view recycler_recipe_ingredients
        onView(withId(R.id.recycler_recipe_ingredients))
                .check(matches(isDisplayed()));

        //Displays view steps_label
        onView(withId(R.id.steps_label))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        // Steps label has the correct text
        onView(withId(R.id.steps_label))
                .perform(scrollTo())
                .check(matches(withText(STEPS_LABEL_TEXT)));

        //Displays view recycler_recipe_steps
        onView(withId(R.id.recycler_recipe_steps))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkingStepDetailsActivity() {

        //Clicking Brownies
        onView(ViewMatchers.withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        //Clicking the first position in the recycler view
        onView(withId(R.id.recycler_recipe_steps))
                .perform(scrollTo(), RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @After
    public void unregisterIdlingResource() {
        if (espressoTestIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(espressoTestIdlingResource);
        }
    }

    //Custom Matcher to check if specific position in RecyclerView has the correct text
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}

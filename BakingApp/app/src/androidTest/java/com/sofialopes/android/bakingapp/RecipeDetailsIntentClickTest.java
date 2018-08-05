package com.sofialopes.android.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.sofialopes.android.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_NAME_TESTING;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Sofia on 4/26/2018.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailsIntentClickTest {

    public static final String RECIPE_NAME_RESULT = "Nutella Pie";

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);

    private CountingIdlingResource espressoTestIdlingResource;

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Before
    public void registerIdlingResource() {
        espressoTestIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(espressoTestIdlingResource);
    }

    @Test
    public void RecipeDetailsTest() {

        //Clicking Nutella Pie
        onView(ViewMatchers.withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasComponent(hasShortClassName(".ui.RecipeDetailsActivity")),
                hasExtra(RECIPE_NAME_TESTING, RECIPE_NAME_RESULT)));
    }

    @After
    public void unregisterIdlingResource() {
        if (espressoTestIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(espressoTestIdlingResource);
        }
    }
}

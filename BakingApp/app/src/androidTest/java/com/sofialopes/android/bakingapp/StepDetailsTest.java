package com.sofialopes.android.bakingapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.app.AppCompatActivity;

import com.sofialopes.android.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.TAG_LANDSCAPE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Sofia on 5/4/2018.
 */

@RunWith(AndroidJUnit4.class)
public class StepDetailsTest {

    public static final String DESCRIPTION_TEXT =
            "1. Preheat the oven to 350°F. Grease the bottom of a 9-inch round springform pan with butter. ";

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
    public void checkingTextViewAfterRotation() {

        //Clicking Cheesecake
        onView(ViewMatchers.withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        //Clicking position 1 in the recycler view
        onView(withId(R.id.recycler_recipe_steps))
                .perform(scrollTo(), RecyclerViewActions.actionOnItemAtPosition(1, click()));


        //Rotating the StepDetailsActivity because, if we are using a phone, in portrait mode
        //several fragments are created, due to ViewPager, causing an AmbiguousViewMatcherException
        Activity stepActivity = getActivityInstance();
        stepActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Checking if the video_image_container is displayed
        onView(withId(R.id.video_image_container)).check(matches(isDisplayed()));

        //Checking if the step_description is displayed
        onView(withId(R.id.step_description)).check(matches(isDisplayed()));

        //Checking if the step_description view has the correct text
        onView(withId(R.id.step_description))
                .check(matches(withText(DESCRIPTION_TEXT)));
    }

    @Test
    public void checkingStepDetailsShowVideoPlayer() {

        //Clicking Yellow Cake
        onView(ViewMatchers.withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        //Clicking position 3 in the recycler view
        onView(withId(R.id.recycler_recipe_steps))
                .perform(scrollTo(), RecyclerViewActions.actionOnItemAtPosition(3, click()));


        //Rotating the StepDetailsActivity because, if we are using a phone, in portrait mode
        //several fragments are created, due to ViewPager, causing an AmbiguousViewMatcherException
        AppCompatActivity stepActivity = getActivityInstance();
        stepActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Checking if the step_video is displayed
        onView(allOf(
                withTagValue(is((Object) (TAG_LANDSCAPE))),
                withId(R.id.step_video)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkViewPagerSwipe() {
        //Clicking Yellow Cake
        onView(ViewMatchers.withId(R.id.recipes_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        //Clicking position 3 in the recycler view
        onView(withId(R.id.recycler_recipe_steps))
                .perform(scrollTo(), RecyclerViewActions.actionOnItemAtPosition(3, click()));

        onView(withId(R.id.pager)).perform(swipeLeft());
    }

    //Helper method to get current activity
    public AppCompatActivity getActivityInstance() {
        final AppCompatActivity[] activity = new AppCompatActivity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                AppCompatActivity currentActivity = null;
                Collection resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    currentActivity = (AppCompatActivity) resumedActivities.iterator().next();
                    activity[0] = currentActivity;
                }
            }
        });
        return activity[0];
    }

    @After
    public void unregisterIdlingResource() {
        if (espressoTestIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(espressoTestIdlingResource);
        }
    }
}

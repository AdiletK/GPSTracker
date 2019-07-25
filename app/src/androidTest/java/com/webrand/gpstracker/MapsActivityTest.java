package com.webrand.gpstracker;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(JUnit4.class)
public class MapsActivityTest  {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule=
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickNewTrack() {
        // Click on decrement button
        onView(withId(R.id.btn_new_track))
                .perform(click());

        onView(withId(R.id.btn_start_tracking)).check(matches(isDisplayed()));

    }



    @Test
    public void clickToStartTrack(){
        onView(withId(R.id.btn_new_track))
                .perform(click());


        onView(withId(R.id.btn_start_tracking))
                .perform(click());

        onView(withId(R.id.info_layout)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_start_tracking)).check(matches(withText("STOP")));

    }
}

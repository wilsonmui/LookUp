package edu.ucsb.cs48.lookup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Eliza on 3/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class HomePageTest {

    @Rule
    public ActivityTestRule<HomePageActivity> homePageActivityRule = new ActivityTestRule<HomePageActivity>(HomePageActivity.class);

    @Test
    public void checkForSignOutButton(){
        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
    }

}

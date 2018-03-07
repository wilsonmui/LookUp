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
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eliza on 3/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class HomePageTest {

    @Rule
    public ActivityTestRule<HomePageActivity> HomePageActivityRule =
            new ActivityTestRule<>(HomePageActivity.class);
    @Test
    public void HomePageActivityExists() {
        assertNotNull(HomePageActivityRule);
    }

    @Test
    public void checkForAllComponents(){
        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
        onView(withId(R.id.view_code)).check(matches(isDisplayed()));
        onView(withId(R.id.scan_face_button)).check(matches(isDisplayed()));
        onView(withId(R.id.user_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.contacts_button)).check(matches(isDisplayed()));
        onView(withId(R.id.info_button)).check(matches(isDisplayed()));
        onView(withId(R.id.number_of_requests)).check(matches(isDisplayed()));
        onView(withId(R.id.requests_text)).check(matches(isDisplayed()));
    }

}

package edu.ucsb.cs48.lookup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eliza on 3/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SignOutForTest {

    @Rule
    public ActivityTestRule<HomePageActivity> HomePageActivityRule =
            new ActivityTestRule<>(HomePageActivity.class);

    @Test
    public void HomePageActivityExists() {
        assertNotNull(HomePageActivityRule);
    }

    @Test
    public void signOut() throws IOException{
        onView(withId(R.id.buttonSignOut)).perform(click());
    }
}

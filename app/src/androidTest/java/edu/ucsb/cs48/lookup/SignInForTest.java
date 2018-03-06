package edu.ucsb.cs48.lookup;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;


/**
 * Created by Eliza on 3/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SignInForTest {

    @Rule
    public ActivityTestRule<SignInPageActivity> signInActivityRule = new ActivityTestRule<SignInPageActivity>(SignInPageActivity.class);

    @Test
    public void signInPageActivityExists() {assertNotNull(signInActivityRule);}

    @Test
    public void testSetup() throws IOException {
        String testEmail = "testing@gmail.com";
        String testPassword = "123456";

        onView(withId(R.id.editTextEmail)).perform(replaceText(testEmail));
        onView(withId(R.id.editTextPassword)).perform(replaceText(testPassword));

        onView(withId(R.id.buttonSignIn)).perform(click());
    }

}

package edu.ucsb.cs48.lookup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Eliza on 3/5/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SignInTest {

    // Launch the SignIn page.
    @Rule
    public ActivityTestRule<SignInPageActivity> signInActivityRule = new ActivityTestRule<SignInPageActivity>(SignInPageActivity.class);

    @Test
    public void checkTestInputs(){
        String testEmail = "testing@gmail.com";
        String testPassword = "123456";

        onView(withId(R.id.editTextEmail)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        onView(withId(R.id.editTextEmail)).check(matches(withText(testEmail)));
        onView(withId(R.id.editTextPassword)).check(matches(withText(testPassword)));

    }

    @Test
    public void checkBlankTestInputs(){
        String testEmail = "";
        String testPassword = "";

        onView(withId(R.id.editTextEmail)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        onView(withId(R.id.editTextEmail)).check(matches(withText(testEmail)));
        onView(withId(R.id.editTextPassword)).check(matches(withText(testPassword)));

    }

    // Test authentication
    @Test
    public void testSetup() throws IOException {
        onView(withId(R.id.editTextEmail)).perform(typeText("testUser@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.buttonSignIn)).perform(click());

    }
}

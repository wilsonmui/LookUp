package edu.ucsb.cs48.lookup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eliza on 3/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SignUpTest {

    @Rule
    public ActivityTestRule<SignUpPageActivity> signUpActivityRule = new ActivityTestRule<SignUpPageActivity>(SignUpPageActivity.class);

    @Test
    public void SignUpPageActivityExists() {
        assertNotNull(signUpActivityRule);
    }

    @Test
    public void checkTestInputs(){
        String testName = "testName";
        String testEmail = "testing@gmail.com";
        String testPhone = "012-345-6789";
        String testPassword = "123456";

        onView(withId(R.id.editTextName)).perform(typeText(testName), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPhone)).perform(typeText(testPhone), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        onView(withId(R.id.editTextName)).check(matches(withText(testName)));
        onView(withId(R.id.editTextEmail)).check(matches(withText(testEmail)));
        onView(withId(R.id.editTextPhone)).check(matches(withText(testPhone)));
        onView(withId(R.id.editTextPassword)).check(matches(withText(testPassword)));
    }

    public void checkBlankTestInputs(){
        String testName = "";
        String testEmail = "";
        String testPhone = "";
        String testPassword = "";

        onView(withId(R.id.editTextName)).perform(typeText(testName), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPhone)).perform(typeText(testPhone), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        onView(withId(R.id.editTextName)).check(matches(withText(testName)));
        onView(withId(R.id.editTextEmail)).check(matches(withText(testEmail)));
        onView(withId(R.id.editTextPhone)).check(matches(withText(testPhone)));
        onView(withId(R.id.editTextPassword)).check(matches(withText(testPassword)));
    }

    @Test
    public void testSignUp() throws IOException {
        String testName = "testName";
        String testEmail = "testing@gmail.com";
        String testPhone = "012-345-6789";
        String testPassword = "123456";

        onView(withId(R.id.editTextName)).perform(typeText(testName), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPhone)).perform(typeText(testPhone), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        // Scroll to get the button in view.
        onView(withId(R.id.buttonSignUp)).perform(scrollTo(), click());
    }

}

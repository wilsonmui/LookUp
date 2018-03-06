package edu.ucsb.cs48.lookup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by Eliza on 3/5/2018.
 * Ensures that clicking each button in MainActivity leads the user to the appropriate page.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // Launch the MainActivity page.
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    // Test clicking 'Get Started' leads to Sign Up Page.
    @Test
    public void buttonSignUpActivity_editTextName() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.editTextName)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_editTextEmail() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.editTextEmail)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_editTextPhone() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.editTextPhone)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_buttonSignUp() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.buttonSignUp)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_setPhotoButton() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.set_photo_button)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_googleLogin() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.google_login)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }

    @Test
    public void buttonSignUpActivity_FBLogin() {
        onView(withId(R.id.buttonGetStarted)).perform(click());
        onView(withId(R.id.fb_sign_in_button)).check(matches(allOf(isDescendantOfA(withId(R.id.signUpPage)), isDisplayed())));
    }


    // Test clicking 'Sign In' leads to Sign In Page - check all fields on page.
    @Test
    public void buttonSignInActivity() {
        onView(withId(R.id.buttonSignIn)).perform(click());
        onView(withId(R.id.editTextEmail)).check(matches(allOf(isDescendantOfA(withId(R.id.signInPage)), isDisplayed())));
    }

    @Test
    public void buttonSignInActivity_editTextPassword() {
        onView(withId(R.id.buttonSignIn)).perform(click());
        onView(withId(R.id.editTextPassword)).check(matches(allOf(isDescendantOfA(withId(R.id.signInPage)), isDisplayed())));
    }

    @Test
    public void buttonSignInActivity_buttonSignIn() {
        onView(withId(R.id.buttonSignIn)).perform(click());
        onView(withId(R.id.buttonSignIn)).check(matches(allOf(isDescendantOfA(withId(R.id.signInPage)), isDisplayed())));
    }

    @Test
    public void buttonSignInActivity_googleLogin() {
        onView(withId(R.id.buttonSignIn)).perform(click());
        onView(withId(R.id.google_login)).check(matches(allOf(isDescendantOfA(withId(R.id.signInPage)), isDisplayed())));
    }

    @Test
    public void buttonSignInActivity_FBLogin() {
        onView(withId(R.id.buttonSignIn)).perform(click());
        onView(withId(R.id.fb_sign_in_button)).check(matches(allOf(isDescendantOfA(withId(R.id.signInPage)), isDisplayed())));
    }
}

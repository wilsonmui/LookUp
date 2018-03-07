package edu.ucsb.cs48.lookup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by Eliza on 3/5/2018.
 * Tests methods of the User class.
 */

public class UserTest {

    // Initialize Variables
    private String testName = "Mr. Test";
    private String testEmail = "testEmail@gmail.com";
    private String testPhone = "012-345-6789";
    private String testUID = "test1test2test3";
    private String testFB = "facebook.com/Mr. Test123";
    private String testTwitter = "twitter.com/realmrtest";

    private String dummyName = "Neil Armstrong";
    private String dummyEmail = "ilovethemoon@gmail.com";
    private String dummyPhone = "999-999-9999";
    private String dummyFB = "facebook.com/genericAstronaut";
    private String dummyTwitter = "twitter.com/genericAstronaut";

    private User testUser = new User(testName, testEmail, testPhone, testUID);
    private User secondTestUser = new User(testName, testEmail, testPhone, testUID);
    private User blankUser = new User("", "", "", "");

    ///
    // Test .equals method.
    ///
    @Test
    public void selfIsSameUser() throws Exception {
        assertEquals(true, testUser.equals(testUser));
    }

    @Test
    public void checkIsSameUser() throws Exception {
        assertEquals(true, testUser.equals(secondTestUser));
    }

    @Test
    public void checkIsSameUser2() throws Exception {
        assertEquals(true, secondTestUser.equals(testUser));
    }

    @Test
    public void checkIsNotSameUser_testUser() throws Exception {
        assertEquals(false, testUser.equals(blankUser));
    }

    @Test
    public void checkIsNotSameUser_secondTestUser() throws Exception {
        assertEquals(false, secondTestUser.equals(blankUser));
    }

    @Test
    public void checkIsNotSameUser_randomString() throws Exception {
        assertEquals(false, testUser.equals("The FitnessGram pacer test"));
    }

    ///
    // Test 'get*' methods.
    ///
    @Test
    public void getUserName_testName() throws Exception {
        assertEquals(testName, testUser.getName());
    }

    @Test
    public void getUserName_blank() throws Exception {
        assertEquals("", blankUser.getName());
    }


    @Test
    public void getUserEmail_testEmail() throws Exception {
        assertEquals(testEmail, testUser.getEmail());
    }

    @Test
    public void getUserEmail_blank() throws Exception {
        assertEquals("", blankUser.getEmail());
    }


    @Test
    public void getUserPhone_testPhone() throws Exception {
        assertEquals(testPhone, testUser.getPhone());
    }

    @Test
    public void getUserPhone_blank() throws Exception {
        assertEquals("", blankUser.getPhone());
    }


    @Test
    public void getUserUID_testUID() throws Exception {
        assertEquals(testUID, testUser.getUid());
    }

    @Test
    public void getUserUID_blank() throws Exception {
        assertEquals("", blankUser.getUid());
    }


    @Test
    public void getBlankUserFacebook_testUser() throws Exception {
        assertEquals("", testUser.getFacebookURL());
    }

    @Test
    public void getBlankUserFacebook_blankUser() throws Exception {
        assertEquals("", blankUser.getFacebookURL());
    }


    @Test
    public void getBlankUserTwitter_testUser() throws Exception {
        assertEquals("", testUser.getTwitterURL());
    }

    @Test
    public void getBlankUserTwitter_blankUser() throws Exception {
        assertEquals("", blankUser.getTwitterURL());
    }

    ///
    // Test 'set*' methods.
    ///

    // Name
    @Test
    public void setNameOfTestUser_dummyName() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setName(dummyName);
        assertEquals(dummyName, dummyUser.getName());
    }

    @Test
    public void setNameOfTestUser_testName() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setName(testName);
        assertEquals(testName, dummyUser.getName());
    }

    @Test
    public void setNameOfBlankUser_dummyName() throws Exception {
        User dummyUser = new User("", "", "", "");
        dummyUser.setName(dummyName);
        assertEquals(dummyName, dummyUser.getName());
    }

    @Test
    public void setNameOfTestUser_blank() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setName("");
        assertEquals("", dummyUser.getName());
    }

    // Email
    @Test
    public void setEmailOfTestUser_dummyEmail() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setEmail(dummyEmail);
        assertEquals(dummyEmail, dummyUser.getEmail());
    }

    @Test
    public void setEmailOfTestUser_testEmail() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setEmail(testEmail);
        assertEquals(testEmail, dummyUser.getEmail());
    }

    @Test
    public void setEmailOfBlankUser_dummyEmail() throws Exception {
        User dummyUser = new User("", "", "", "");
        dummyUser.setEmail(dummyEmail);
        assertEquals(dummyEmail, dummyUser.getEmail());
    }

    @Test
    public void setEmailOfTestUser_blank() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setEmail("");
        assertEquals("", dummyUser.getEmail());
    }


    // Phone
    @Test
    public void setPhoneOfTestUser_dummyPhone() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setPhone(dummyPhone);
        assertEquals(dummyPhone, dummyUser.getPhone());
    }

    @Test
    public void setPhoneOfTestUser_testPhone() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setPhone(testPhone);
        assertEquals(testPhone, dummyUser.getPhone());
    }

    @Test
    public void setPhoneOfBlankUser_dummyPhone() throws Exception {
        User dummyUser = new User("", "", "", "");
        dummyUser.setPhone(dummyPhone);
        assertEquals(dummyPhone, dummyUser.getPhone());
    }

    @Test
    public void setPhoneOfTestUser_blank() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setPhone("");
        assertEquals("", dummyUser.getPhone());
    }


    // Facebook
    @Test
    public void setFBOfTestUser_dummyFB() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setFacebookURL(dummyFB);
        assertEquals(dummyFB, dummyUser.getFacebookURL());
    }

    @Test
    public void setFBOfTestUser_testFB() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setFacebookURL(testFB);
        assertEquals(testFB, dummyUser.getFacebookURL());
    }

    @Test
    public void setFBOfBlankUser_dummyFB() throws Exception {
        User dummyUser = new User("", "", "", "");
        dummyUser.setFacebookURL(dummyFB);
        assertEquals(dummyFB, dummyUser.getFacebookURL());
    }

    @Test
    public void setFBOfTestUser_blank() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setFacebookURL("");
        assertEquals("", dummyUser.getFacebookURL());
    }


    // Twitter
    @Test
    public void setTWOfTestUser_dummyTwitter() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setTwitterURL(dummyTwitter);
        assertEquals(dummyTwitter, dummyUser.getTwitterURL());
    }

    @Test
    public void setTWOfTestUser_testTwitter() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setTwitterURL(testTwitter);
        assertEquals(testTwitter, dummyUser.getTwitterURL());
    }

    @Test
    public void setTWOfBlankUser_dummyTwitter() throws Exception {
        User dummyUser = new User("", "", "", "");
        dummyUser.setTwitterURL(dummyTwitter);
        assertEquals(dummyTwitter, dummyUser.getTwitterURL());
    }

    @Test
    public void setTWOfTestUser_blank() throws Exception {
        User dummyUser = new User(testName, testEmail, testPhone, testUID);
        dummyUser.setTwitterURL("");
        assertEquals("", dummyUser.getTwitterURL());
    }
}

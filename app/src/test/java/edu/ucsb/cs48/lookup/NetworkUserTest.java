package edu.ucsb.cs48.lookup;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Eliza on 3/6/2018.
 */

public class NetworkUserTest {
    String testID = "houston we've had a problem";
    String testID2 = "say again please";
    String testID3 = "we have a problem";

    private NetworkUser testUser = new NetworkUser(testID);
    private NetworkUser sameUser = new NetworkUser(testID);
    private NetworkUser blankUser = new NetworkUser("");


    @Test
    public void selfIsSameUser() throws Exception {
        assertEquals(true, testUser.equals(testUser));
    }

    @Test
    public void isSameUser_sameUser() throws Exception {
        assertEquals(true, testUser.equals(sameUser));
    }

    @Test
    public void isSameUser_testUser() throws Exception {
        assertEquals(true, sameUser.equals(testUser));
    }


    @Test
    public void checkIsNotSameUser_testUser() throws Exception {
        assertEquals(false, testUser.equals(blankUser));
    }

    @Test
    public void checkIsNotSameUser_blankUser() throws Exception {
        assertEquals(false, blankUser.equals(testUser));
    }

    @Test
    public void getUID_testUser() throws Exception {
        assertEquals(testID, testUser.getUid());
    }

    @Test
    public void getUID_blankUser() throws Exception {
        assertEquals("", blankUser.getUid());
    }

    @Test
    public void setUID_testID2() throws Exception {
        NetworkUser emptyUser = new NetworkUser("");
        emptyUser.setUid(testID2);
        assertEquals(testID2, emptyUser.getUid());
    }

    @Test
    public void setUID_testID3() throws Exception {
        NetworkUser emptyUser = new NetworkUser("");
        emptyUser.setUid(testID3);
        assertEquals(testID3, emptyUser.getUid());
    }

    @Test
    public void setUID_testIDBlank() throws Exception {
        NetworkUser emptyUser = new NetworkUser(testID);
        emptyUser.setUid("");
        assertEquals("", emptyUser.getUid());
    }
}

package edu.ucsb.cs48.lookup;

/**
 * Created by esuarez on 2/25/18.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitterOauthResult {

    @SerializedName("oauth_token")
    @Expose
    private String oauthToken;
    @SerializedName("oauth_token_secret")
    @Expose
    private String oauthTokenSecret;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("screen_name")
    @Expose
    private String screenName;
    @SerializedName("x_auth_expires")
    @Expose
    private String xAuthExpires;

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getXAuthExpires() {
        return xAuthExpires;
    }

    public void setXAuthExpires(String xAuthExpires) {
        this.xAuthExpires = xAuthExpires;
    }
}

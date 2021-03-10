package vn.icheck.android.network.models;

import com.google.gson.annotations.SerializedName;

public class ICLoginFacebook {
    @SerializedName("facebook_token")
    private String facebookToken;
    @SerializedName("accountkit_code")
    private String accountkitCode;
    @SerializedName("password")
    private String password;

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getAccountkitCode() {
        return accountkitCode;
    }

    public void setAccountkitCode(String accountkitCode) {
        this.accountkitCode = accountkitCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package vn.icheck.android.network.models;

import com.google.gson.annotations.SerializedName;

public class ICResetPassword {
    @SerializedName("code")
    private String code;
    @SerializedName("password")
    private String password;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

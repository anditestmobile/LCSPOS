package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;


public class UserLogin {
    @SerializedName("UserName")
    private String userName;
    @SerializedName("Password")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

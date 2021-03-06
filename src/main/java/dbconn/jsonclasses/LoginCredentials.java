package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class LoginCredentials {
    @JsonRequired
    @SerializedName("email")
    private String email;

    @JsonRequired
    @SerializedName("password")
    private String password;

    @JsonRequired
    @SerializedName("is_mobile")
    private boolean isMobile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isMobile()
    {
        return isMobile;
    }

    public void setMobile(boolean isMobile)
    {
        this.isMobile = isMobile;
    }

}

package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class RegisterCredentials {
    
    @JsonRequired
    @SerializedName("email")
    private String email;

    @JsonRequired
    @SerializedName("password")
    private String password;

    @JsonRequired
    @SerializedName("nickname")
    private String nickname;

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

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

}

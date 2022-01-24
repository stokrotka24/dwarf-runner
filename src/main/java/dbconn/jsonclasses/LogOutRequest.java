package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;
public class LogOutRequest {
    @JsonRequired
    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

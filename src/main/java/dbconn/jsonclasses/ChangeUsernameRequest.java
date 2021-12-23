package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class ChangeUsernameRequest
{
    @JsonRequired
    @SerializedName("email")
    private String email;

    @JsonRequired
    @SerializedName("new_username")
    private String username;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getNewUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}

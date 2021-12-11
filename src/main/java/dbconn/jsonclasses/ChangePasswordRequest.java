package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class ChangePasswordRequest
{
    @JsonRequired
    @SerializedName("email")
    private String email;

    @JsonRequired
    @SerializedName("current_password")
    private String currentPassword;

    @JsonRequired
    @SerializedName("new_password")
    private String newPassword;
    
    @JsonRequired
    @SerializedName("new_password_confirm")
    private String newPasswordConfirm;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword)
    {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm()
    {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm)
    {
        this.newPasswordConfirm = newPasswordConfirm;
    }
}

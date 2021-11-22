package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

public class LoginResponseData {

    @SerializedName("status")
    private int loginStatus;

    @SerializedName("user_nickname")
    private String userNickname;
    
    @SerializedName("failure_reason")
    private String failureReason;
    
    public LoginResponseData(int loginStatus, String userNickname, String failureReason) {
        this.loginStatus = loginStatus;
        this.userNickname = userNickname;
        this.failureReason = failureReason;
    }
    
    static public LoginResponseData failedLoginData(String failureReason) {
        return new LoginResponseData(0, null, failureReason);
    }
    static public LoginResponseData successLoginData(String nickname) {
        return new LoginResponseData(1, nickname, null);
        
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    
}

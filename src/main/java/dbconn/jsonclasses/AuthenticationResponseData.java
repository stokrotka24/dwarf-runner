package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class AuthenticationResponseData {

    @JsonRequired
    @SerializedName("status")
    private int loginStatus;

    @SerializedName("user_nickname")
    private String userNickname;
    
    @SerializedName("failure_reason")
    private String failureReason;
    
    public AuthenticationResponseData(int loginStatus, String userNickname, String failureReason) {
        this.loginStatus = loginStatus;
        this.userNickname = userNickname;
        this.failureReason = failureReason;
    }

    public AuthenticationResponseData(int loginStatus, String failureReason) {
        this.loginStatus = loginStatus;
        this.failureReason = failureReason;
    }
    
    static public AuthenticationResponseData failedAuthenticationData(String failureReason) {
        return new AuthenticationResponseData(0, failureReason);
    }
    
    static public AuthenticationResponseData successAuthenticationData(String nickname) {
        return new AuthenticationResponseData(1, nickname, null);   
    }

    static public AuthenticationResponseData successAuthenticationData() {
        return new AuthenticationResponseData(1, null);
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

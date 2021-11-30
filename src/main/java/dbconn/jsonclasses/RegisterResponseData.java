package dbconn.jsonclasses;

import com.google.gson.annotations.SerializedName;

import messages.JsonRequired;

public class RegisterResponseData {
    
    @JsonRequired
    @SerializedName("status")
    private int loginStatus;
    
    @SerializedName("failure_reason")
    private String failureReason;
    
    public RegisterResponseData(int loginStatus, String failureReason) {
        this.loginStatus = loginStatus;
        this.failureReason = failureReason;
    }
    
    static public RegisterResponseData failedRegisterData(String failureReason) {
        return new RegisterResponseData(0, failureReason);
    }
    
    static public RegisterResponseData successRegisterData() {
        return new RegisterResponseData(1, null);
        
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}

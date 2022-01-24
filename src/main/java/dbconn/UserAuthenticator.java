package dbconn;

import dbconn.jsonclasses.LoginCredentials;
import dbconn.jsonclasses.AuthenticationResponseData;
import dbconn.jsonclasses.ChangePasswordRequest;
import dbconn.jsonclasses.ChangeUsernameRequest;
import dbconn.jsonclasses.LogOutRequest;
import dbconn.jsonclasses.RegisterCredentials;
import game.GamePlatform;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import server.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Base64;

public class UserAuthenticator {
    
    private static final String loginQuery = "{call LoginIn(?, ?, ?)}";
    private static final String loginStatusOn = "{call Log_in(?)}";
    private static final String loginCheck = "{?= call Is_logged(?)}";
    private static final String loginStatusOff = "{call Log_out(?)}";
    private static final String registerQuery = "{call Register(?, ?, ?, ?)}";
    private static final String changePasswordQuery = "{call Change_Pass(?, ?, ?, ?)}";
    private static final String changeUsernameQuery = "{call Change_Nick(?, ?, ?, ?)}";
    private static final int USER_ALREADY_EXISTS_CODE = 15600;
    private static final int CHANGE_PASSWORD_OK = 0;
    private static final int CHANGE_PASSWORD_WRONG_CREDENTIALS = 100;
    private static final int CHANGE_USERNAME_OK = 0;
    private static final int CHANGE_USERNAME_NULL = 10;
    private static final int CHANGE_USERNAME_WRONG_CREDENTIALS = 100;
    private static final int CHANGE_USERNAME_TAKEN = 1000;
    private static final Logger logger = Logger.getInstance();

    public static void handleChangeUsernameRequest(Message<ChangeUsernameRequest> msg, User creator) {
        ChangeUsernameRequest request = msg.content;
        if (request == null || request.getEmail() == null || request.getNewUsername() == null) {
            sendChangeUsernameFailureResponse("DATA_LOST", creator);
        }
        
        try {
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(changeUsernameQuery);
            cStatement.setString(1, request.getEmail());
            cStatement.setString(2, creator.getUsername());
            cStatement.setString(3, request.getNewUsername());
            cStatement.registerOutParameter(4, java.sql.Types.INTEGER);
            cStatement.execute();

            int errorCode = cStatement.getInt(4);
            logger.info("Change username result: " + errorCode);
            
            if (errorCode == CHANGE_USERNAME_OK) {
                creator.setUsername(request.getNewUsername());
                sendChangeUsernameSuccessResponse(creator);
            }
            else if (errorCode == CHANGE_USERNAME_NULL) {
                sendChangeUsernameFailureResponse("GOT_NULL", creator);
            }
            else if (errorCode == CHANGE_USERNAME_WRONG_CREDENTIALS) {
                sendChangeUsernameFailureResponse("WRONG_CREDENTIALS", creator);
            }
            else if (errorCode == CHANGE_USERNAME_TAKEN) {
                sendChangeUsernameFailureResponse("NICKNAME_TAKEN", creator);
            }
            else {
                sendChangeUsernameFailureResponse("UNKNOWN", creator);
            }
        }
        catch (SQLException ex) {
            logger.error("Exception caught. Change username procedure error - reason UNKNOWN");
            logger.error(ex.getMessage());
            sendChangeUsernameFailureResponse("UNKNOWN", creator);
        }
    }

    public static void handleChangePasswordRequest(Message<ChangePasswordRequest> msg, User creator) {
        ChangePasswordRequest request = msg.content;
        if (request == null || request.getEmail() == null || request.getCurrentPassword() == null ||
                request.getNewPassword() == null || request.getNewPasswordConfirm() == null) {
            sendChangePasswordFailureResponse("DATA_LOST", creator);
        }
        
        try {
            if (request.getNewPassword().equals(request.getNewPasswordConfirm())) {
                CallableStatement cStatement = DBConnection.getConnection().prepareCall(changePasswordQuery);
                cStatement.setString(1, request.getEmail());
                cStatement.setString(2, hash256(request.getCurrentPassword()));
                cStatement.setString(3, hash256(request.getNewPassword()));
                cStatement.registerOutParameter(4, java.sql.Types.INTEGER);
                cStatement.execute();
                int errorCode = cStatement.getInt(4);
                logger.info("Change password result: " + errorCode);
                
                if (errorCode == CHANGE_PASSWORD_OK) {
                    logger.info("Change password procedure was successful");
                    sendChangePasswordSuccessResponse(creator);
                }
                else if (errorCode == CHANGE_PASSWORD_WRONG_CREDENTIALS) {
                    sendChangePasswordFailureResponse("WRONG_CREDENTIALS", creator);
                }
                else {
                    sendChangePasswordFailureResponse("UNKNOWN", creator);
                }
            }
            else {
                sendChangePasswordFailureResponse("DIFFERENT_NEW_PASSWORDS", creator);
            }
        }
        catch (SQLException ex) {
            logger.error("Exception caught. Change password procedure error - reason UNKNOWN");
            logger.error(ex.getMessage());
            sendChangePasswordFailureResponse("UNKNOWN", creator);
        }
    }

    public static void handleRegisterRequest(Message<RegisterCredentials> msg, User creator) {
        RegisterCredentials credentials = msg.content;
        if (credentials == null || credentials.getEmail() == null || credentials.getNickname() == null ||
                credentials.getPassword() == null) {
            sendRegisterFailureResponse("DATA_LOST", creator);
        }
        
        try {
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(registerQuery);
            cStatement.setString(1, credentials.getEmail());
            cStatement.setString(2, credentials.getNickname());
            cStatement.setString(3, hash256(credentials.getPassword()));
            cStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
            cStatement.execute();
            sendRegisterSuccessResponse(creator);
        }
        catch (SQLException ex) {
            if (ex.getErrorCode() == USER_ALREADY_EXISTS_CODE) {
                sendRegisterFailureResponse("EMAIL_TAKEN", creator);
            }
            else {
                logger.warning(ex.getMessage());
                sendRegisterFailureResponse("UNKNOWN", creator);
            }
        }
    }
    
    /**
     * function to handle JSON login request from client
     * @param msg received JSON with login request
     * @param creator user that sent login request
     */
    public static void handleLoginRequest(Message<LoginCredentials> msg, User creator) {
        LoginCredentials credentials = msg.content;

        if (credentials == null || credentials.getEmail() == null || credentials.getPassword() == null 
                || credentials.getEmail().isEmpty() || credentials.getPassword().isEmpty()) {
            sendLoginFailureResponse("DATA_LOST", creator);
            return;
        }

        try {
            CallableStatement loginCheck = DBConnection.getConnection().prepareCall(UserAuthenticator.loginCheck);
            loginCheck.setString(2, credentials.getEmail());
            loginCheck.registerOutParameter(1, java.sql.Types.INTEGER);
            loginCheck.execute();
            Integer isLogged = loginCheck.getInt(1);
            if (isLogged == 1) {
                sendLoginFailureResponse("ALREADY_LOGGED_IN", creator);
                return;
            }

            CallableStatement cStatement = DBConnection.getConnection().prepareCall(loginQuery);
            cStatement.setString(1, credentials.getEmail());
            cStatement.setString(2, hash256(credentials.getPassword()));
            cStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
            cStatement.execute();
            String userNickname = cStatement.getString(3);
            if (userNickname != null && !userNickname.isEmpty()) {
                creator.setPlatform(credentials.isMobile() ? GamePlatform.MOBILE : GamePlatform.WEB);
                creator.setEmail(credentials.getEmail());
                sendLoginSuccessResponse(userNickname, creator);

                CallableStatement logStatement = DBConnection.getConnection().prepareCall(loginStatusOn);
                logStatement.setString(1, credentials.getEmail());
                logStatement.execute();
                return;
            }
            else {
                sendLoginFailureResponse("WRONG_CREDENTIALS", creator);
            }
            return;
        }
        catch (SQLException ex) {
            logger.warning(ex.getMessage());
        }
        sendLoginFailureResponse("UNKNOWN", creator);
    }
    
    private static String hash256(String toHash) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } 
        catch (NoSuchAlgorithmException e) {
            logger.warning(e.getMessage());
            return toHash;
        }
    }

    private static void sendRegisterFailureResponse(String failureReason, User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.failedAuthenticationData(failureReason);
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.REGISTER_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendRegisterSuccessResponse(User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.successAuthenticationData();
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.REGISTER_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendLoginFailureResponse(String failureReason, User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.failedAuthenticationData(failureReason);
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendLoginSuccessResponse(String nickname, User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.successAuthenticationData(nickname);
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        creator.setUsername(nickname);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendChangePasswordFailureResponse(String failureReason, User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.failedAuthenticationData(failureReason);
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.CHANGE_PASSWORD_RESPONSE
                , responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendChangePasswordSuccessResponse(User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.successAuthenticationData();
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.CHANGE_PASSWORD_RESPONSE
                , responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendChangeUsernameFailureResponse(String failureReason, User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.failedAuthenticationData(failureReason);
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.CHANGE_USERNAME_RESPONSE
                , responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendChangeUsernameSuccessResponse(User creator) {
        AuthenticationResponseData responseData = AuthenticationResponseData.successAuthenticationData();
        Message<AuthenticationResponseData> respMsg = new Message<>(MessageType.CHANGE_USERNAME_RESPONSE
                , responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    /**
     * 
     * only called by internal DISCONNECT
     */
    public static void handleLogOutRequest(User sender) {
        CallableStatement logStatement;
        try {
            logStatement = DBConnection.getConnection().prepareCall(loginStatusOff);
            logStatement.setString(1, sender.getEmail());
            logStatement.execute();
        } catch (SQLException e) {
            logger.error("Exception caught. Logout procedure error - reason UNKNOWN");
            logger.error(e.getMessage());
        }
    }

    public static void handleLogOutRequest(Message<LogOutRequest> msg, User sender) {
        LogOutRequest request = msg.content;
        if (request == null || request.getEmail() == null || request.getEmail() != sender.getEmail()) {
            sendLoginFailureResponse("DATA_LOST", sender);
            return;
        }
        CallableStatement logStatement;
        try {
            logStatement = DBConnection.getConnection().prepareCall(loginStatusOff);
            logStatement.setString(1, sender.getEmail());
            logStatement.execute();
        } catch (SQLException e) {
            sendLoginFailureResponse("DATABASE_ERROR", sender);
            logger.error("Exception caught. Logout procedure error - reason UNKNOWN");
            logger.error(e.getMessage());
        }
    }
}

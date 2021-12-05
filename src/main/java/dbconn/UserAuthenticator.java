package dbconn;

import dbconn.jsonclasses.LoginCredentials;
import dbconn.jsonclasses.LoginResponseData;
import dbconn.jsonclasses.RegisterCredentials;
import dbconn.jsonclasses.RegisterResponseData;
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
    private static final String registerQuery = "{call Register(?, ?, ?, ?)}";
    private static final int USER_ALREADY_EXISTS_CODE = 15600;
    private static final Logger logger = Logger.getInstance();

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
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(loginQuery);
            cStatement.setString(1, credentials.getEmail());
            cStatement.setString(2, hash256(credentials.getPassword()));
            cStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
            cStatement.execute();
            String userNickname = cStatement.getString(3);
            if (userNickname != null && !userNickname.isEmpty()) {
                creator.setPlatform(credentials.isMobile() ? GamePlatform.MOBILE : GamePlatform.WEB);
                sendLoginSuccessResponse(userNickname, creator);
                return;
            }
            else {
                sendLoginFailureResponse("WRONG_CREDENTIALS", creator);
                return;
            }
        }
        catch (SQLException ex) {
            logger.warning(ex.getMessage());
        }
        sendLoginFailureResponse("UNKNOWN", creator);
        return;
    }
    
    private static String hash256(String toHash)
    {
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
        RegisterResponseData responseData = RegisterResponseData.failedRegisterData(failureReason);
        Message<RegisterResponseData> respMsg = new Message<>(MessageType.REGISTER_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendRegisterSuccessResponse(User creator) {
        RegisterResponseData responseData = RegisterResponseData.successRegisterData();
        Message<RegisterResponseData> respMsg = new Message<>(MessageType.REGISTER_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendLoginFailureResponse(String failureReason, User creator) {
        LoginResponseData responseData = LoginResponseData.failedLoginData(failureReason);
        Message<LoginResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendLoginSuccessResponse(String nickname, User creator) {
        LoginResponseData responseData = LoginResponseData.successLoginData(nickname);
        Message<LoginResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        creator.setUsername(nickname);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }
    
}

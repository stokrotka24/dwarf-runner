package dbconn;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Base64;

import dbconn.jsonclasses.LoginCredentials;
import dbconn.jsonclasses.LoginResponseData;
import game.GamePlatform;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

public class UserAuthenticator {
    
    private static final String loginQuery = "{call LoginIn(?, ?, ?)}";
    
    /**
     * function to handle JSON login request from client
     * @param msg received JSON with login request
     * @param creator user that sent login request
     */
    public static void handleLoginRequest(Message<LoginCredentials> msg, User creator) {
        LoginCredentials credentials = msg.content;

        if (credentials.getEmail().isEmpty() || credentials.getPassword().isEmpty()) {
            sendFailureResponse("DATA_LOST", creator);
            return;
        }

        try {
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(loginQuery);
            cStatement.setString(1, credentials.getEmail());
            cStatement.setString(2, hash256(credentials.getPassword()));
            cStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
            cStatement.execute();
            String userNickname = cStatement.getString(3);
            if (!userNickname.isEmpty()) {
                creator.setPlatform(credentials.isMobile() ? GamePlatform.MOBILE : GamePlatform.WEB);
                sendSuccessResponse(userNickname, creator);
                return;
            }
            else {
                sendFailureResponse("WRONG_CREDENTIALS", creator);
                return;
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        sendFailureResponse("UNKNOWN", creator);
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
            e.printStackTrace();
            return toHash;
        }
    }

    private static void sendFailureResponse(String failureReason, User creator) {
        LoginResponseData responseData = LoginResponseData.failedLoginData(failureReason);
        Message<LoginResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }

    private static void sendSuccessResponse(String nickname, User creator) {
        LoginResponseData responseData = LoginResponseData.successLoginData(nickname);
        Message<LoginResponseData> respMsg = new Message<>(MessageType.LOG_IN_RESPONSE, responseData);
        System.out.println(MessageParser.toJsonString(respMsg));
        creator.sendMessage(MessageParser.toJsonString(respMsg));
    }
    
}

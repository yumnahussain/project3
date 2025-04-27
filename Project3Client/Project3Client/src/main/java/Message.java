import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Message implements Serializable {
    private static final long serialVersionUID = 42L;

    public enum MessageType {
        TEXT,
        NEWUSER,
        DISCONNECT,
        UPDATEUSERS,
        SIGNUP,
        SIGNUP_RESPONSE,
        LOGIN,
        LOGIN_RESPONSE,
        GAME_START,
        GAME_UPDATE,
        GAME_OVER,
        GAME_MOVE,
        REQUEST_GAME,
        INVALID_MOVE,
        REQUEST_AI_GAME
    }

    public MessageType type;
    public String username;
    public String password;
    public String recipientUsername;
    public String message;
    public List<String> onlineUsers;
    public int column;
    public int playerNumber;
    public int[][] gameBoard;

    public Message(MessageType type, String username, String password) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.onlineUsers = new ArrayList<>();
    }

    public Message(MessageType type, Object content) {
        this.type = type;
        this.onlineUsers = new ArrayList<>();
        if (content instanceof String) {
            String strContent = (String) content;
            if (type == MessageType.SIGNUP_RESPONSE || type == MessageType.LOGIN_RESPONSE) {
                this.message = strContent;
            } else if (type == MessageType.NEWUSER || type == MessageType.DISCONNECT) {
                this.username = strContent;
            }
        } else if (content instanceof Integer) {
            this.playerNumber = (Integer) content;
        }
    }

    public Message(String fromUser, String toUser, String messageBody) {
        this.type = MessageType.TEXT;
        this.username = fromUser;
        this.recipientUsername = toUser;
        this.message = messageBody;
        this.onlineUsers = new ArrayList<>();
    }

    public Message(List<String> onlineUsers) {
        this.type = MessageType.UPDATEUSERS;
        this.onlineUsers = onlineUsers;
    }

    public Message(String mess){
        this.type = MessageType.TEXT;
        this.message = mess;
    }

    public Message(MessageType type, int[][] gameBoard, int currentPlayer) {
        this.type = type;
        this.gameBoard = gameBoard;
        this.playerNumber = currentPlayer;
        this.onlineUsers = new ArrayList<>();
    }

    public Message(MessageType type, int column) {
        this.type = type;
        this.column = column;
//        this.onlineUsers = new ArrayList<>();
    }

    public Message(MessageType type, String username, String message, String recipientUsername) {
        this.type = type;
        this.username = username;
        this.message = message;
        this.recipientUsername = recipientUsername;
    }

    public Message(MessageType type, String username, int column) {
        this.type = type;
        this.username = username;
        this.column = column;
    }

    public Message(MessageType type) {
        this.type = type;
        this.onlineUsers = new ArrayList<>();
    }

//    public Message(MessageType type, int column) {
//        this.type = type;
//        this.column = column;
//    }

}
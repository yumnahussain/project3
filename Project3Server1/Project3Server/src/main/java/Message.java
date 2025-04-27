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
        MOVE,
        START,
        ERROR,
        WIN,
        DRAW,
        UPDATE
    }

    public MessageType type;
    public String username;
    public String password;
    public String recipientUsername;
    public String message;
    public List<String> onlineUsers;
    public int column;
    public int row;


    public Message(MessageType type, String username, String password) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.onlineUsers = new ArrayList<>();
    }

    public Message(MessageType type, String content) {
        this.type = type;
        this.onlineUsers = new ArrayList<>();
        if (type == MessageType.SIGNUP_RESPONSE || type == MessageType.LOGIN_RESPONSE) {
            this.message = content;
        } else if (type == MessageType.NEWUSER || type == MessageType.DISCONNECT) {
            this.username = content;
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

    public Message(MessageType type, int column, int row) {
        this.type = type;
        this.column = column;
        this.row = row;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return username;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getRecipient() {return recipientUsername;}
}
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
        LOGIN_RESPONSE
    }

    public MessageType type;
    public String username;
    public String password;
    public String recipientUsername;
    public String message;
    public List<String> onlineUsers;

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
}
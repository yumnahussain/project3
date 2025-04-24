import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    enum MessageType {
        TEXT, NEWUSER, DISCONNECT, UPDATEUSERS;
    }
    MessageType type;
    String message;
    int recipient;
    ArrayList<Integer> clients;

    public Message(int i, boolean connect){
        if(connect) {
            type = MessageType.NEWUSER;
            message = "User "+i+" has joined!";
            recipient = i;
        } else {
            type = MessageType.DISCONNECT;
            message = "User "+i+" has disconnected!";
            recipient = i;
        }
    }

    public Message(String mess){
        type = MessageType.TEXT;
        message = mess;
        recipient = -1;
        clients = new ArrayList<Integer>();
    }

    public Message(int rec, String mess){
        type = MessageType.TEXT;
        message = mess;
        recipient = rec;
        clients.add(1);
    }
}
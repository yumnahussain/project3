import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket clientSocket;
    private Server server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String clientName;

    public ClientThread(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating streams: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                Message message = (Message) input.readObject();

                switch (message.getType()) {
                    case TEXT:
                        server.broadcast(message, this);
                        break;
                    case NEWUSER:
                        clientName = message.getSender();
                        server.broadcast(message, this);
                        break;
                    case MOVE:
                        server.handleGameMove(message, this);
                        break;
                    case DISCONNECT:
                        server.broadcast(message, this);
                        server.removeClient(this);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error in client thread: " + e.getMessage());
            server.removeClient(this);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientName() {
        return clientName;
    }
}
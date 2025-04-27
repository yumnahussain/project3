import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	private Consumer<Message> callback;

	Client(Consumer<Message> call){

		callback = call;
	}

	public void run() {

		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		while(true) {

			try {
				Message message = (Message) in.readObject();
				handleMessage(message);
			}
			catch(Exception e) {}
		}

	}

	private void handleMessage(Message message) {
		switch (message.type) {
			case GAME_UPDATE:
				// Update the game board with the new state
//				if (message.gameBoard != null) {
					callback.accept(message);
//				}
				break;
			default:
				callback.accept(message);
		}
	}

	public void send(Message data) {

		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}

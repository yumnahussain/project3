import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

//import Project

public class GameMaster {
    private final List<GameSession> activeGames    = new ArrayList<>();
    private final Queue<ClientThread> waitingQueue = new LinkedList<>();

    /** Call when a new client connects and wants a game. */
    public synchronized void addWaitingPlayer(ClientThread newPlayer) {
        waitingQueue.add(newPlayer);
        pairPlayers();
    }

    private void pairPlayers() {
        while (waitingQueue.size() >= 2) {
            ClientThread p1 = waitingQueue.poll();
            ClientThread p2 = waitingQueue.poll();
            GameSession session = new GameSession(p1, p2);
            activeGames.add(session);

            // notify each client that the game is starting
            p1.setSession(session);
            p2.setSession(session);
            p1.sendStart(1);
            p2.sendStart(2);
        }
    }
}
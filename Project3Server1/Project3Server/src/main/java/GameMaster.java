import java.util.ArrayList;
import java.util.List;

public class GameMaster {
    private List<GameSession> gameSessions;
    private List<ClientThread> waitingPlayers;

    public GameMaster() {
        gameSessions = new ArrayList<>();
        waitingPlayers = new ArrayList<>();
    }

    public void addPlayer(ClientThread player) {
        if (waitingPlayers.isEmpty()) {
            waitingPlayers.add(player);
        } else {
            ClientThread opponent = waitingPlayers.remove(0);
            GameSession newSession = new GameSession(player, opponent);
            gameSessions.add(newSession);
        }
    }

    public void addGameSession(GameSession session) {
        gameSessions.add(session);
    }

    public GameSession getGameSession(ClientThread player) {
        for (GameSession session : gameSessions) {
            if (session.hasPlayer(player)) {
                return session;
            }
        }
        return null;
    }

    public void removeGameSession(GameSession session) {
        gameSessions.remove(session);
    }
}
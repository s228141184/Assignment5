import java.util.List;

class GameSession extends Thread {
    private final List<ClientHandler> players;
    private int currentPlayerIndex = 0;

    public GameSession(List<ClientHandler> players) {
        this.players = players;
    }

    @Override
    public void run() {
        broadcast("GAME_START|" + players.size() + " players connected.");

        boolean gameOver = false;
        while (!gameOver) {
            ClientHandler currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.send("YOUR_TURN");

            String command = currentPlayer.waitForCommand();
            if (command.startsWith("ROLL")) {
                int dice1 = (int) (Math.random() * 6) + 1;
                int dice2 = (int) (Math.random() * 6) + 1;
                broadcast("DICE_ROLLED|" + dice1 + "|" + dice2);

                // Simulate token placement
                broadcast("MOVE_MADE|" + currentPlayer.getId() + "|" + dice1 + "," + dice2);
            }

            // Rotate turns
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        broadcast("GAME_OVER|Player " + players.get(0).getId() + " wins!");
    }

    private void broadcast(String msg) {
        for (ClientHandler player : players) {
            player.send(msg);
        }
    }
}

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient {

    private static final String HOST = "localhost";  // or server IP (e.g., "192.168.1.10")
    private static final int PORT = 5008;
    private static volatile boolean isMyTurn = false;
    private static PrintWriter out;

    public static void main(String[] args) {
        System.out.println("🎲 Connecting to Sequence-Dice Server...");

        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("✅ Connected to server at " + HOST + ":" + PORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread for listening to server messages
            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        handleServerMessage(msg);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Connection lost: " + e.getMessage());
                }
            });
            listener.setDaemon(true);
            listener.start();

            // Console input loop for commands
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (isMyTurn) {
                    System.out.print("🎯 Your turn! Type 'roll' to roll the dice: ");
                    String command = scanner.nextLine().trim().toUpperCase();

                    if (command.equals("ROLL")) {
                        out.println("ROLL");
                        isMyTurn = false;
                    } else {
                        System.out.println("⚠️ Invalid input. Type 'ROLL' only.");
                    }
                } else {
                    // Not your turn — wait for updates
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {}
                }
            }

        } catch (IOException e) {
            System.out.println("🚫 Could not connect to server: " + e.getMessage());
        }
    }

    private static void handleServerMessage(String msg) {
        String[] parts = msg.split("\\|");
        String type = parts[0];

        switch (type) {
            case "CONNECTED":
                System.out.println("🆔 You are player ID: " + parts[1]);
                break;

            case "GAME_START":
                System.out.println("🎮 Game started! " + parts[1]);
                break;

            case "YOUR_TURN":
                System.out.println("🕹️ It's your turn!");
                isMyTurn = true;
                break;

            case "DICE_ROLLED":
                System.out.println("🎲 Dice rolled: " + parts[1] + " and " + parts[2]);
                break;

            case "MOVE_MADE":
                System.out.println("🪙 Player " + parts[1] + " made a move using dice: " + parts[2]);
                break;

            case "GAME_OVER":
                System.out.println("🏁 " + parts[1]);
                System.exit(0);
                break;

            default:
                System.out.println("📩 Server: " + msg);
        }
    }
}

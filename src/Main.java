
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 5008;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connected to Broker");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Connection closed.");
                }
            }).start();

            while (true) {
                System.out.print("> ");
                String userInput = scanner.nextLine();
                out.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Could not connect to broker: " + e.getMessage());
        }
    }
}

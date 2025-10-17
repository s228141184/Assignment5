import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BrokerServer {
    private static final int PORT = 5008;
    private static final AtomicInteger clientIdCounter = new AtomicInteger(1);
    private static final Queue<ClientHandler> waitingPlayers = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        System.out.println("Broker Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                int id = clientIdCounter.getAndIncrement();
                ClientHandler handler = new ClientHandler(clientSocket, id);
                handler.start();
                waitingPlayers.add(handler);

                if (waitingPlayers.size() >= 2) { // for simplicity: 2 players per game
                    startNewGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startNewGame() {
        List<ClientHandler> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(waitingPlayers.poll());
        }
        GameSession game = new GameSession(players);
        game.start();
    }
}


//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class BrokerServer {
//    private static final int PORT = 5008;
//    private static final Map<String, Set<ClientHandler>> topicSubscribers = new ConcurrentHashMap<>();
//
//    public static void main(String[] args) {
//        System.out.println("Broker Server started on port " + PORT);
//        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                ClientHandler handler = new ClientHandler(clientSocket);
//                handler.start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static class ClientHandler extends Thread {
//        private final Socket socket;
//        private PrintWriter out;
//        private BufferedReader in;
//        private final Set<String> mySubscriptions = new HashSet<>();
//
//        public ClientHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            try {
//                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                out = new PrintWriter(socket.getOutputStream(), true);
//                out.println("✅ Connected to Broker");
//
//                String line;
//                while ((line = in.readLine()) != null) {
//                    handleCommand(line);
//                }
//
//            } catch (IOException e) {
//                System.out.println("Client disconnected");
//            } finally {
//                cleanup();
//            }
//        }
//
//        private void handleCommand(String command) {
//            try {
//                switch (command.toLowerCase()) {
//                    case "subscribe":
//                        sendAvailableTopics();
//                        out.println("Which topic would you like to subscribe to?");
//                        String subTopic = in.readLine();
//                        topicSubscribers.computeIfAbsent(subTopic, k -> ConcurrentHashMap.newKeySet()).add(this);
//                        mySubscriptions.add(subTopic);
//                        out.println("Subscribed to topic: " + subTopic);
//                        break;
//
//                    case "unsubscribe":
//                        sendMyTopics();
//                        out.println("Which topic would you like to unsubscribe from?");
//                        String unsubTopic = in.readLine();
//                        if (mySubscriptions.contains(unsubTopic)) {
//                            topicSubscribers.getOrDefault(unsubTopic, new HashSet<>()).remove(this);
//                            mySubscriptions.remove(unsubTopic);
//                            out.println("Unsubscribed from topic: " + unsubTopic);
//                        } else {
//                            out.println("You are not subscribed to: " + unsubTopic);
//                        }
//                        break;
//
//                    case "publish":
//                        out.println("Enter topic to publish to:");
//                        String topic = in.readLine();
//                        out.println("Enter message:");
//                        String msg = in.readLine();
//                        publishToTopic(topic, msg);
//                        break;
//
//                    default:
//                        out.println("Unknown command. Use subscribe, unsubscribe, or publish.");
//                }
//
//                out.println("What would you like to do next? (subscribe / unsubscribe / publish)");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void publishToTopic(String topic, String message) {
//            Set<ClientHandler> subscribers = topicSubscribers.getOrDefault(topic, Collections.emptySet());
//            if (subscribers.isEmpty()) {
//                out.println("No one subscribed to topic: " + topic);
//                return;
//            }
//
//            for (ClientHandler handler : subscribers) {
//                if (handler != this) {
//                    handler.out.println(topic + "  : " + message);
//                }
//            }
//            out.println("Message published to topic: " + topic);
//        }
//
//        private void sendAvailableTopics() {
//            if (topicSubscribers.isEmpty()) {
//                out.println("No topics yet. You can create one.");
//            } else {
//                out.println("Available topics:");
//                topicSubscribers.keySet().forEach(t -> out.println("- " + t));
//            }
//        }
//
//        private void sendMyTopics() {
//            if (mySubscriptions.isEmpty()) {
//                out.println("You are not subscribed to any topics.");
//            } else {
//                out.println("Your subscriptions:");
//                mySubscriptions.forEach(t -> out.println("- " + t));
//            }
//        }
//
//        private void cleanup() {
//            for (String topic : mySubscriptions) {
//                Set<ClientHandler> subs = topicSubscribers.get(topic);
//                if (subs != null) {
//                    subs.remove(this);
//                }
//            }
//            try {
//                socket.close();
//            } catch (IOException ignored) {}
//        }
//    }
//}

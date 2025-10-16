import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class BrokerServer {
    public static int clientID = 0;
    public Map<Integer, String, Se>
    public static int PORT = 5008;

    public static void main(String[] args) {
        new BrokerServer().startServer();
    }
    public void startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true){
                Socket socket = server.accept();
                new HandleClients(socket).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static class HandleClients extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public HandleClients(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

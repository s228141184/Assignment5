import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class BrokerClient {
    public static final int PORT = 5008;
    private static final String HOST = "localhost";
    public static void main(String[] args) {

        try {
            Socket socket = new Socket(InetAddress.getByName("0.0.0.0"), PORT);
            new HandleServer(socket).start();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(System.in);

            System.out.print("CONNECTED TO BROKER, Enter COMMAND(SUBSCRIBE/UNSUBSCRIBE/PUBLISH): ");
            while(true) {
                String command = in.nextLine();
                out.println(command);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static class HandleServer extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public HandleServer(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

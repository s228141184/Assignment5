import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class BrokerClient {
    public static int PORT = 5008;



    public static void main(String[] args) {


        try {
            Socket socket = new Socket(InetAddress.getByName("0.0.0.0"), PORT);
            new HandleServer(socket).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static class HandleServer extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public HandleServer(Socket socket){
            socket = socket;
        }

        @Override
        public void run() {
            super.run();
        }
    }
}

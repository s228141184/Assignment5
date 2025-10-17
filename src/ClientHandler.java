import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler extends Thread {
    private final Socket socket;
    private final int id;
    private PrintWriter out;
    private BufferedReader in;
    private String lastCommand = null;

    public ClientHandler(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String waitForCommand() {
        while (lastCommand == null) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        String cmd = lastCommand;
        lastCommand = null;
        return cmd;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("CONNECTED|" + id);

            String line;
            while ((line = in.readLine()) != null) {
                lastCommand = line;
            }
        } catch (IOException e) {
            System.out.println("Client " + id + " disconnected.");
        }
    }
}

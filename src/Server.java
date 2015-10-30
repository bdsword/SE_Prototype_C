import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by bdsword on 10/30/15.
 */
public class Server {
    public static void main(String[] args) {
        new Server();
    }

    private boolean shouldStop;
    private final int MAX_BUFFER_SIZE = 128;
    private final int PORT = 5566;

    public Server() {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(0);
        }

        while (!shouldStop) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Receive msg: " + msg);
        }

        socket.close();
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }
}

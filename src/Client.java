import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by bdsword on 10/30/15.
 */
public class Client {
    public static void main(String[] args) {
        new Client();
    }

    private final int PORT = 5566;
    private final double UPDATE_COORDINATE_PERIOD = 2   * 1000;
    private final double UPLOAD_COORDINATE_PERIOD = 0.2 * 1000;
    private int x;
    private int y;
    private List<String> serverIPsList;

    public Client() {
        initCoordinate();
        serverIPsList = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input servers' IPs: ");

        Thread coordinateUpdateThread = new Thread(new CoordinateUpdater(this));
        coordinateUpdateThread.start();

        String serverIP1 = scanner.nextLine();
        Thread coordinateUploadThread1 = new Thread(new CoordinateUploader(serverIP1, this));
        coordinateUploadThread1.start();

        String serverIP2 = scanner.nextLine();
        Thread coordinateUploadThread2 = new Thread(new CoordinateUploader(serverIP2, this));
        coordinateUploadThread2.start();
    }

    private void initCoordinate() {
        x = 0;
        y = 0;
    }

    private class CoordinateUploader implements Runnable {

        private String serverIP;
        private Client client;

        public CoordinateUploader(String serverIP, Client client) {
            this.serverIP = serverIP;
            this.client = client;
        }

        private void uploadCoordinate() throws IOException {
            String msg = String.format("%d %d", client.x, client.y);
            DatagramPacket packet = new DatagramPacket(
                    msg.getBytes(), msg.getBytes().length, InetAddress.getByName(serverIP), PORT);
            new DatagramSocket().send(packet);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    uploadCoordinate();
                    Thread.sleep((long) UPLOAD_COORDINATE_PERIOD);
                } catch (InterruptedException e) {
                    System.err.println("This thread [CoordinateUploader] is not designed to be interrupted.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CoordinateUpdater implements Runnable {

        private Client client;

        public CoordinateUpdater(Client client) {
            this.client = client;
        }

        private void updateCoordinate() {
            client.x += 1;
            if (client.x == 100) {
                client.x = 0;
            }

            client.y += 1;
            if (client.y == 100) {
                client.y = 0;
            }
        }

        @Override
        public void run() {
            while (true){
                updateCoordinate();
                try {
                    Thread.sleep((long) UPDATE_COORDINATE_PERIOD);
                } catch (InterruptedException e) {
                    System.err.println("This thread [CoordinateUpdater] is not designed to be interrupted.");
                }
            }
        }
    }
}

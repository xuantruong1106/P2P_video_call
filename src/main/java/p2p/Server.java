package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final List<Integer> savePorts = new ArrayList<>();
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1106);
        System.out.println("Server is running and waiting for connections...");

        // Bắt đầu một luồng để lắng nghe các kết nối đến và gửi thông tin port
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    if (savePorts.isEmpty()) {
                        System.out.println("There are no ports stored in the array");
                        dos.writeBoolean(false);
                        dos.writeUTF("Hello, no ports available.");
                    } else {
                        System.out.println("There are ports stored in the array");
                        dos.writeBoolean(true);
                        dos.writeUTF("Hello, ports available: " + savePorts);
                    }

                    String clientName = din.readUTF();
                    System.out.println("Client connected: " + clientName);

                    // Đọc port từ client
                    int clientPort = din.readInt();
                    System.out.println("Received port from client: " + clientPort);

                    // Lưu port vào danh sách savePorts
                    savePorts.add(clientPort);

                    // Lưu thông tin client vào danh sách clients
                    clients.add(clientSocket);

                    // Optionally, you can print the current list of ports
                    System.out.println("Current list of ports: " + savePorts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }
}

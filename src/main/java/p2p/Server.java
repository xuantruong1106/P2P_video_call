package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends RemoveClientOutServer {

    private static final RemoveClientOutServer rm = new RemoveClientOutServer();

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

                    if (rm.clientInfos.isEmpty()) {
                        System.out.println("There are no clients connected");
                        dos.writeBoolean(false);
                        dos.writeUTF("Hello, no clients available.");
                    } else {
                        System.out.println("There are clients connected");
                        dos.writeBoolean(true);

                        for (ClientInfo info : rm.clientInfos) {
                            dos.writeUTF("Hello, clients available: " + info.getClientName() + " Port: " + info.getPort());
                        }
                    }

                    String clientName = din.readUTF();
                    System.out.println("Client connected: " + clientName);

                    // Đọc port từ client
                    int clientPort = din.readInt();
                    System.out.println("Received port from client: " + clientPort);

                    // Lưu thông tin client vào danh sách clients
                    ClientInfo clientInfo = new ClientInfo(clientPort, clientSocket, clientName);
                    rm.clientInfos.add(clientInfo);

//                    // Print the current list of clients
//                    for (ClientInfo info : rm.clientInfos) {
//                        System.out.println("Client: " + info.getClientName() + " Port: " + info.getPort());
//                    }

                   
                    // Check for notification after adding the client
                    if (rm.getTrue() == true) {
                        System.out.println("Client disconnected and removed from the list.");
                    }                   

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }
}
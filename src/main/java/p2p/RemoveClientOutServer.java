/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author admin
 */

class ClientInfo {

    private int port;
    private Socket clientSocket;
    private String clientName;

    public ClientInfo(int port, Socket clientSocket, String clientName) {
        this.port = port;
        this.clientSocket = clientSocket;
        this.clientName = clientName;
    }

    public int getPort() {
        return port;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientName() {
        return clientName;
    }
}

public class RemoveClientOutServer {

    static final List<ClientInfo> clientInfos = new ArrayList<>();
    static volatile boolean state;

    public static boolean getTrue() {
        return state;
    }

    public static void main(String[] args) throws Exception {
        
        // Start a background thread to continuously check and update the state
        Thread backgroundThread = new Thread(() -> {
            try {
                while (true) {
                    // Duyệt qua danh sách clients và loại bỏ những client đã đóng kết nối
                    Iterator<ClientInfo> iterator = clientInfos.iterator();
                    while (iterator.hasNext()) {
                        ClientInfo clientInfo = iterator.next();

                        if (clientInfo.getClientSocket().isClosed()) {
                            // Client đã đóng kết nối, loại bỏ khỏi danh sách
                            iterator.remove();

                            // Print a message about the disconnected client
                            System.out.println("Client disconnected: " + clientInfo.getClientName() + " with port " + clientInfo.getPort());

                            state = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the background thread
        backgroundThread.start();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author My
 */
public class Server {

    public Server() throws Exception {
        String from_client;
        String to_client;
        Integer port = 1106;

        ServerSocket serversocker = new ServerSocket(port);
        Socket socket = serversocker.accept();
        System.out.println("successfull" + socket);
        while (true) {

            BufferedReader inFromClient
                    = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Tạo outputStream, nối tới socket
            DataOutputStream outToClient
                    = new DataOutputStream(socket.getOutputStream());

            //Đọc thông tin từ socket
            from_client = inFromClient.readLine();

            to_client = from_client + " (Server accepted!)" + '\n';
            //ghi dữ liệu ra socket
            outToClient.writeBytes(to_client);
            
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        return;

    }

}

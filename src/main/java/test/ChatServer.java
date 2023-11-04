/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) {
        int port = 12345;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Peer A started on port " + port);

            Socket socket = serverSocket.accept();
            System.out.println("Peer A is connected to Peer B.");

            // Đọc dữ liệu từ Peer B
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Đọc và gửi dữ liệu
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Peer A: ");
                String message = scanner.nextLine();
                writer.println(message);

                String receivedMessage = reader.readLine();
                System.out.println("Peer B: " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


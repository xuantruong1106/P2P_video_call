/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        int port = 123455;

        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Peer B is connected to Peer A.");

            // Đọc dữ liệu từ Peer A
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Đọc và gửi dữ liệu
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Peer B: ");
                String message = scanner.nextLine();
                writer.println(message);

                String receivedMessage = reader.readLine();
                System.out.println("Peer A: " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author My
 */
public class Client {

    public static void main(String argv[]) throws Exception {

        //Tạo socket cho client kết nối đến server qua ID address và port number
        Socket clientSocket = new Socket("127.0.0.1", 1106);

        
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Input from client: ");

            //Lấy chuỗi ký tự nhập từ bàn phím
            String sentence_to_server = sc.nextLine();

            //Tạo OutputStream nối với Socket
        DataOutputStream dos
                = new DataOutputStream(clientSocket.getOutputStream());

        //Tạo inputStream nối với Socket
            DataInputStream din
                = new DataInputStream(clientSocket.getInputStream());

        
            //Gửi chuỗi ký tự tới Server thông qua outputStream đã nối với Socket (ở trên)
            dos.writeUTF(sentence_to_server + '\n');

            //Đọc tin từ Server thông qua InputSteam đã nối với socket
            String sentence_from_server = din.readUTF();

            //print kết qua ra màn hình
            System.out.println("FROM SERVER: " + sentence_from_server);

        }

    }
}

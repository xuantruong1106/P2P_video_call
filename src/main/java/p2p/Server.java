package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1106);
        System.out.println("Server is running and waiting for connections...");

        // Chấp nhận kết nối từ client
        Socket socket = serverSocket.accept();
        System.out.println("Client connected: " + socket.getInetAddress());

        // Tạo input và output streams cho client
        DataInputStream din = new DataInputStream(socket.getInputStream());
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

        while (true) {
            // Đọc thông tin từ client
            String fromClient = din.readUTF();
            System.out.println("From client " + socket.getInetAddress() + ": " + fromClient);

            // Gửi dữ liệu đến client
            System.out.print("To client " + socket.getInetAddress() + ": ");
            Scanner sc = new Scanner(System.in);
            String toClient = sc.nextLine();
            outToClient.writeUTF(toClient);
        }
    }
}

package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class Client extends ConnectSocket {

    public static void main(String argv[]) {

        Scanner sc = new Scanner(System.in);

        try {
            Socket socketServer = new Socket("localhost", 1106);

            if (socketServer.isConnected()) {

                DataInputStream din = new DataInputStream(socketServer.getInputStream());

                DataOutputStream dos = new DataOutputStream(socketServer.getOutputStream());

                while (true) {
                    boolean checkStateListPort = din.readBoolean();

                    if (checkStateListPort == true) {
                        System.out.println(din.readUTF());

                        System.out.println("enter port join meet");
                        int port = sc.nextInt();
                        try {
                            Socket socketClient = new Socket("localhost", port);

                            DataInputStream din1 = new DataInputStream(socketClient.getInputStream());

                            DataOutputStream dos1 = new DataOutputStream(socketClient.getOutputStream());

                            System.out.println(din1.readUTF());
                        } catch (Exception e) {
                            System.out.println("don't access socket");
                        }
                    } else {
                        System.out.println(din.readUTF());

                        System.out.println("enter name");
                        String name = sc.nextLine();
                        dos.writeUTF(name);

                        System.out.println("enter port create server meet");
                        int port = sc.nextInt();
                        dos.writeInt(port);

                        // Corrected code for ServerSocket instantiation
                        ServerSocket sSClient = new ServerSocket(port);

                        try {
                            while (true) {
                                Socket sClinet = sSClient.accept();

                                DataInputStream din2 = new DataInputStream(sClinet.getInputStream());
                                DataOutputStream dos2 = new DataOutputStream(sClinet.getOutputStream());

                                dos2.writeUTF("hello client 2");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

            } else {

                System.out.println("cannot access server");
                System.out.println("enter port join meet");
                int port = sc.nextInt();

                Socket socketClient = new Socket("localhost", port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cannot access ");

        }
    }
}

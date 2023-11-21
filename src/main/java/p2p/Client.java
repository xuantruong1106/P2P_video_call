package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String argv[]) throws Exception {

        Scanner sc = new Scanner(System.in);

        try {
            // Connect to the server
            Socket socketServer = new Socket("localhost", 1106);

//            if (socketServer.isConnected()) {
            DataInputStream din = new DataInputStream(socketServer.getInputStream());
            DataOutputStream dos = new DataOutputStream(socketServer.getOutputStream());

            while (true) {
                boolean checkStateListPort = din.readBoolean();

                if (checkStateListPort) {
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
                        System.out.println(" socketClient - 36");
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
                        Socket sClinet = sSClient.accept();

                        DataInputStream din2 = new DataInputStream(sClinet.getInputStream());
                        DataOutputStream dos2 = new DataOutputStream(sClinet.getOutputStream());

                        dos2.writeUTF("hello client 2");
                        while (true) {
                            System.out.println("client aswer: " + din2.readUTF());

                            System.out.println("enter message to client: ");
                            String message = sc.nextLine();

                            dos2.writeUTF(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(" sClinet - 71");
                    }
                }
            }
//            } else {
//                System.out.println("Cannot connect to the server. Please try again.");
//                // Add any additional logic you want for this condition.
//            }

        } catch (Exception e) {

            System.out.println("enter port join meet");
            int port = sc.nextInt();
            try {
                Socket socketClient = new Socket("localhost", port);

                DataInputStream din2 = new DataInputStream(socketClient.getInputStream());
                DataOutputStream dos2 = new DataOutputStream(socketClient.getOutputStream());

                while (true) {
                    System.out.println(" client aswer: " + din2.readUTF());

                    System.out.println("enter message to client: ");
                    String message = sc.nextLine();

                    dos2.writeUTF(message);
                }

            } catch (Exception e1) {
                System.out.println("don't access socket");
                System.out.println(" socketClient - 94");
            }

        }
    }
}

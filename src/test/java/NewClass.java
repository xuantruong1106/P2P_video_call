import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Socket socketServer = new Socket("localhost", 1106);

            while (true) {
                Thread clientThread = new Thread(() -> clientRun(socketServer, scanner));
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error connecting to the server.");
            e.printStackTrace();
        }
    }

    private static void clientRun(Socket socketServer, Scanner scanner) {
        try {
            DataInputStream din = new DataInputStream(socketServer.getInputStream());
            DataOutputStream dos = new DataOutputStream(socketServer.getOutputStream());

            while (true) {
                boolean checkStateListPort = din.readBoolean();

                if (checkStateListPort) {
                    handleJoinMeeting(din, dos, scanner);
                } else {
                    handleCreateMeeting(din, dos, scanner);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in clientRun method.");
            e.printStackTrace();
        }
    }

    private static void handleJoinMeeting(DataInputStream din, DataOutputStream dos, Scanner scanner) {
        try {
            System.out.println(din.readUTF());

            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            dos.writeUTF(name);

            System.out.print("Enter port to join the meeting: ");
            int portInt = Integer.parseInt(scanner.nextLine());

            try (Socket socketClient = new Socket("localhost", portInt);
                 DataInputStream din1 = new DataInputStream(socketClient.getInputStream());
                 DataOutputStream dos1 = new DataOutputStream(socketClient.getOutputStream())) {

                dos1.writeUTF(name);

                while (true) {
                    System.out.println("Client answer: " + din1.readUTF());

                    System.out.print("Enter message to client: ");
                    String message = scanner.nextLine();
                    dos1.writeUTF(message);
                }
            } catch (Exception e) {
                System.out.println("Error in joining the meeting.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error in handleJoinMeeting method.");
            e.printStackTrace();
        }
    }

    private static void handleCreateMeeting(DataInputStream din, DataOutputStream dos, Scanner scanner) {
        try {
            System.out.println(din.readUTF());

            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            dos.writeUTF(name);

            System.out.print("Enter port to create the server meet: ");
            int portInt = Integer.parseInt(scanner.nextLine());

            dos.writeInt(portInt);

            System.out.println("Waiting for client to connect...");

            try (ServerSocket sSClient = new ServerSocket(portInt);
                 Socket sClinet = sSClient.accept();
                 DataInputStream din2 = new DataInputStream(sClinet.getInputStream());
                 DataOutputStream dos2 = new DataOutputStream(sClinet.getOutputStream())) {

                String clientName = din2.readUTF();
                System.out.println("Client connected: " + clientName);
                dos2.writeUTF("Hello client " + clientName);

                while (true) {
                    System.out.println("Client answer: " + din2.readUTF());

                    System.out.print("Enter message to client: ");
                    String message = scanner.nextLine();
                    dos2.writeUTF(message);
                }
            } catch (Exception e) {
                System.out.println("Error in creating the meeting.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error in handleCreateMeeting method.");
            e.printStackTrace();
        }
    }
}

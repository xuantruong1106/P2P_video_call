/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 *
 * @author My
 */
public class Client {
     public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String name = in.nextLine().trim();
            InterServer chatServer = (InterServer) Naming.lookup("rmi://localhost/ABC");
            InterClientImp chatClient = new InterClientImp();
            UnicastRemoteObject.exportObject((Remote) chatClient, 0);
            chatServer.registerClient(name, chatClient);
            String msg;
            msg = "[" + name + "] got connected";
            chatServer.unicastMessage(msg);
            while (true) {
                msg = in.nextLine().trim();
                if (msg.equals("exit")) {
                    break;
                }
                msg = "[From " + name + "] " + msg;
                chatClient.client.retrieveMessage(msg);
            }
            System.out.println("End ");
            System.exit(0);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            System.out.println("[System] Server failed: " + e);
        }
    }
}

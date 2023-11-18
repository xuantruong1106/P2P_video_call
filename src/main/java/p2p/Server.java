/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author My
 */
public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099); 
            InterServerImp chatServer = new InterServerImp();
            UnicastRemoteObject.exportObject(chatServer, 0); 
            String url = "rmi://localhost/ABC";
            Naming.rebind(url, chatServer);
            System.out.println("[Server] is ready..."); 
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("[System] Server failed: " + e);
        } 
    }
}

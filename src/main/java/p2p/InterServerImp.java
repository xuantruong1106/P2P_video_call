/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

import java.rmi.RemoteException;

/**
 *
 * @author My
 */
public class InterServerImp implements InterServer{
     public String name1;
    public String name2;
    public InterClient client1;
    public InterClient client2;
    public InterServerImp() throws RemoteException {
    }
    @Override
    public void unicastMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }
    @Override
    public void registerClient(String name, InterClient client) throws RemoteException {
        if(this.client1 == null){
            this.client1 = client;
            this.name1 = name;
        }
        else{
            this.client2 = client;
            this.name2 = name;
            client2.registerClient(client1);
            client1.registerClient(client2);
            client2.retrieveMessage("[" + name1 + "] is waiting...");
            client1.retrieveMessage("[" + name2 + "] got connected...");
        }
    } 
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author My
 */
public interface InterServer extends Remote{
   public void unicastMessage(String msg) throws RemoteException;
    public void registerClient(String name, InterClient client) throws RemoteException;
}

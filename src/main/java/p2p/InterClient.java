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
public interface InterClient extends Remote{
    public void retrieveMessage(String msg) throws RemoteException;
    public void registerClient(InterClient client) throws RemoteException;
}

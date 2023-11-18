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
public class InterClientImp implements InterClient{
    public InterClient client;
    @Override
    public void retrieveMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }
    @Override
    public void registerClient(InterClient client) throws RemoteException {
        this.client = client;
    }
}

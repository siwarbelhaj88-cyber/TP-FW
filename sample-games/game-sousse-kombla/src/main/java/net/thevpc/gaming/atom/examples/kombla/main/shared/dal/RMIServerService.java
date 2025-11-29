package net.thevpc.gaming.atom.examples.kombla.main.shared.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerService extends Remote {
    StartGameInfo connect(String playerName, RMIClientService client) throws RemoteException;
    void moveLeft(int playerId) throws RemoteException;
    void moveRight(int playerId) throws RemoteException;
    void moveUp(int playerId) throws RemoteException;
    void moveDown(int playerId) throws RemoteException;
    void releaseBomb(int playerId) throws RemoteException;

}

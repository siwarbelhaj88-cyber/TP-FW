package net.thevpc.gaming.atom.examples.kombla.main.shared.dal;

import net.thevpc.gaming.atom.examples.kombla.main.client.dal.MainClientDAOListener;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientService extends Remote {
    void onModelChanged(DynamicGameModel model) throws RemoteException;
    StartGameInfo connect(String playerName, RMIClientService client) throws RemoteException;
    void moveLeft(int playerId) throws RemoteException;
    void moveRight(int playerId) throws RemoteException;
    void moveUp(int playerId) throws RemoteException;
    void moveDown(int playerId) throws RemoteException;
    void releaseBomb(int playerId) throws RemoteException;
}

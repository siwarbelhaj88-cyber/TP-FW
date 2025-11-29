package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAOListener;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIServerService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RMIClientServiceImpl extends UnicastRemoteObject implements RMIClientService {

    private MainServerDAOListener listener;
    private AppConfig properties;


    private Map<Integer, RMIClientService> connectedClients = new ConcurrentHashMap<>();

    /**
     * Constructeur nécessaire pour UnicastRemoteObject
     */
    public RMIClientServiceImpl() throws RemoteException {
        super();
    }




    public void sendModelChanged(DynamicGameModel dynamicGameModel) throws RemoteException {

        for (Map.Entry<Integer, RMIClientService> entry : connectedClients.entrySet()) {

            entry.getValue().onModelChanged(dynamicGameModel);

        }
    }


    @Override
    public void onModelChanged(DynamicGameModel model) throws RemoteException {

    }

    @Override
    public StartGameInfo connect(String playerName, RMIClientService client) throws RemoteException {
        return null; //lazem yzidou l liste haka 3lh 3tineh clientt
    }

    public void moveLeft(int playerId) throws RemoteException {
        listener.onReceiveMoveLeft(playerId);
    }


    public void moveRight(int playerId) throws RemoteException {
        listener.onReceiveMoveRight(playerId);
    }


    public void moveUp(int playerId) throws RemoteException {
        listener.onReceiveMoveUp(playerId);
    }


    public void moveDown(int playerId) throws RemoteException {
        listener.onReceiveMoveDown(playerId);
    }


    public void releaseBomb(int playerId) throws RemoteException {
        listener.onReceiveReleaseBomb(playerId);
    }

    /**
     * Méthode utilitaire pour retirer un client déconnecté
     */
    public void removeClient(int playerId) {
        connectedClients.remove(playerId);
        System.out.println("Client déconnecté: " + playerId);
    }
}

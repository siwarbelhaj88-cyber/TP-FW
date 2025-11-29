package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAO;
import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAOListener;
import net.thevpc.gaming.atom.examples.kombla.main.server.dal.RMIServerServiceImpl;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RmiMainClientDAO implements MainClientDAO {
    private MainServerDAOListener listener;
    private AppConfig properties;
    private Map<Integer, RMIClientService> connectedClients = new ConcurrentHashMap<>();


        public void start(MainServerDAOListener listener, AppConfig properties) throws IOException {
            this.listener = listener;
            this.properties = properties;

            int serverPort = properties.getServerPort();
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind("GameServer", new RMIServerServiceImpl());

            System.out.println("Serveur RMI est démarré sur le port " + serverPort);


        }


    public StartGameInfo connect(String playerName, RMIClientService client) throws RemoteException {

        StartGameInfo startInfo = listener.onReceivePlayerJoined(playerName);
        System.out.println("Joueur est connecté: " + playerName + " (ID: " + startInfo.getPlayerId() + ")");
        connectedClients.put(startInfo.getPlayerId(), client);
        return startInfo;
    }


    @Override
    public void sendModelChanged(DynamicGameModel dynamicGameModel) {

    }

    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {

    }

    @Override
    public StartGameInfo connect() {
        return null;
    }

    @Override
    public void sendMoveLeft() {

    }

    @Override
    public void sendMoveRight() {

    }

    @Override
    public void sendMoveUp() {

    }

    @Override
    public void sendMoveDown() {

    }

    @Override
    public void sendFire() {

    }
}

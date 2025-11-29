package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side TCP implementation
 */
public class TCPMainServerDAO implements MainServerDAO {
    //dal = data access layer
    //dao = houwa objet
    private ServerSocket serverSocket;
    private MainServerDAOListener listener;
    private AppConfig properties;
    private Map<Integer, ClientSession> playerToSocketMap = new ConcurrentHashMap<>();
    private volatile boolean running = false;


    public class ClientSession {
        int id;
        Socket s;
        DataInputStream in;
        DataOutputStream out;
        volatile boolean active = true;
    }

    @Override
    public void start(MainServerDAOListener listener, AppConfig properties) throws IOException {
        this.listener = listener;
        this.properties = properties;
        this.running = true;

        // nabdew el server fi thread jdid
        new Thread(this::startSync).start();
    }

    /**
     * el main loop mta3 el server - nestannew el clients
     */
    private void startSync() {
        try {
            int port = properties.getServerPort();
            serverSocket = new ServerSocket(port);
            System.out.println("Serveur TCP démarré sur le port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nouvelle connexion: " + clientSocket.getInetAddress());

                    // na3mlou handling lel client el jdid
                    handleNewClient(clientSocket);

                } catch (SocketException e) {
                    if (!running) break; // normal quand el server yet9afel
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleNewClient(Socket clientSocket) {
        ClientSession cs = new ClientSession();
        cs.s = clientSocket;

        try {
            cs.in = new DataInputStream(clientSocket.getInputStream());
            cs.out = new DataOutputStream(clientSocket.getOutputStream());

            // naqraw esm el player
            String name = cs.in.readUTF();

            // n3almou el engine eli player jdid dkhel
            StartGameInfo i = listener.onReceivePlayerJoined(name);
            cs.id = i.getPlayerId();

            // nab3thou el start info lel client
            envoyer(i, cs);

            // na7fodhoh fel map
            playerToSocketMap.put(cs.id, cs);

            System.out.println("Joueur '" + name + "' connecté avec ID: " + cs.id);
            System.out.println("Nombre de joueurs: " + playerToSocketMap.size());

            // na3mloulou thread ya3mel handle lel requests mte3ou
            new Thread(() -> handleClient(cs)).start();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    void envoyer(StartGameInfo i, ClientSession cs) throws IOException {
        synchronized (cs.out) {
            cs.out.writeInt(i.getPlayerId());
            int[][] maze = i.getMaze();

            cs.out.writeInt(maze.length);
            cs.out.writeInt(maze[0].length);
            for (int[] row : maze) {
                for (int cell : row) {
                    cs.out.writeInt(cell);
                }
            }
            cs.out.flush();
        }
    }

    /**
     * na3mlou handle lel requests mta3 kol client
     */
    private void handleClient(ClientSession cs) {
        try {
            while (running && cs.active) {
                traiterReq(cs);
            }
        } catch (IOException e) {
            if (cs.active) {
                System.out.println("Client " + cs.id + " déconnecté");
            }
        } finally {
            removeClient(cs);
        }
    }


    private void traiterReq(ClientSession cs) throws IOException {
        int command = cs.in.readInt();
        int playerId = cs.in.readInt();

        // n7aqq eli el player ID ye5i
        if (playerId != cs.id) {
            System.err.println("Player ID yekhou: reçu " + playerId + ", attendu " + cs.id);
            return;
        }

        // na3mlou process lel command
        switch (command) {
            case ProtocolConstants.LEFT:
                listener.onReceiveMoveLeft(playerId);
                break;
            case ProtocolConstants.RIGHT:
                listener.onReceiveMoveRight(playerId);
                break;
            case ProtocolConstants.UP:
                listener.onReceiveMoveUp(playerId);
                break;
            case ProtocolConstants.DOWN:
                listener.onReceiveMoveDown(playerId);
                break;
            case ProtocolConstants.FIRE:
                listener.onReceiveReleaseBomb(playerId);
                break;
            default:
                System.err.println("Commande ma3raftech: " + command);
        }
    }

    public void sendModelChanged(DynamicGameModel model) {
        // nab3thou el model lel kol les clients
        for (ClientSession cs : playerToSocketMap.values()) {
            if (!cs.active) continue;

            try {
                synchronized (cs.out) {
                    cs.out.writeUTF("MODEL_UPDATE");



                    cs.out.flush();
                }
            } catch (IOException e) {
                System.err.println("Ma7chitech nab3eth lel client " + cs.id);
                cs.active = false;
                removeClient(cs);
            }
        }
    }


    private void removeClient(ClientSession cs) {
        cs.active = false;
        playerToSocketMap.remove(cs.id);

        try {
            if (cs.out != null) cs.out.close();
            if (cs.in != null) cs.in.close();
            if (cs.s != null && !cs.s.isClosed()) cs.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client " + cs.id + " tfas5. Joueurs restants: " + playerToSocketMap.size());
    }


    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ClientSession cs : playerToSocketMap.values()) {
            try {
                synchronized (cs.out) {
                    cs.out.writeUTF("DISCONNECT");
                    cs.out.flush();
                }
            } catch (IOException e) {
                // mech mohim
            }
            removeClient(cs);
        }

        playerToSocketMap.clear();
        System.out.println("Serveur TCP arrêté");
    }

    public int getConnectedClientsCount() {
        return playerToSocketMap.size();
    }

    public boolean isRunning() {
        return running;
    }
}
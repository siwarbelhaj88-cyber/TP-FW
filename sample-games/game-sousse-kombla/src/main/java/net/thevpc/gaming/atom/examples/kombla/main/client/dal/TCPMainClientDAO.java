package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client-side TCP implementation
 */
public class TCPMainClientDAO implements MainClientDAO {
    private MainClientDAOListener listener;
    private AppConfig properties;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int playerId;
    private boolean running = false;
    private Thread listenerThread;

    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {
        this.listener = listener;
        this.properties = properties;
    }

    @Override
    public StartGameInfo connect() {
        try {
            String serverAddress = properties.getServerAddress();
            int serverPort = properties.getServerPort();

            socket = new Socket(serverAddress, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // nab3thou esm el player lel server
            String playerName = properties.getPlayerName();
            out.writeUTF(playerName);
            out.flush();

            // nestanaw el StartGameInfo men el server
            StartGameInfo info = receiveStartGameInfo();
            this.playerId = info.getPlayerId();

            System.out.println("Connected successfully with player ID: " + playerId);

            // nabdew thread bech nesma3 el updates mel server
            running = true;
            listenerThread = new Thread(this::listenToServer);
            listenerThread.start();

            return info;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private StartGameInfo receiveStartGameInfo() throws IOException {
        int playerId = in.readInt();
        int rows = in.readInt();
        int cols = in.readInt();

        int[][] maze = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = in.readInt();
            }
        }

        return new StartGameInfo(playerId, maze);
    }


    private void listenToServer() {
        try {
            while (running && !socket.isClosed()) {
                String messageType = in.readUTF();

                if ("MODEL_UPDATE".equals(messageType)) {
                    DynamicGameModel model = receiveDynamicGameModel();
                    if (model != null) {
                        listener.onModelChanged(model);
                    }
                } else if ("DISCONNECT".equals(messageType)) {
                    System.out.println("Server requested disconnect");
                    break;
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Connection to server lost: " + e.getMessage());
            }
        }
    }


    private DynamicGameModel receiveDynamicGameModel() throws IOException {


        DynamicGameModel model = new DynamicGameModel();
        return model;
    }

    @Override
    public void sendMoveLeft() {
        sendCommand(ProtocolConstants.LEFT);
    }

    @Override
    public void sendMoveRight() {
        sendCommand(ProtocolConstants.RIGHT);
    }

    @Override
    public void sendMoveUp() {
        sendCommand(ProtocolConstants.UP);
    }

    @Override
    public void sendMoveDown() {
        sendCommand(ProtocolConstants.DOWN);
    }

    @Override
    public void sendFire() {
        sendCommand(ProtocolConstants.FIRE);
    }


    private void sendCommand(int command) {
        if (socket == null || socket.isClosed()) {
            return;
        }

        try {
            synchronized (out) {
                out.writeInt(command);
                out.writeInt(playerId);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public boolean isConnected() {
        return socket != null && !socket.isClosed() && running;
    }

    public int getPlayerId() {
        return playerId;
    }
}
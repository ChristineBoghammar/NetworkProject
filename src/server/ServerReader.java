package server;

import protocol.Action;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by christineboghammar on 18/04/16.
 */


public class ServerReader extends Thread {
    private ServerMonitor mon;
    private Socket socket;

    public ServerReader(ServerMonitor mon, Socket connection) {
        this.mon = mon;
        this.socket = connection;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Action action = null;
            while ((action = ((Action) in.readObject())) != null) {
                mon.putMessage(action);
            }
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

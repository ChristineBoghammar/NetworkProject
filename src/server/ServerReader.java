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
    private ObjectInputStream ois;

    public ServerReader(ServerMonitor mon, Socket connection) {
        this.mon = mon;
        this.socket = connection;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Action action;
            while ((action = ((Action) ois.readObject())) != null) {

                System.out.println("Action read: " + action.getSender() + " " + action.getCmd());
                mon.putMessage(action);
            }
        } catch (IOException e) {
            System.err.print("IOException in ServerReader for client: " + "\n");
            mon.disconnectClient(socket);
        } catch (ClassNotFoundException e1) {
            System.err.print("ClassNotFoundException in ServerReader for client: " + "\n");
        }

    }
}

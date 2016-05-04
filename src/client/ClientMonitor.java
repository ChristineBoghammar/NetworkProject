package client;

import protocol.Action;
import server.Call;

import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientMonitor {
    private LinkedList<Action> actions;
    private String name;
    private Call call;
    private Socket socket;

    public ClientMonitor(String name, Socket s) {
        this.actions = new LinkedList<Action>();
        this.name = name;
        this.socket = s;
        call = null;
    }


    public void setCall(Call callID) {
        call = callID;
    }

    public Call getCall() {
        return call;
    }

    public void destroyCall() {
        call = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public synchronized Action getAction() {
        while (actions.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return actions.poll();

    }

    public synchronized void putAction(Action action){
        actions.add(action);
    }

    public void requestCall(Action action) {
        /**
         * Någon försöker starta ett samtal med denna client. Lösning är att godkänna eller neka. Görs via GUI.
         */
    }

    public void acceptCall(Action action) {
        /**
         * En person som förfrågades ett samtal har accepterat. Notifiera klienten via GUI
         */
    }

    public void rejectCall(Action action) {
        /**
         * En förfrågad person nekar ett samtal
         */
    }
}

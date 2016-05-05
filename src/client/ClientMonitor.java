package client;

import protocol.Action;
import server.Call;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientMonitor {
    private LinkedList<Action> actions;
    private String name;
    private Socket socket;
    private int callID;
    private ObjectOutputStream oos;

    public ClientMonitor(String name, Socket s) {
        this.actions = new LinkedList<Action>();
        this.name = name;
        this.socket = s;
        callID = -1;
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void setCallID(int callID) {
        this.callID  = callID;
    }

    public synchronized int getCallID() {
        return callID;
    }

    public synchronized void destroyCall() {
        callID = -1;
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized String getName() {
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
        System.out.println("PutAction: " + "cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallID());
        notifyAll();
    }

    public synchronized void requestCall(Action action) {
        /**
         * Någon försöker starta ett samtal med denna client. Lösning är att godkänna eller neka. Görs via GUI.
         */
            try {
                oos.writeObject(action);
                System.out.println("RequestCall Action written to server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        System.out.println("cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallList());
    }

    public synchronized void acceptCall(Action action) {
        /**
         * En person som förfrågades ett samtal har accepterat. Notifiera klienten via GUI
         */

        System.out.println("cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallID());

    }

    public synchronized void sendToCall(Action action) {
        System.out.println("cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallID());
    }

    public synchronized void connectClient(Action action) {
        try {
            oos.writeObject(action);
            System.out.println("connectClient Action written to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rejectCall(Action action) {
        /**
         * En förfrågad person nekar ett samtal
         */
    }

    public void recieveRequest(Action action) {
        System.out.println("Received requested call:" + action.getSender() + " " + action.getCmd());
    }
}

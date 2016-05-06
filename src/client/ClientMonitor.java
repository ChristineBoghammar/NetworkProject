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
    private final int CONNECT = 0;
    private final int DISCONNECT = 1;
    private final int INITIATE_CALL = 2;
    private final int ACCEPT_CALL = 3;
    private final int CLOSE_CALL = 4;
    private final int COMMUNICATE_TO_CALL = 5;
    private final int RECIEVE_REQUESTED_CALL = 6;
    private final int REJECT_CALL = 7;
    private final int RECIEVE_CLOSE_CALL = 8;
    /**
     * Possible cmd's are:
     * 0 - Connect to server
     * 1 - Disconnect from server
     * 2 - Initiate call
     * 3 - Accept call
     * 4 - Close call
     * 5 - Communication via call
     * 6 - Receive requested Call
     */

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
        this.callID = callID;
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

    public synchronized void putAction(Action action) {
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
        System.out.println(action.getSender() + " Has accepted the call");
    }

    public synchronized void rejectCall(Action action) {
        if(action.getContent().equals("n")){
            System.out.println(action.getSender() + " Has rejected the call");
        } else {
            System.out.println(action.getSender() + " is already in another call");
        }
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


    public synchronized void receiveRequest(Action action) {

        /**
         * Om användaren är upptagen
         */
//        if(callID != -1){
//            Action response = new Action("b",getName(), REJECT_CALL, action.getCallID());
//            try {
//                oos.writeObject(response);
//                System.out.println("connectClient Action written to server");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        /**
         * Om användaren godkänner samtalet
         */
        callID = action.getCallID();
        Action response = new Action("y", getName(), ACCEPT_CALL, action.getCallID());
        try {
            oos.writeObject(response);
            System.out.println("receiveRequest Action written to server");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Om användaren ej godkänner samtalet
         */
//        Action response = new Action("n",getName(), REJECT_CALL, action.getCallID());
//        try {
//            oos.writeObject(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public synchronized void closeCall() {
        Action closeAction = new Action("content", getName(), CLOSE_CALL, callID);
        callID = -1;
        try {
            oos.writeObject(closeAction);
            System.out.println("CloseCall action written to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveCloseCall(Action action) {
        System.out.println(action.getSender() + " Has left the call");
    }

    public void receiveCallID(Action action) {
        callID = action.getCallID();
        System.out.println("CallID is :" + action.getCallID());
    }
}


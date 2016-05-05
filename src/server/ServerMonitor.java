package server;

import protocol.Action;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by christineboghammar on 18/04/16.
 */
public class ServerMonitor {
    private LinkedList<Action> actions;
    private ArrayList<Participant> participants;
    private ArrayList<Call> activeCalls;
    private int uniqueID = 0;

    public ServerMonitor() {
        this.actions = new LinkedList<Action>();
        this.participants = new ArrayList<Participant>();
        this.activeCalls = new ArrayList<Call>();
    }


    public synchronized Action getMessage() {
        while (actions.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return actions.poll();

    }

    public synchronized void putMessage(Action action) {
        actions.add(action);
        notifyAll();
    }

    public synchronized void addParticipant(Participant p) {
        participants.add(p);
    }

    public synchronized ArrayList<Participant> getParticipants() {
        return participants;
    }

    public synchronized Participant getParticipant(String name) {
        for (Participant p : participants) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public synchronized Participant getParticipant(Socket s) {
        for (Participant p : participants) {
            if (p.getSocket().equals(s)) {
                return p;
            }
        }
        return null;
    }

    public synchronized Call getCall(int id) {
        for (Call c : activeCalls) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public synchronized void connectClient(Action action, Socket socket) {
        participants.add(new Participant(action.getSender(), socket));
        System.out.println("Client connected: " + action.getSender());
        System.out.println(participants.size());
    }

    public void disconnectClient(Action action) {
        participants.remove(getParticipant(action.getSender()));
    }

    public synchronized ArrayList<Participant> getCallParticipants(ArrayList<String> callList) {
        ArrayList<Participant> participantToCall = new ArrayList<Participant>();
        for (String name : callList) {
            for (Participant p : participants) {
                if (name.equals(p.getName())) {
                    participantToCall.add(p);
                }
            }
        }
        return participantToCall;
    }

    public synchronized void sendToCall(Action action) {
        for (Participant p : getCall(action.getCallID()).getAcceptedCallList()) {
            try {
                p.getSocket().getOutputStream().write(action.getContent().getBytes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public synchronized void requestCall(Action action) {
        ArrayList<Participant> activeCallList = new ArrayList<Participant>();
        activeCallList.add(getParticipant(action.getSender()));
        Call c = new Call(activeCallList, uniqueID++);
        activeCalls.add(c);
        Action reqAction = new Action(action.getContent(), action.getSender(), action.getCmd(), c.getID());
        for (Participant p : getCallParticipants(action.getCallList())) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(p.getSocket().getOutputStream());
                oos.writeObject(reqAction);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void acceptCall(Action action) {
        Call actualCall = getCall(action.getCallID());
        ArrayList<Participant> callList = actualCall.getAcceptedCallList();

        for (Participant p : actualCall.getInvitedParticipants()) {
            if (p.getName().equals(action.getSender())) {
                if (getCall(action.getCallID()).getAcceptedCallList().size() > 0) {
                    for (Participant acceptP : getCall(action.getCallID()).getAcceptedCallList()) {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(p.getSocket().getOutputStream());
                            oos.writeObject(action);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                getCall(action.getCallID()).getAcceptedCallList().add(p);
            }
        }
    }

    public synchronized void closeCall(Action action) {
        Call call = getCall(action.getCallID());

        for (Participant p : call.getAcceptedCallList()) {
            if (p.getName().equals(action.getSender())) {
                call.getAcceptedCallList().remove(p);
                    /*
                    Här måste vi hantera de "participants" som ännu inte har accepterat eller nekat detta samtal
                     */
            }
        }
    }


    public synchronized void closeConnection(Action action) {
        for (Participant p : participants) {
            if (p.getName().equals(action.getSender())) {
                participants.remove(p);
            }
        }
    }

    public void rejectCall(Action action) {
        Call actualCall = getCall(action.getCallID());
        ArrayList<Participant> callList = actualCall.getAcceptedCallList();

        for (Participant p : callList) {
            if (p.getName().equals(action.getSender())) {
                if (callList.size() > 0) {
                    for (Participant acceptP : callList) {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(p.getSocket().getOutputStream());
                            oos.writeObject(action);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                callList.add(p);
            }
        }
    }
}

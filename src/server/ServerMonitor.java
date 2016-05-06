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

    private final int CONNECT = 0;
    private final int DISCONNECT = 1;
    private final int INITIATE_CALL = 2;
    private final int ACCEPT_CALL = 3;
    private final int CLOSE_CALL = 4;
    private final int COMMUNICATE_TO_CALL = 5;
    private final int RECIEVE_REQUESTED_CALL = 6;
    private final int REJECT_CALL = 7;


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

    public synchronized void disconnectClient(Action action) {
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
        System.out.println("Got to requestCall");
        ArrayList<Participant> activeCallList = new ArrayList<Participant>();
        activeCallList.add(getParticipant(action.getSender()));
        ArrayList<Participant> invited = new ArrayList<Participant>();
        for (String p : action.getCallList()) {
            invited.add(getParticipant(p));
        }
        Call c = new Call(invited, uniqueID++);
        activeCalls.add(c);
        c.getAcceptedCallList().add(getParticipant(action.getSender()));
        Action reqAction = new Action(action.getContent(), action.getSender(), RECIEVE_REQUESTED_CALL, c.getID());
        System.out.println(reqAction.getContent() + " " + reqAction.getSender() + " " + reqAction.getCmd() + " " + reqAction.getCallID());
        for (Participant p : getCallParticipants(action.getCallList())) {
            System.out.println("Found ho/ha");
            try {
                ObjectOutputStream oos = new ObjectOutputStream(p.getSocket().getOutputStream());
                oos.writeObject(reqAction);
                System.out.println("Wrote object to them");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("Duplicates")
    public synchronized void acceptCall(Action action) {
        Call actualCall = getCall(action.getCallID());

        for (Participant p : actualCall.getInvitedParticipants()) {
            if (p.getName().equals(action.getSender())) {
                getCall(action.getCallID()).getAcceptedCallList().add(p);
                getCall(action.getCallID()).getInvitedParticipants().remove(p);
                if (getCall(action.getCallID()).getAcceptedCallList().size() > 0) {
                    for (Participant acceptP : getCall(action.getCallID()).getAcceptedCallList()) {
                        if (!acceptP.getName().equals(action.getSender())) {
                            try {
                                System.out.println(action.getSender() + " " + action.getCmd());
                                ObjectOutputStream oos = new ObjectOutputStream(acceptP.getSocket().getOutputStream());
                                oos.writeObject(action);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void rejectCall(Action action) {
        Call actualCall = getCall(action.getCallID());

        for (Participant p : actualCall.getInvitedParticipants()) {
            if (p.getName().equals(action.getSender())) {
                getCall(action.getCallID()).getInvitedParticipants().remove(p);
                if (getCall(action.getCallID()).getAcceptedCallList().size() > 0) {
                    for (Participant acceptP : getCall(action.getCallID()).getAcceptedCallList()) {
                        if (!acceptP.getName().equals(action.getSender())) {
                            try {
                                System.out.println(action.getSender() + " " + action.getCmd());
                                ObjectOutputStream oos = new ObjectOutputStream(acceptP.getSocket().getOutputStream());
                                oos.writeObject(action);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
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


}

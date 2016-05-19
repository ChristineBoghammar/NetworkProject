package server;

import protocol.Action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by christineboghammar on 18/04/16.
 */
public class ServerMonitor {
    private LinkedList<Action> actions;
    private ArrayList<Participant> participants;
    private ArrayList<Call> activeCalls;
    private int uniqueID = 0;
    private HashMap<Integer, Long> callSendAudioInterval;
    private HashMap<Integer, ArrayList<Action>> audioToSend;
    private HashMap<Integer, Boolean> sentAudio;

    private final int CONNECT = 0;
    private final int DISCONNECT = 1;
    private final int INITIATE_CALL = 2;
    private final int ACCEPT_CALL = 3;
    private final int CLOSE_CALL = 4;
    private final int COMMUNICATE_TO_CALL = 5;
    private final int RECIEVE_REQUESTED_CALL = 6;
    private final int REJECT_CALL = 7;
    private final int RECIEVE_CLOSE_CALL = 8;
    private final int RECIEVE_CALL_ID = 9;
    private final int RECIEVE_FROM_CALL = 10;
    private final int SEND_AUDIO_DATA = 11;
    private final int RECIEVE_AUDIO_DATA = 12;
    private final int UPDATE_CLIENT_LIST = 13;
    private final int UPDATE_CALL_LIST = 14;
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
        this.callSendAudioInterval = new HashMap<>();
        this.audioToSend = new HashMap<>();
        this.sentAudio = new HashMap<>();
    }

    /**
     * method that returns the next action to perform
     *
     * @return action
     */
    public synchronized Action getAction() {
        while (actions.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ActionList size: " + actions.size());
        return actions.poll();

    }

    /**
     * Puts an action to be performed in list with actions
     *
     * @param action
     */
    public synchronized void putMessage(Action action) {
        actions.add(action);
        notifyAll();
    }

    /**
     * @param p
     */
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

    /**
     * Connects a client to the service. Puts an action to update Conenctec contacts to every other client.
     *
     * @param action
     * @param socket
     */
    public synchronized void connectClient(Action action, Socket socket) {
        try {
            participants.add(new Participant(action.getSender(), socket));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> contacts = new ArrayList<String>();
        for (Participant p : participants) {
            contacts.add(p.getName());
        }

        Action updateListAction = new Action(null, action.getSender(), UPDATE_CLIENT_LIST, contacts);

        for (Participant p : participants) {
            try {
                p.getObjectOutputStream().writeObject(updateListAction);
                p.getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client connected: " + action.getSender());
        System.out.println(participants.size());
    }

    /**
     * A clients disconnects from the service: Removed from participants; Puts an action as an updated list with the remaining participants.
     *
     * @param action
     */
    public synchronized void disconnectClient(Action action) {
        participants.remove(getParticipant(action.getSender()));

        ArrayList<String> contacts = new ArrayList<String>();
        for (Participant p : participants) {
            contacts.add(p.getName());
        }
        Action updateListAction = new Action(null, action.getSender(), UPDATE_CLIENT_LIST, contacts);

        for (Participant p : participants) {
            try {
                p.getObjectOutputStream().writeObject(updateListAction);
                p.getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param callList
     * @return Arraylist with participants to be called
     */
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

//    public synchronized void sendToCall(Action action) {
//        Action sendAction = new Action(action.getContent(), action.getSender(), RECIEVE_FROM_CALL, action.getCallID());
//        for (Participant p : getCall(action.getCallID()).getAcceptedCallList()) {
//            if(!p.getName().equals(action.getSender()))
//            try {
//                p.getObjectOutputStream().writeObject(sendAction);
//                p.getObjectOutputStream().flush();
////                p.getSocket().getOutputStream().write(action.getContent().getBytes());
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * A request is
     *
     * @param action
     */
    public synchronized void requestCall(Action action) {
        ArrayList<Participant> invited = new ArrayList<Participant>();
        for (String p : action.getList()) {
            invited.add(getParticipant(p));
        }
        //initiates a new call and adds it to active calls.
        Call c = new Call(invited, uniqueID++);
        activeCalls.add(c);
        c.getAcceptedCallList().add(getParticipant(action.getSender()));

        callSendAudioInterval.put(c.getID(), null);
        audioToSend.put(c.getID(), new ArrayList<Action>());
        sentAudio.put(c.getID(), false);

        Action reqAction = new Action(action.getContent(), action.getSender(), RECIEVE_REQUESTED_CALL, c.getID());
        System.out.println(reqAction.getContent() + " " + reqAction.getSender() + " " + reqAction.getCmd() + " " + reqAction.getCallID());
        for (Participant p : getCallParticipants(action.getList())) {
            try {
                p.getObjectOutputStream().writeObject(reqAction);
                p.getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Action callIdAction = new Action(action.getContent(), action.getSender(), RECIEVE_CALL_ID, c.getID());
            getParticipant(action.getSender()).getObjectOutputStream().writeObject(callIdAction);
            getParticipant(action.getSender()).getObjectOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    public synchronized void acceptCall(Action action) {
        Call actualCall = getCall(action.getCallID());
        ArrayList<Participant> toRemove = new ArrayList<Participant>();
        for (Participant p : actualCall.getInvitedParticipants()) {
            if (p.getName().equals(action.getSender())) {
                actualCall.getAcceptedCallList().add(p);
                toRemove.remove(p);
                if (actualCall.getAcceptedCallList().size() > 0) {
                    for (Participant acceptP : actualCall.getAcceptedCallList()) {
                        if (!acceptP.getName().equals(action.getSender())) {
                            try {
                                System.out.println(action.getSender() + " " + action.getCmd());
                                acceptP.getObjectOutputStream().writeObject(action);


                                acceptP.getObjectOutputStream().flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        actualCall.getInvitedParticipants().removeAll(toRemove);

        for(Participant p : actualCall.getAcceptedCallList()){
            Action updateCallListAction = new Action("null", p.getName(), UPDATE_CALL_LIST, getCallList(actualCall));
            try {
                p.getObjectOutputStream().writeObject(updateCallListAction);
                p.getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getCallList(Call actualCall){
        ArrayList<String> toReturn = new ArrayList<>();
        for(Participant p : actualCall.getAcceptedCallList()){
                toReturn.add(p.getName());
        }
        return toReturn;
    }

    @SuppressWarnings("Duplicates")
    public synchronized void rejectCall(Action action) {
        Call actualCall = getCall(action.getCallID());
        ArrayList<Participant> toRemove = new ArrayList<Participant>();
        for (Participant p : actualCall.getInvitedParticipants()) {
            if (p.getName().equals(action.getSender())) {
                toRemove.add(p);
                if (actualCall.getAcceptedCallList().size() > 0) {
                    for (Participant acceptP : actualCall.getAcceptedCallList()) {
                        if (!acceptP.getName().equals(action.getSender())) {
                            try {
                                System.out.println(action.getSender() + " " + action.getCmd());
                                acceptP.getObjectOutputStream().writeObject(action);
                                acceptP.getObjectOutputStream().flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        actualCall.getInvitedParticipants().removeAll(toRemove);
    }

    @SuppressWarnings("Duplicates")
    public synchronized void closeCall(Action action) {
        Call actualCall = getCall(action.getCallID());
        boolean removedParticipant = false;
        if (actualCall.getInvitedParticipants().size() > 0) {
            if (!actualCall.getInvitedParticipants().contains(getParticipant(action.getSender()))) {
                ArrayList<Participant> toRemove = new ArrayList<Participant>();
                for (Participant p : actualCall.getAcceptedCallList()) {
                    if (p.getName().equals(action.getSender())) {
                        toRemove.add(p);
                        removedParticipant = true;
                    }

                }
                actualCall.getAcceptedCallList().removeAll(toRemove);

            }
        }
        if (removedParticipant) {
            Action closeAction = new Action(action.getContent(), action.getSender(), RECIEVE_CLOSE_CALL, action.getCallID());
            for (Participant acceptP : actualCall.getAcceptedCallList()) {
                try {
                    System.out.println(action.getSender() + " " + action.getCmd());
                    acceptP.getObjectOutputStream().writeObject(closeAction);

                    Action updateCallListAction = new Action("null", acceptP.getName(), UPDATE_CALL_LIST, getCallList(actualCall));
                    acceptP.getObjectOutputStream().writeObject(updateCallListAction);
                    acceptP.getObjectOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * Kanske blir fel att göra såhär
         */
        if((actualCall.getAcceptedCallList().size() == 0) && (actualCall.getInvitedParticipants().size() == 0)){
            activeCalls.remove(actualCall);
            callSendAudioInterval.remove(actualCall.getID());
            audioToSend.remove(actualCall.getID());
        }

//        for(Participant p : actualCall.getAcceptedCallList()){
//            Action updateCallListAction = new Action("null", p.getName(), UPDATE_CALL_LIST, getCallList(actualCall));
//            try {
//                p.getObjectOutputStream().writeObject(updateCallListAction);
//                p.getObjectOutputStream().flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private boolean sentAudioPackets = false;
    private long timeSinceSent;

    /**
     *
     **/

    @SuppressWarnings("Duplicates")
    public synchronized void sendAudio(Action action) {
        System.out.println("KOM HIT 1");
        if(!sentAudio.get(action.getCallID())){
            callSendAudioInterval.put(action.getCallID(), System.currentTimeMillis());
            System.out.println("KOM HIT 2");
        }

        audioToSend.get(action.getCallID()).add(action);

        if((System.currentTimeMillis() - timeSinceSent) > 50){
            for(Participant p : getCall(action.getCallID()).getAcceptedCallList()){
                if(participants.contains(p)){
                    System.out.println("KOM HIT 3");
                    sendAudioPacket(audioToSend.get(action.getCallID()), p, action);
                }
            }
            audioToSend.get(action.getCallID()).clear();
            sentAudio.put(action.getCallID(), false);
        }

//        Action sendAction = new Action(action.getAudioData(), action.getSender(), RECIEVE_AUDIO_DATA, action.getCallID());
//        for (Participant p : getCall(action.getCallID()).getAcceptedCallList()) {
//            if (!p.getName().equals(action.getSender()) && participants.contains(p)) {
//                try {
//                    p.getObjectOutputStream().writeObject(sendAction);
//                    p.getObjectOutputStream().flush();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private  void sendAudioPacket(ArrayList<Action> audioToSend, Participant p, Action action) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for(Action a : audioToSend){
            if(a.getSender().equals(p.getName())){
                try {
                    outputStream.write(action.getAudioData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Action sendAction = new Action(outputStream.toByteArray(), action.getSender(), RECIEVE_AUDIO_DATA, action.getCallID());

        try {
            p.getObjectOutputStream().writeObject(sendAction);
            p.getObjectOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the disconnected client and updated the remaining clients with a list of connected clients.
     * @param socket
     */
    public synchronized void disconnectClient(Socket socket) {
        String disconnectedClient = "";
        ArrayList<String> contacts = new ArrayList<String>();
        ArrayList<Participant> toRemove = new ArrayList<>();
        for (Participant p : participants) {
            if (p.getSocket().equals(socket)) {
                toRemove.add(p);
                disconnectedClient = p.getName();
            } else {
                contacts.add(p.getName());
            }
        }
        participants.removeAll(toRemove);

        Action updateListAction = new Action(null, disconnectedClient, UPDATE_CLIENT_LIST, contacts);
        for (Participant p : participants) {
            try {
                p.getObjectOutputStream().writeObject(updateListAction);
                p.getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
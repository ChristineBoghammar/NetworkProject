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
public class Monitor {
    private LinkedList<Action> actions;
    private ArrayList<Participant> participants;
    private ArrayList<Call> activeCalls;
    private int uniqueID = 0;

    public Monitor() {
        this.actions = new LinkedList<Action>();
        this.participants = new ArrayList<Participant>();

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

    public synchronized void putMessage(Action action){
        actions.add(action);
    }

    public synchronized void addParticipant(Participant p){
        participants.add(p);
    }

    public synchronized  ArrayList<Participant> getParticipants(){
        return participants;
    }

    public synchronized  Participant getParticipant(String name){
        for(Participant p : participants){
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public synchronized Call getCall(int id){
        for(Call c : activeCalls){
            if(c.getID() == id){
                return c;
            }
        }
        return null;
    }

    public synchronized void connectClient(Action action, Socket socket){
        participants.add(new Participant(action.getSender(), socket));
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
        for (Participant p : getCall(action.getCallID()).getParticipants()) {
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
        activeCalls.add(new Call(activeCallList, uniqueID++));
        //Här måste ett Call object med ett motsvarande action object skapas för att skickas ut till "callList"
        for (Participant p : getCallParticipants(action.getCallList())) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(p.getSocket().getOutputStream());
                oos.writeObject(action);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void acceptCall(Action action) {
        for(Participant p : getCall(action.getCallID()).getParticipants()){
            if(p.getName().equals(action.getSender())){
                getCall(action.getCallID()).getAcceptedCallList().add(p);
            }
        }
    }

    public synchronized void closeCall(Action action) {
        for (Call c : activeCalls) {
            for(Participant p : c.getParticipants()){
                if(p.getName().equals(action.getSender())){
                    c.getParticipants().remove(p);
                }
            }

            for(Participant p : c.getAcceptedCallList()){
                if(p.getName().equals(action.getSender())){
                    c.getAcceptedCallList().remove(p);
                }
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

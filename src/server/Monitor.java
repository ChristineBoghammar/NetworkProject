package server;

import protocol.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by christineboghammar on 18/04/16.
 */
public class Monitor {
    private LinkedList<Message> messages;
    private ArrayList<Participant> participants;
    private ArrayList<Call> activeCalls;

    public Monitor() {
        this.messages = new LinkedList<Message>();
        this.participants = new ArrayList<Participant>();

    }


    public Message getMessage() {
        while (messages.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messages.poll();

    }

    public void putMessage(Message msg){
        messages.add(msg);
    }


    public ArrayList<Participant> getCallParticipants(ArrayList<String> callList) {
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

    public synchronized void sendToCall(String content, Call c) {
        for (Participant p : c.getParticipants()) {
            try {
                p.getSocket().getOutputStream().write(content.getBytes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void requestCall(Message msg) {

        for (Participant p : getCallParticipants(msg.getCallList())) {
            try {
                p.getSocket().getOutputStream().write(msg.getCmd());
                p.getSocket().getOutputStream().write(msg.getSender().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void acceptCall(Message msg) {
        for(Participant p : msg.getCall().getParticipants()){
            if(p.getName().equals(msg.getSender())){
                msg.getCall().getAcceptedCallList().add(p);
            }
        }
    }

    public void closeCall(Message msg) {
        for (Call c : activeCalls) {
            for(Participant p : c.getParticipants()){
                if(p.getName().equals(msg.getSender())){
                    c.getParticipants().remove(p);
                }
            }

            for(Participant p : c.getAcceptedCallList()){
                if(p.getName().equals(msg.getSender())){
                    c.getAcceptedCallList().remove(p);
                }
            }

        }
    }



    public void closeConnection(Message msg) {
        for (Participant p : participants) {
            if (p.getName().equals(msg.getSender())) {
                participants.remove(p);
            }
        }
    }

}

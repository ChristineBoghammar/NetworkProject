package client;

import javafx.application.Platform;
import protocol.Action;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientMonitor {
    private LinkedList<Action> actions;
    private String name;
    private Socket socket;
    private ArrayList<String> callList;
    private int callID;
    private ObjectOutputStream oos;
    private AudioInputStream ais;
    private OutputStream os;
    private AudioFormat format;
    private SourceDataLine speaker;
    private AudioWriter aw = null;
    private List<String> connectedClients;
    private ClientGUIController cgc;
    private ActiveCallGUIController agc;
    private ClientGUI gui;
    private ArrayList<String> senders;
    private ArrayList<AudioMonitor> audioReceivers;

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

    public ClientMonitor(String name, Socket s, ClientGUIController cgc, ActiveCallGUIController agc, ClientGUI gui) {
        this.actions = new LinkedList<Action>();
        this.name = name;
        this.socket = s;
        this.cgc = cgc;
        this.agc = agc;
        this.gui = gui;
        this.callID = -1;
        this.callList = new ArrayList<String>();
        this.senders = new ArrayList<String>();
        this.audioReceivers = new ArrayList<AudioMonitor>();
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
            os = s.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.format = getAudioFormat();
        this.speaker = null;
        cgc.setLabelName(name);
    }

    public synchronized ArrayList<String> getCallList() {
        return callList;
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
        System.out.println("Monitor storlek: " + actions.size());
        return actions.poll();

    }

    /**
     * Puts an action into the lists of actions. Notifies the clients that an action is to be handled.
     *
     * @param action
     */
    public synchronized void putAction(Action action) {
        actions.add(action);
        System.out.println("PutAction: " + "cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallID());
        notifyAll();
    }

    @SuppressWarnings("Duplicates")
    public synchronized void requestCall(Action action) {

        DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
        try {
            speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            System.out.println(speaker.toString());
            speaker.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        speaker.start(); // Möjligen här buggen med speaker ligger?
        try {
            oos.writeObject(action);
            System.out.println("RequestCall Action written to server");
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getList());
    }

    synchronized void acceptCall(Action action) {
//        boolean audioMonExists = false;
//        for(AudioMonitor am : audioReceivers){
//            if(am.getAudioSender().equals(action.getSender())){
//                audioMonExists = true;
//            }
//        }
//        if(!audioMonExists){
//            audioReceivers.add(new AudioMonitor(action.getSender(), speaker));
//        }

        if (aw == null) {
            aw = new AudioWriter(this);
            aw.start();
        }

        Runnable runnable = () -> {
            try {
                gui.activateCall();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        FutureTask<Void> task = new FutureTask<>(runnable, null);
        Platform.runLater(task);
        try {
            task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        System.out.println(action.getSender() + " Has accepted the call");
    }

    synchronized void rejectCall(Action action) {
        if (action.getContent().equals("n")) {
            System.out.println(action.getSender() + " Has rejected the call");
        } else if (action.getContent().equals("b")) {
            System.out.println(action.getSender() + " is busy in another call");
        } else {
            System.out.println("Unknown call rejection from " + action.getSender());
        }
    }


    /**
     * Sends an action to the corresponding call through the server.
     *
     * @param action
     */
    public synchronized void sendToCall(Action action) {
        try {
            oos.writeObject(action);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects a client by sending an action to the server.
     *
     * @param action
     */
    public synchronized void connectClient(Action action) {
        try {
            oos.writeObject(action);
            oos.flush();
            System.out.println("connectClient Action written to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles a request whether the client is busy, accept or rejects the call.
     *
     * @param action
     */
    boolean accept = false;


    public synchronized void receiveRequest(Action action) {
        accept = false;
        if (callID == -1) {
            Runnable runnable = () -> {
                try {
                    accept = gui.incomingCall(action.getSender());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            runGUIUpdate(runnable);
            if (accept) {
                System.out.println("Accepterade samtalet");
                DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
                try {
                    speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
                    System.out.println(speaker.toString());
                    speaker.open(format);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                speaker.start();

                boolean audioMonExists = false;
                for(AudioMonitor am : audioReceivers){
                    if(am.getAudioSender().equals(action.getSender())){
                        audioMonExists = true;
                    }
                }
                if(!audioMonExists){
                    audioReceivers.add(new AudioMonitor(action.getSender(), speaker));
                }

                callID = action.getCallID();
                Action response = new Action("y", getName(), ACCEPT_CALL, action.getCallID());
                try {
                    oos.writeObject(response);
                    oos.flush();
                    System.out.println("receiveRequest Action written to server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                aw = new AudioWriter(this);
                aw.start();

            } else {
                Action response = new Action("n", getName(), REJECT_CALL, action.getCallID());
                try {
                    oos.writeObject(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Action response = new Action("b", getName(), REJECT_CALL, action.getCallID());
            try {
                oos.writeObject(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @SuppressWarnings("Duplicates")
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }

    /**
     * Closes a call by shutting it down, sending it to the server.
     */

    public synchronized void closeCall() {
        for(AudioMonitor am : audioReceivers){
            am.setReceiving(false);
        }
        audioReceivers.clear();
        Action closeAction = new Action("content", getName(), CLOSE_CALL, callID);
        callID = -1;
        try {
            oos.writeObject(closeAction);
            oos.flush();
            System.out.println("CloseCall action written to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        speaker.drain();
        speaker.close();
    }

    /**
     * Updates that a call is ended.. GUI
     *
     * @param action
     */
    public synchronized void receiveCloseCall(Action action) {
        ArrayList<AudioMonitor> toRemove = new ArrayList<AudioMonitor>();
        for(AudioMonitor am : audioReceivers){
            if(am.getAudioSender().equals(action.getSender())){
                am.setReceiving(false);
                toRemove.add(am);

            }
        }
        audioReceivers.removeAll(toRemove);
        System.out.println(action.getSender() + " Has left the call");
    }

    /**
     * collects the id of the Call.
     *
     * @param action
     */
    public synchronized void receiveCallID(Action action) {
        callID = action.getCallID();
        System.out.println("CallID is :" + action.getCallID());
    }

    public synchronized void receiveFromCall(Action action) {
        System.out.println(action.getSender() + ": " + action.getContent());
    }

    public synchronized void sendAudioData(Action action) {
        try {
            oos.writeObject(action);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param action
     */
    public synchronized void receiveAudioData(Action action) {

        byte[] data = action.getAudioData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, data.length);
        int bytesRead;
        try {
            if ((bytesRead = ais.read(data)) != -1) {
                System.out.println("Writing to audio output.");
                speaker.write(data, 0, bytesRead);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ais.close();
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public synchronized void receiveAudioData(Action action) {

//        String sender = action.getSender();
//        for(String s : senders) {
//            if (!sender.equals(s)) {
//                AudioReader ar = new AudioReader(sender, action, format, speaker);
//                ar.start();
//                senders.add(sender);
//
//            }
//        }
//        AudioReader ar = new AudioReader(sender, action, format, speaker);
//        ar.start();
//
//
//
//    }


    public void runGUIUpdate(Runnable runnable){
        FutureTask<Void> task = new FutureTask<>(runnable, null);
        Platform.runLater(task);
        try {
            task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collects all the connected clients and removes the current client to show the rest in GUI.
     *
     * @param action, with connected clients
     */
    public synchronized void updateContactList(Action action) {
        //gets the list of conected clients
        ArrayList<String> updatedContactList = action.getList();

        //(List) with one element being the curent client
        ArrayList<String> toRemove = new ArrayList<String>();
        for (String contact : updatedContactList) {
            if (contact.equals(this.getName())) {
                toRemove.add(contact);
            }
        }
        updatedContactList.removeAll(toRemove);
        Runnable runnable = () -> {
            cgc.updateContactList(updatedContactList);
        };
        runGUIUpdate(runnable);
    }

    public synchronized void updateCallList(Action action) {
        ArrayList<String> updatedCallList = action.getList();

        ArrayList<String> toRemove = new ArrayList<String>();
        for (String contact : updatedCallList) {
            if (contact.equals(this.getName())) {
                toRemove.add(contact);
            }
        }
        updatedCallList.removeAll(toRemove);
        Runnable runnable = () -> {
            agc.updateConnectedParticipants(updatedCallList);
        };
        runGUIUpdate(runnable);
    }

    public ArrayList<AudioMonitor> getAudioReceivers() {
        return audioReceivers;
    }
}


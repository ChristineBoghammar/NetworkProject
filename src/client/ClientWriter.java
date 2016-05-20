package client;

import protocol.Action;

import java.net.Socket;

/**
 * Created by johan on 2016-05-04.
 */
public class ClientWriter extends Thread {
    private Socket socket;
    private ClientMonitor mon;


    public ClientWriter(ClientMonitor mon, Socket socket){
        this.socket = socket;
        this.mon = mon;
    }

    private final int CONNECT = 0;
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
    private final int RECEIVE_MESSAGE = 15;
    private final int SEND_AUDIO_MESSAGE = 16;
    private final int RECEIVE_AUDIO_MESSAGE = 17;




    public void run(){
        while (true) {
            Action action = mon.getAction();
            System.out.println("Action polled: " + action.getSender() + " " + action.getCmd());

            switch (action.getCmd()) {

                case CONNECT:
                    mon.connectClient(action);
                    break;

                case INITIATE_CALL:
                    mon.requestCall(action);
                    break;

                case ACCEPT_CALL:
                    mon.acceptCall(action);
                    break;

                case CLOSE_CALL:
                    mon.closeCall();
                    break;

                case COMMUNICATE_TO_CALL:
                    mon.sendToCall(action);
                    break;

                case RECIEVE_REQUESTED_CALL:
                    mon.receiveRequest(action);
                    break;

                case REJECT_CALL:
                    mon.rejectCall(action);
                    break;

                case RECIEVE_CLOSE_CALL:
                    mon.receiveCloseCall(action);
                    break;

                case RECIEVE_CALL_ID:
                    mon.receiveCallID(action);
                    break;

                case RECIEVE_FROM_CALL:
                    mon.receiveFromCall(action);
                    break;

                case SEND_AUDIO_DATA:
                    mon.sendAudioData(action);
                    break;

                case RECIEVE_AUDIO_DATA:
                    mon.receiveAudioData(action);
                    break;

                case UPDATE_CLIENT_LIST:
                    mon.updateContactList(action);
                    break;
                case UPDATE_CALL_LIST:
                    mon.updateCallList(action);
                    break;
                case RECEIVE_MESSAGE:
                    mon.receiveMessage(action);
                    break;
                case RECEIVE_AUDIO_MESSAGE:
                    mon.receiveAudioMessage(action);
            }
        }
    }


}

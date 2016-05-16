package server;

import protocol.Action;

import java.net.Socket;

/**
 * Created by johan on 2016-04-28.
 */
public class ServerWriter extends Thread  {
    private ServerMonitor mon;
    private Socket socket;

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


    public ServerWriter(ServerMonitor mon, Socket socket){
        this.mon = mon;
        this.socket = socket;
    }

    public void run(){
        while (true) {
            Action action = mon.getAction();
            switch (action.getCmd()) {
                case CONNECT:
                    mon.connectClient(action, socket);
                    break;
                case DISCONNECT:
                    mon.disconnectClient(action);
                    break;
                case INITIATE_CALL:
                    mon.requestCall(action);
                    break;

                case ACCEPT_CALL:
                    mon.acceptCall(action);
                    break;
                case CLOSE_CALL:
                    mon.closeCall(action);
                    break;
                case COMMUNICATE_TO_CALL:
//                    mon.sendToCall(action);
                    break;
                case RECIEVE_REQUESTED_CALL:

                    break;
                case REJECT_CALL:
                    mon.rejectCall(action);
                    break;
                case SEND_AUDIO_DATA:
                    mon.sendAudio(action);
                    break;

            }
        }
    }

}

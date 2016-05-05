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

    public void run(){
        while (true) {
            Action action = mon.getAction();
            System.out.println("Action polled: " + action.getSender() + " " + action.getSender());

            switch (action.getCmd()) {
                case 0:
                    mon.connectClient(action);
                    break;
                case 1:

                    break;
                case 2:
                    mon.requestCall(action);
                    break;

                case 3:
                    mon.acceptCall(action);
                    break;
                case 4:
                    mon.rejectCall(action);
                    break;
                case 5:
                    mon.sendToCall(action);
                    break;
                case 6:
                    mon.recieveRequest(action);
                    break;
            }
        }
    }


}

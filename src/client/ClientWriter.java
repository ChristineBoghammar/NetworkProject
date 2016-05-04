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
            switch (action.getCmd()) {
                case 0:

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
//                    mon.closeCall(action);
                    break;
                case 5:
//                    mon.sendToCall(action);
            }
        }
    }


}

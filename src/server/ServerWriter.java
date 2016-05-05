package server;

import protocol.Action;

import java.net.Socket;

/**
 * Created by johan on 2016-04-28.
 */
public class ServerWriter extends Thread  {
    private ServerMonitor mon;
    private Socket socket;

    public ServerWriter(ServerMonitor mon, Socket socket){
        this.mon = mon;
        this.socket = socket;
    }

    public void run(){
        while (true) {
            Action action = mon.getMessage();
            switch (action.getCmd()) {
                case 0:
                    mon.connectClient(action, socket);
                    break;
                case 1:
                    mon.disconnectClient(action);
                    break;
                case 2:
                    mon.requestCall(action);
                    break;

                case 3:
                    mon.acceptCall(action);
                    break;
                case 4:
                    mon.closeCall(action);
                    break;
                case 5:
                    mon.sendToCall(action);
                    break;
                case 6:
                    mon.rejectCall(action);
            }
        }
    }

}

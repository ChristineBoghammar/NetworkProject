package server;

import client.ClientInfo;
import protocol.Message;

/**
 * Created by christineboghammar on 18/04/16.
 */


public class ServerReader extends Thread {
    private Monitor mon;
    private ClientInfo info;

    public ServerReader(Monitor mon, ClientInfo info) {
        this.mon = mon;
        this.info = info;
    }

    public void run() {
        while (true) {
            Message msg = mon.getMessage();
            switch (msg.getCmd()) {
                case 0:
                    mon.requestCall(msg);
                    break;
                case 1:
                    mon.acceptCall(msg);
                    break;
                case 2:
                    mon.closeCall(msg);
                    break;

                case 3:
//                    mon.sendToCall(msg.getMsg(), );
                    break;

                case 4:
                    mon.closeConnection();
            }
        }
    }
}
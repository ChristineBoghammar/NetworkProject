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
        //Kolla på Client ur labb 3. Ska skapa meddelanden för utskick till monitor
    }
}
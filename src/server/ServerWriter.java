package server;

import client.ClientInfo;
import com.sun.corba.se.spi.activation.Server;
import protocol.Message;

/**
 * Created by johan on 2016-04-28.
 */
public class ServerWriter extends Thread  {
    private Monitor mon;
    private ClientInfo info;

    public ServerWriter(Monitor mon, ClientInfo info){
        this.mon = mon;
        this.info = info;
    }

    public void run(){
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
                    mon.sendToCall(msg.getMsg(), info.getCall());
                    break;

                case 4:
                    mon.closeConnection(msg);
            }
        }
    }

}

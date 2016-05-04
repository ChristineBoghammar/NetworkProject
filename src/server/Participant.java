package server;

import java.net.Socket;

/**
 * Created by johan on 2016-04-28.
 */
public class Participant {
    private String name;
    private Call call;
    private Socket s;

    public Participant(String name, Socket s){
        this.name = name;
        this.s = s;
    }

    public String getName(){
        return name;
    }

    public Socket getSocket(){
        return s;
    }

    public void setCall(Call call){
        this.call = call;
    }

    public void endCall(){
        this.call = null;
    }
}

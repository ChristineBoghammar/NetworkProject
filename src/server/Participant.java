package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by johan on 2016-04-28.
 */
public class Participant {
    private String name;
    private Call call;
    private Socket s;
    private ObjectOutputStream oos;

    public Participant(String name, Socket s) throws IOException {
        this.name = name;
        this.s = s;
        oos = new ObjectOutputStream(s.getOutputStream());
    }

    public String getName(){
        return name;
    }

    public Socket getSocket(){
        return s;
    }

    public ObjectOutputStream getObjectOutputStream(){
        return oos;
    }

    public void setCall(Call call){
        this.call = call;
    }

    public void endCall(){
        this.call = null;
    }
}

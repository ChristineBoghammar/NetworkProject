package client;

import server.Call;

import java.net.Socket;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientInfo {
    private String name;
    private Call call;
    private Socket socket;

    public ClientInfo(String name, Socket s){
        this.name = name;
        this.socket = s;
        call = null;
    }


    public void setCall(Call callID){
        call = callID;
    }

    public Call getCall(){
        return call;
    }

    public void destroyCall(){
        call = null;
    }

    public Socket getSocket(){
        return socket;
    }

    public String getName(){
        return name;
    }
}

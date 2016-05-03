package client;

import server.Call;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientInfo {
    private String name;
    private Call call;

    public ClientInfo(String name){
        this.name = name;
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

    public String getName(){
        return name;
    }
}

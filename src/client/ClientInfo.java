package client;

/**
 * Created by johan on 2016-04-28.
 */
public class ClientInfo {
    private String name;
    private boolean activeCall;

    public ClientInfo(String name){
        this.name = name;
        activeCall = false;
    }

    public void setCallStatus(boolean bol){
        activeCall = bol;
    }

    public boolean getCallStatus(){
        return activeCall;
    }

    public String getName(){
        return name;
    }
}

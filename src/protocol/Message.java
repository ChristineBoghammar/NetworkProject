package protocol;

import server.Call;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Message {
    private String msg;
    private int cmd;
    private Call call;
    private String sender;
    private ArrayList<String> peopleToCall;

    /**
     * Possible cmd's are:
     * 0 - Initiate call
     * 1 - Accept call
     * 2 - Close call
     * 3 - Communication via call
     *
     */
    public Message(String msg, String sender, int cmd, Call call){
        this.msg=msg;
        this.sender=sender;
        this.cmd=cmd;
        this.call=call;
    }

    public Message(String msg, String sender, int cmd, ArrayList<String> peopleToCall){
        this.msg = msg;
        this.sender = sender;
        this.cmd = cmd;
        this.peopleToCall = peopleToCall;
    }



    public String getMsg(){
        return msg;
    }

    public int getCmd(){
        return cmd;
    }

    public String getSender(){
        return sender;
    }

    public Call getCall(){
        return call;
    }

    public ArrayList<String> getCallList(){
        return peopleToCall;
    }
}

package protocol;

import server.Call;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Action implements Serializable {
    private String content;
    private int cmd;
    private int callID;
    private String sender;
    private ArrayList<String> peopleToCall;
    private byte[] audioData;

    /**
     * Possible cmd's are:
     * 0 - Connect to server
     * 1 - Disconnect from server
     * 2 - Initiate call
     * 3 - Accept call
     * 4 - Close call
     * 5 - Communication via call
     * 6 - Receive requested Call
     * 7 - Deny Call
     */
    public Action(String content, String sender, int cmd, int callID) {
        this.content = content;
        this.sender = sender;
        this.cmd = cmd;
        this.callID = callID;
    }

    public Action(String content, String sender, int cmd, ArrayList<String> peopleToCall) {
        this.content = content;
        this.sender = sender;
        this.cmd = cmd;
        this.peopleToCall = peopleToCall;
    }

    public Action(byte[] audioData, String sender, int cmd, int callID) {
        this.audioData = audioData;
        this.sender = sender;
        this.cmd = cmd;
        this.callID = callID;
    }


    public String getContent() {
        return content;
    }

    public int getCmd() {
        return cmd;
    }

    public String getSender() {
        return sender;
    }

    public int getCallID() {
        return callID;
    }

    public ArrayList<String> getToCallList() {
        return peopleToCall;
    }

    public byte[] getAudioData() {
        return audioData;
    }
}

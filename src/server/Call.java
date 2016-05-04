package server;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Call implements Serializable {
    private int nbrParticipants;
    private ArrayList<Participant> participants;
    private ArrayList<Participant> acceptedCallList;
    private int id;

    public Call(ArrayList<Participant> participants, int id){
        this.participants = participants;
        this.id = id;
        acceptedCallList = new ArrayList<Participant>();
    }

    public int getID(){
        return id;
    }

    public ArrayList<Participant> getParticipants(){
        return participants;
    }

    public ArrayList<Participant> getAcceptedCallList(){
        return acceptedCallList;
    }
}

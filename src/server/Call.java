package server;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Call {
    private int nbrParticipants;
    private ArrayList<Participant> participants;
    private ArrayList<Participant> acceptedCallList;

    public Call(ArrayList<Participant> participants){
        this.participants = participants;
        acceptedCallList = new ArrayList<Participant>();
    }

    public ArrayList<Participant> getParticipants(){
        return participants;
    }

    public ArrayList<Participant> getAcceptedCallList(){
        return acceptedCallList;
    }
}

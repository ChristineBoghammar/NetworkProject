package server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Call implements Serializable {
    private ArrayList<Participant> invitedParticipants;
    private ArrayList<Participant> acceptedCallList;

    private int id;

    public Call(ArrayList<Participant> participants, int id){
        this.invitedParticipants = participants;
        this.id = id;
        acceptedCallList = new ArrayList<Participant>();
    }

    public int getID(){
        return id;
    }

    public ArrayList<Participant> getInvitedParticipants(){
        return invitedParticipants;
    }

    public ArrayList<Participant> getAcceptedCallList(){
        return acceptedCallList;
    }
}

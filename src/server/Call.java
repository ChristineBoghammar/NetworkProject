package server;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by johan on 2016-04-28.
 */
public class Call {
    private int nbrParticipants;
    private ArrayList<Participant> participants;
    private ArrayList<Participant> nbrAcceptedCall;

    public Call(ArrayList<Participant> participants){
        this.participants = participants;
    }

    public ArrayList<Participant> getParticipants(){
        return participants;
    }

}

package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import protocol.Action;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by johan on 2016-05-16.
 */
public class ClientGUIController implements Initializable {
    private final int CONNECT = 0;
    private final int DISCONNECT = 1;
    private final int INITIATE_CALL = 2;
    private final int ACCEPT_CALL = 3;
    private final int CLOSE_CALL = 4;
    private final int COMMUNICATE_TO_CALL = 5;
    private final int RECIEVE_REQUESTED_CALL = 6;
    private final int REJECT_CALL = 7;
    private final int RECIEVE_CLOSE_CALL = 8;
    private final int RECIEVE_CALL_ID = 9;
    private final int RECIEVE_FROM_CALL = 10;
    private final int SEND_AUDIO_DATA = 11;
    private final int RECIEVE_AUDIO_DATA = 12;


    private ClientMonitor mon;
    private ClientGUI gui;

    private ArrayList<String> callList;

    @FXML
    private Button callButton;

    @FXML
    private ListView<String> connectedContacts;



    public void setMonitor(ClientMonitor mon){
        this.mon = mon;
    }

    public void setGUI(ClientGUI gui){
        this.gui = gui;
    }


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert callButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'startGui.fxml'.";
        assert connectedContacts != null : "fx:id=\"connectedContacts\" was not injected: check your FXML file 'startGui.fxml'.";

        callList = new ArrayList<String>();
        System.out.println("Initialized controller");

        callButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> callList = mon.getCallList();
                System.out.println("Tryckte på callButton");
                if(callList.size() > 0){
                    mon.putAction(new Action(null, mon.getName(), INITIATE_CALL, callList));
                } else {
                    System.out.println("No users selected for call");
                }
            }
        });

        connectedContacts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        connectedContacts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                callList.addAll(connectedContacts.getSelectionModel().getSelectedItems());
            }
        });

    }

    public void updateContactList(ArrayList<String> updatedList) {
        for(String participant : updatedList){
            if(!connectedContacts.getItems().contains(participant)){
                connectedContacts.getItems().add(participant);
            }
        }
    }
}

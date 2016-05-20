package client;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import protocol.Action;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 * Created by johan on 2016-05-16.
 */
public class ActiveCallGUIController implements Initializable {
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

    @FXML
    private Button disconnectButton;

    @FXML
    private ListView connectedParticipants;

    @FXML
    private Button sendButton;

    @FXML
    private ListView chatListView;

    @FXML
    private TextField chatTextField;


    public void setMonitor(ClientMonitor mon) {
        this.mon = mon;
    }

    public void setGUI(ClientGUI gui) {
        this.gui = gui;
    }


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert disconnectButton != null : "fx:id=\"disconnectButton\" was not injected: check your FXML file 'activateGui.fxml'.";
        assert connectedParticipants != null : "fx:id=\"connectedParticipants\" was not injected: check your FXML file 'activateGui.fxml'.";
        assert chatListView != null : "fx:id=\"connectedParticipants\" was not injected: check your FXML file 'activateGui.fxml'.";
        assert chatListView != null : "fx:id=\"connectedParticipants\" was not injected: check your FXML file 'activateGui.fxml'.";
        assert sendButton != null : "fx:id=\"connectedParticipants\" was not injected: check your FXML file 'activateGui.fxml'.";

        System.out.println("Initialized 'active' controller");
//        connectedParticipants.getItems().add("Connected participants:");

        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    gui.startScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mon.putAction(new Action("null", mon.getName(), CLOSE_CALL, mon.getCallID()));
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = chatTextField.getText();
                if(!message.isEmpty()){
                    mon.putAction(new Action(message, mon.getName(), COMMUNICATE_TO_CALL, mon.getCallID()));
                    chatTextField.clear();
                }
            }
        });
    }

    public void updateConnectedParticipants(ArrayList<String> updatedList){
        connectedParticipants.getItems().clear();
        connectedParticipants.getItems().add("Connected participants:");
        System.out.println(updatedList.toString());
        for(String s : updatedList){
            connectedParticipants.getItems().add(s);
        }
        System.out.println("Att skriva på listview: " + connectedParticipants.getItems().toString());
    }

    /**
     * Display a new message in chat within the call.
     * @param message
     */
    public void displayChat(String message){
        chatListView.getItems().add(message);
    }

}

package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import protocol.Action;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 * Created by johan on 2016-05-16.
 */
public class IncomingGUIController implements Initializable {
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
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    public void setMonitor(ClientMonitor mon) {
        this.mon = mon;
    }

    public void setGUI(ClientGUI gui) {
        this.gui = gui;
    }


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert acceptButton != null : "fx:id=\"acceptButton\" was not injected: check your FXML file 'startGui.fxml'.";
        assert rejectButton != null : "fx:id=\"rejectButton\" was not injected: check your FXML file 'startGui.fxml'.";

        System.out.println("Initialized 'incoming' controller");

        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.activateCall();
            }
        });

        rejectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.startScreen();
            }
        });

    }
}

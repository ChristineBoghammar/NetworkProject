package client;

import protocol.Action;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Viktor on 2016-05-17.
 */
public class AudioReader extends Thread {
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

    private AudioMonitor mon;

    public AudioReader(AudioMonitor mon) {
        this.mon = mon;
    }

    public void run() {
        while (mon.isReceiving()) {
            Action action = mon.getAction();
            if (action.getCmd() == RECIEVE_AUDIO_DATA) {
                mon.receiveAudioData(action);
            }
        }
//
//        byte[] data = action.getAudioData();
//        ByteArrayInputStream bais = new ByteArrayInputStream(data);
//        AudioInputStream ais = new AudioInputStream(bais, format, data.length);
//        int bytesRead;
//        try {
//            if ((bytesRead = ais.read(data)) != -1) {
//                System.out.println("Writing to audio output.");
//                speaker.write(data, 0, bytesRead);
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            ais.close();
//            bais.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}






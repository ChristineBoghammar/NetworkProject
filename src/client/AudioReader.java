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
    private String sender;
    private Action action;
    private AudioFormat format;
    private SourceDataLine speaker;

    public AudioReader(String sender, Action action, AudioFormat format, SourceDataLine speaker){
        this.sender = sender;
        this.action = action;
        this.format = format;
        this.speaker = speaker;
    }

    public void run() {
        byte[] data = action.getAudioData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, data.length);
        int bytesRead;
        try {
            if ((bytesRead = ais.read(data)) != -1) {
                System.out.println("Writing to audio output.");
                speaker.write(data, 0, bytesRead);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ais.close();
            bais.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }






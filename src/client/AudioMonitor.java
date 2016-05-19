package client;

import protocol.Action;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by johan on 2016-05-19.
 */
public class AudioMonitor {
    private LinkedList<Action> actions;
    private AudioFormat format;
    private String audioSender;
    private SourceDataLine speaker;
    private boolean receiving;

    public AudioMonitor(String audioSender, SourceDataLine speaker){
        this.format = getAudioFormat();
        this.audioSender = audioSender;
        this.speaker = speaker;
        this.actions = new LinkedList<Action>();
        receiving = true;
        new AudioReader(this).start();
    }


    public synchronized Action getAction() {
        while (actions.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Monitor storlek: " + actions.size());
        return actions.poll();

    }

    /**
     * Puts an action into the lists of actions. Notifies the clients that an action is to be handled.
     *
     * @param action
     */
    public synchronized void putAction(Action action) {
        actions.add(action);
        System.out.println("PutAction: " + "cmd: " + action.getCmd() + " content: " + action.getContent() + " sender: " + action.getSender() + " callId: " + action.getCallID());
        notifyAll();
    }

    @SuppressWarnings("Duplicates")
    public synchronized void receiveAudioData(Action action) {

        byte[] data = action.getAudioData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, data.length);
        int bytesRead;
        try {
            if(speaker.isOpen() && receiving){
                if ((bytesRead = ais.read(data)) != -1) {
                    System.out.println("Writing to audio output.");
                    long time = System.currentTimeMillis();
                    speaker.write(data, 0, bytesRead);
                    System.out.println(System.currentTimeMillis() - time);
                }
            } else {
                receiving = false;
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

    @SuppressWarnings("Duplicates")
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }

    public boolean isReceiving(){
        return receiving;
    }

    public String getAudioSender(){
        return audioSender;
    }

    public void setReceiving(boolean bool) {
        receiving = bool;
    }
}

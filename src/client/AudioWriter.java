package client;

import protocol.Action;

import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by johan on 2016-05-12.
 */


public class AudioWriter extends Thread {
    private ClientMonitor mon;
    private AudioFormat format;
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

    public AudioWriter(ClientMonitor mon) {
        this.mon = mon;

        this.format = getAudioFormat();
    }

    public void run() {
//        DataOutputStream out = new DataOutputStream(os);
        System.out.println("Started AudioWriter");
        DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine mic = null;
        try {
            mic = (TargetDataLine) AudioSystem.getLine(micInfo);
            mic.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        System.out.println("Mic open.");
        byte tmpBuff[] = new byte[mic.getBufferSize() / 10];
        mic.start();
        long time;
        while (mon.getCallID() != -1) {
            time = System.currentTimeMillis();
            int count = mic.read(tmpBuff, 0, tmpBuff.length);
            if (count > 0) {
                mon.putAction(new Action(tmpBuff, mon.getName(), SEND_AUDIO_DATA, mon.getCallID()));
                tmpBuff = new byte[mic.getBufferSize() / 10];
                try {
                    this.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() - time);
//              out.write(tmpBuff, 0, count);
            }

        }
        return;
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }
}

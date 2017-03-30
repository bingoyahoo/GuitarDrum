package sg.edu.nus.guitardrum;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kang You Wei on 30/3/17.
 */

public class SoundSynthesizer {

    private int     samplingRate = 44100;
    public boolean isRunning    = false;
    private int     amp          = 10000;
    private double  twopi        = 8. * Math.atan(1.);
    private int     midi         = 60;
    private double  ph           = 0.0;
    private int buffsize;
    private double multiplier = 16;
    int portion = 4;
    private long    timeFrame = 5000;
    private boolean playSound = true;
    private int     findNotes = midi % 12;

    private String     noteInString;
    private TextView   notes;
    private AudioTrack audioTrack;
    private static Map<String, Integer> midi_map = new HashMap<String, Integer>();
    static {
        midi_map.put("C", 60);
        midi_map.put("C#", 61);
        midi_map.put("D", 62);
        midi_map.put("D#", 63);
        midi_map.put("E", 64);
        midi_map.put("F", 65);
        midi_map.put("F#", 66);
        midi_map.put("G", 67);
        midi_map.put("G#", 68);
        midi_map.put("A", 69);
        midi_map.put("A#", 70);
        midi_map.put("B", 71);

    }

    public SoundSynthesizer(String note_to_play) {
        noteInString = note_to_play;
        midi = midi_map.get(note_to_play);

        buffsize = AudioTrack.getMinBufferSize(samplingRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffsize = (int) (buffsize * multiplier);
        // Create an audiotrack object
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffsize,
                AudioTrack.MODE_STATIC);

        short samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            samples[i] = 0;
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();

    }

    public void play() {
        audioTrack.stop();
        audioTrack.flush();
        double freq = convertMidiToFreq(midi);
        isRunning = true;
        short samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void stop(){
        isRunning = false;
        audioTrack.stop();
        audioTrack.flush();
    }

    public void makeSofter(){
        amp = amp / 2;
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeLouder(){
        amp = amp * 2;
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeHigher(){
        midi = midi + 12;
        if (midi > 127) {
            midi = 127;
        }
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeLower(){
        midi = midi - 12;
        if (midi < 0) {
            midi = 0;
        }
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeSlower(){
        portion = portion - 1;
        if (portion < 2) {
            portion = 2;
        }
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeFaster(){
        portion = portion + 1;
        if (portion > 10) {

            portion = 10;
        }
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makeNext(){
        midi = midi + 1;
        if (midi > 127) {
            midi = 127;
        }
        findNotes = midi % 12;
        switch (findNotes) {
            case 0:
                noteInString = "C";
                break;
            case 1:
                noteInString = "C#";
                break;
            case 2:
                noteInString = "D";
                break;
            case 3:
                noteInString = "D#";
                break;
            case 4:
                noteInString = "E";
                break;
            case 5:
                noteInString = "F";
                break;
            case 6:
                noteInString = "F#";
                break;
            case 7:
                noteInString = "G";
                break;
            case 8:
                noteInString = "G#";
                break;
            case 9:
                noteInString = "A";
                break;
            case 10:
                noteInString = "A#";
                break;
            case 11:
                noteInString = "B";
                break;

        }
        notes.setText(noteInString);
        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();
    }

    public void makePrevious(){
        midi = midi - 1;
        if (midi < 0) {
            midi = 0;
        }
        findNotes = midi % 12;
        switch (findNotes) {
            case 0:
                noteInString = "C";
                break;
            case 1:
                noteInString = "C#";
                break;
            case 2:
                noteInString = "D";
                break;
            case 3:
                noteInString = "D#";
                break;
            case 4:
                noteInString = "E";
                break;
            case 5:
                noteInString = "F";
                break;
            case 6:
                noteInString = "F#";
                break;
            case 7:
                noteInString = "G";
                break;
            case 8:
                noteInString = "G#";
                break;
            case 9:
                noteInString = "A";
                break;
            case 10:
                noteInString = "A#";
                break;
            case 11:
                noteInString = "B";
                break;

        }

        notes.setText(noteInString);

        audioTrack.stop();
        audioTrack.flush();

        double freq      = convertMidiToFreq(midi);
        short  samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            if (i < buffsize / portion) {
                short wavePoint = (short) (0.25 * amp * Math.sin(ph) +
                        0.25 * amp * Math.sin(ph * 2) +
                        0.25 * amp * Math.sin(ph * 3) +
                        0.25 * amp * Math.sin(ph * 4));
                samples[i] = wavePoint;
                ph += twopi * freq / samplingRate;
            } else {
                samples[i] = 0;

            }
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.setLoopPoints(0, (buffsize - 1) / 2, -1);
        audioTrack.play();

    }



    private static double convertMidiToFreq(int midi) {
        if (midi == 0) {
            return 0;
        } else {
            double freq = 440.0 * Math.pow(2, (midi - 69.0) / 12.0);
            return freq;
        }
    }

    private static double[] linspace(double min, double max, int points) {
        double[] d = new double[points];
        for (int i = 0; i < points; i++) {
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    private static short[] createADSRFunction(int buffsize, int portion) {
        int attackDuration  = (int) ((buffsize / portion) * 0.35);
        int decayDuration   = (int) ((buffsize / portion) * 0.2);
        int sustainDuration = (int) ((buffsize / portion) * 0.25);
        int releaseDuration = (int) ((buffsize / portion) * 0.2);

        double[] attackValue  = linspace(0, 1, attackDuration);
        double[] decayValue   = linspace(1, 0.5, attackDuration);
        double[] releaseValue = linspace(0.5, 0, attackDuration);

        short[] signal = new short[buffsize];

        for (int i = 0; i < attackDuration; i++) {
            signal[i] = (short) attackValue[i];
        }
        for (int i = 0; i < decayDuration; i++) {
            signal[attackDuration + i] = (short) decayValue[i];
        }
        for (int i = 0; i < sustainDuration; i++) {
            signal[attackDuration + decayDuration + i] = (short) 0.5;
        }
        for (int i = 0; i < releaseDuration; i++) {
            signal[attackDuration + decayDuration + sustainDuration + i] = (short) releaseValue[i];
        }
        for (int i = attackDuration + decayDuration + sustainDuration + releaseDuration; i < buffsize; i++) {
            signal[i] = 0;
        }
        return signal;
    }
}

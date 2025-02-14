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
    private int     midi         = 60;
    private int buffsize;
    private double multiplier = 32;
    private int octave = 1; // 1 for normal, 0 for lower, 2 for higher
    private int volume = 1; // 1 for normal, 0 for lower, 2 for higher
    private boolean shortNote = true;
    private int chord =0; // 0 for C, 1 for Am, 2 for f, 3 for g

    private int     findNotes = midi % 12;
    private boolean chordPlay = false;
    private String     noteInString;
    private TextView   notes;
    private AudioTrack audioTrack;
    public String volumeText;
    public String octaveText;

    private static Map<String, Integer> midi_map = new HashMap<String, Integer>();
    static {
        midi_map.put("C", 60);
     //   midi_map.put("C#", 61);
        midi_map.put("D", 62);
     //   midi_map.put("D#", 63);
        midi_map.put("E", 64);
        midi_map.put("F", 65);
     //   midi_map.put("F#", 66);
        midi_map.put("G", 67);
    //    midi_map.put("G#", 68);
        midi_map.put("A", 69);
   //     midi_map.put("A#", 70);
        midi_map.put("B", 71);
        midi_map.put("C+",72); // c+ is a higher octave of C
        midi_map.put("Cchord", 0);
        midi_map.put("Amchord",1);
        midi_map.put("Fchord",2);
        midi_map.put("Gchord",3);

    }

 //   public SoundSynthesizer(String note_to_play) {
 //       noteInString = note_to_play;
   //     midi = midi_map.get(note_to_play);

    public SoundSynthesizer(){
        buffsize = AudioTrack.getMinBufferSize(samplingRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffsize = (int)(buffsize*multiplier);
        // create an audiotrack object
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffsize,
                AudioTrack.MODE_STREAM);

        short samples[] = new short[buffsize];

        for (int i = 0; i < buffsize; i++) {
            samples[i] = 0;
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();

    }

    public void setNote(String note_to_play,Boolean isChord){
        midi = midi_map.get(note_to_play);
        chordPlay = isChord;

    }
// No longer needed since will be using left or right direction to play
    public void play() {
        audioTrack.stop();
        audioTrack.flush();
        short[] samples = playNote(midi,buffsize,amp,samplingRate,shortNote,octave,volume);
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();
    }

    public void stop(){
        audioTrack.stop();
        audioTrack.flush();
    }

    public String makeSofter(){
        volume--;
        if(volume<0){
            volume = 0;
        }
        switch(volume){
            case 0: volumeText = "Low";
                break;
            case 1: volumeText = "Medium";
                break;
            case 2: volumeText = "High";
                break;
        }
        return volumeText;
    }

    public String makeLouder(){
        volume++;
        if(volume>2){
            volume = 2;
        }
        switch(volume){
            case 0: volumeText = "Low";
                break;
            case 1: volumeText = "Medium";
                break;
            case 2: volumeText = "High";
                break;
        }
        return volumeText;
    }

    public String makeHigher(){
        octave++;
        if (octave>2){
            octave =2;
        }
        switch(octave){
            case 0: octaveText = "Low";
                break;
            case 1: octaveText = "Medium";
                break;
            case 2: octaveText = "High";
                break;
        }
        return octaveText;
        /*
        shortNote = true;
        audioTrack.stop();
        audioTrack.flush();
        short[] samples = playNote(midi,buffsize,amp,samplingRate,shortNote,octave,volume);
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();
        */
    }

    public String makeLower(){
        octave--;
        if (octave<0){
            octave = 0;
        }
        switch(octave){
            case 0: octaveText = "Low";
                break;
            case 1: octaveText = "Medium";
                break;
            case 2: octaveText = "High";
                break;
        }
        return octaveText;
        /*
        shortNote = true;
        audioTrack.stop();
        audioTrack.flush();
        short[] samples = playNote(midi,buffsize,amp,samplingRate,shortNote,octave,volume);
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();
        */
    }

    public void shortPlay(){ // rename to shortPlay
        shortNote = true;
        audioTrack.stop();
        audioTrack.flush();
        short[] samples;
        if (chordPlay){
            samples = playChord(midi,buffsize,amp,samplingRate,shortNote,volume);
        }else{
            samples = playNote(midi,buffsize,amp,samplingRate,shortNote,octave,volume);
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();
    }

    public void longPlay(){  // rename to longPlay
        shortNote = false;
        audioTrack.stop();
        audioTrack.flush();
        short[] samples;
        if (chordPlay){
            samples = playChord(midi,buffsize,amp,samplingRate,shortNote,volume);
        }else{
            samples = playNote(midi,buffsize,amp,samplingRate,shortNote,octave,volume);
        }
        audioTrack.write(samples, 0, buffsize);
        audioTrack.play();
    }





    private static double convertMidiToFreq(int midi){
        if (midi ==0){
            return 0;
        }
        else{
            double freq = 440.0 * Math.pow(2,(midi-69.0)/12.0);
            return freq;
        }
    }
    private static double[] linspace(double min, double max, int points) {
        double[] d = new double[points];
        for (int i = 0; i < points; i++){
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    private static double[] createADSRFunction(int buffsize,boolean shortNote){
        int attackDuration;
        int decayDuration ;
        int sustainDuration ;
        int releaseDuration;

        if (shortNote){
            attackDuration = (int)((buffsize/4)*0.35);
            decayDuration = (int)((buffsize/4)*0.2);
            sustainDuration = (int)((buffsize/4) *0.25);
            releaseDuration = (int)((buffsize/4) *0.2);
        }
        else{
            attackDuration = (int)((buffsize)*0.35);
            decayDuration = (int)((buffsize)*0.2);
            sustainDuration = (int)((buffsize) *0.25);
            releaseDuration = (int)((buffsize) *0.2);
        }


        double[] attackValue = linspace(0,1,attackDuration);
        double[] decayValue = linspace(1,0.5,attackDuration);
        double[] releaseValue = linspace(0.5,0,attackDuration);

        //   Log.d("attack",Short.toString((short)attackValue[1]));

        double[] signal = new double[buffsize];

        for (int i = 0; i<attackDuration;i++){
            signal[i] = (double)attackValue[i];
        }
        for (int i = 0; i<decayDuration;i++){
            signal[attackDuration+i] = (double)decayValue[i];
        }
        for (int i = 0; i<sustainDuration;i++){
            signal[attackDuration+decayDuration+i] = (double) 0.5;
        }
        for (int i = 0; i<releaseDuration;i++){
            signal[attackDuration+decayDuration+sustainDuration+i] = (double)releaseValue[i];
        }
        //  Log.d("signal",Arrays.toString(signal));

        for (int i=attackDuration+decayDuration+sustainDuration+releaseDuration;i<buffsize;i++){
            signal[i]=0;
        }
        return signal;

    }
    private static short[] playNote(int midi, int buffsize,int amp, int samplingRate,boolean shortNote, int octave,int volume){
        if (octave== 0){
            midi = midi -12;
            if (midi<0){
                midi = 0;
            }
        }
        if (octave == 2){
            midi = midi +12;
            if (midi> 127){
                midi = 127;
            }
        }
        if (volume == 2){
            amp = amp*2;
        }
        else if (volume == 0){
            amp = amp /2;
        }

        double freq = convertMidiToFreq(midi);
        double freq2 = convertMidiToFreq(midi+12);
        double freq3 = convertMidiToFreq(midi+12+7);
        double freq4 = convertMidiToFreq(midi+24);
        short samples[] = new short[buffsize];
        double[] adsr = createADSRFunction(buffsize,shortNote);

        if (shortNote){
            for(int i=0; i < buffsize; i++) {
                if (i<buffsize/8){
                    double wavePoint= Math.abs(0.45*amp*(2*(i%(samplingRate/freq))/(samplingRate/freq)-1)
                            +0.3*amp*(2*(i%(samplingRate/freq2))/(samplingRate/freq2)-1)
                            +0.2*amp*(2*(i%(samplingRate/freq3))/(samplingRate/freq3)-1)
                            +0.05*amp*(2*(i%(samplingRate/freq4))/(samplingRate/freq4)-1))-amp;


                    samples[i] = (short)(wavePoint*adsr[i]);
                }
                else{
                    samples[i]=0;
                }
            }
        }
        else{
            for(int i=0; i < buffsize/4; i++) {
                if (i<buffsize /4) {
                    double wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / freq)) / (samplingRate / freq) - 1)
                            + 0.3 * amp * (2 * (i % (samplingRate / freq2)) / (samplingRate / freq2) - 1)
                            + 0.2 * amp * (2 * (i % (samplingRate / freq3)) / (samplingRate / freq3) - 1)
                            + 0.05 * amp * (2 * (i % (samplingRate / freq4)) / (samplingRate / freq4) - 1)) - amp;

                    samples[i] = (short) (wavePoint * adsr[i]);
                } else {
                    samples[i]=0;
                }

            }
        }

        return samples;

    }

    private static short[] playChord(int chord, int buffsize,int amp, int samplingRate,boolean shortNote,int volume) {
        int note1 = 60;
        int note2 = 64;
        int note3 = 67;
        int note4 = 72;

        if (chord == 0) {//c chord
            note1 = 60;//c4
            note2 = 64;//e4
            note3 = 67;//g4
            note4 = 72;//c5
        } else if (chord == 1) {//Am chord
            note1 = 57;//a3
            note2 = 60;//c4
            note3 = 64;//e4
            note4 = 69;//a4
        } else if (chord == 2) {//f chord
            note1 = 53;//f3
            note2 = 57;//a4
            note3 = 60;//c4
            note4 = 65;//f4
        } else { //g chord
            note1 = 55;//g3
            note2 = 59;//b3
            note3 = 62;//d4
            note4 = 67;//g4
        }

        if (volume == 2) {
            amp = amp * 2;
        } else if (volume == 0) {
            amp = amp / 2;
        }
        double note1_freq = convertMidiToFreq(note1);
        double note1_freq2 = convertMidiToFreq(note1+12);
        double note1_freq3 = convertMidiToFreq(note1+12+7);
        double note1_freq4 = convertMidiToFreq(note1+24);

        double note2_freq = convertMidiToFreq(note2);
        double note2_freq2 = convertMidiToFreq(note2+12);
        double note2_freq3 = convertMidiToFreq(note2+12+7);
        double note2_freq4 = convertMidiToFreq(note2+24);

        double note3_freq = convertMidiToFreq(note3);
        double note3_freq2 = convertMidiToFreq(note3+12);
        double note3_freq3 = convertMidiToFreq(note3+12+7);
        double note3_freq4 = convertMidiToFreq(note3+24);

        double note4_freq = convertMidiToFreq(note4);
        double note4_freq2 = convertMidiToFreq(note4+12);
        double note4_freq3 = convertMidiToFreq(note4+12+7);
        double note4_freq4 = convertMidiToFreq(note4+24);



        short samples[] = new short[buffsize];
        double[] adsr = createADSRFunction(buffsize, shortNote);

        if (shortNote) {
            for (int i = 0; i < buffsize; i++) {
                if (i < buffsize / 12) {
                    double note1_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note1_freq)) / (samplingRate / note1_freq) - 1)
                            + 0.3 * amp * (2 * (i % (samplingRate / note1_freq2)) / (samplingRate / note1_freq2) - 1)
                            + 0.2 * amp * (2 * (i % (samplingRate / note1_freq3)) / (samplingRate / note1_freq3) - 1)
                            + 0.05 * amp * (2 * (i % (samplingRate / note1_freq4)) / (samplingRate / note1_freq4) - 1)) - amp;
                    double note2_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note2_freq)) / (samplingRate / note2_freq) - 1)
                            + 0.3 * amp * (2 * (i % (samplingRate / note2_freq2)) / (samplingRate / note2_freq2) - 1)
                            + 0.2 * amp * (2 * (i % (samplingRate / note2_freq3)) / (samplingRate / note2_freq3) - 1)
                            + 0.05 * amp * (2 * (i % (samplingRate / note2_freq4)) / (samplingRate / note2_freq4) - 1)) - amp;

                    double note3_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note3_freq)) / (samplingRate / note3_freq) - 1)
                            + 0.3 * amp * (2 * (i % (samplingRate / note3_freq2)) / (samplingRate / note3_freq2) - 1)
                            + 0.2 * amp * (2 * (i % (samplingRate / note3_freq3)) / (samplingRate / note3_freq3) - 1)
                            + 0.05 * amp * (2 * (i % (samplingRate / note3_freq4)) / (samplingRate / note3_freq4) - 1)) - amp;

                    double note4_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note4_freq)) / (samplingRate / note4_freq) - 1)
                            + 0.3 * amp * (2 * (i % (samplingRate / note4_freq2)) / (samplingRate / note4_freq2) - 1)
                            + 0.2 * amp * (2 * (i % (samplingRate / note4_freq3)) / (samplingRate / note4_freq3) - 1)
                            + 0.05 * amp * (2 * (i % (samplingRate / note4_freq4)) / (samplingRate / note4_freq4) - 1)) - amp;

                    double wavePoint = 0.25 * note1_wavePoint +
                            0.25 * note2_wavePoint +
                            0.25 * note3_wavePoint +
                            0.25 * note4_wavePoint;


                    samples[i] = (short) (wavePoint * adsr[i]);
                } else {
                    samples[i] = 0;
                }
            }
        } else {
            for (int i = 0; i < buffsize; i++) {
                double note1_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note1_freq)) / (samplingRate / note1_freq) - 1)
                        + 0.3 * amp * (2 * (i % (samplingRate / note1_freq2)) / (samplingRate / note1_freq2) - 1)
                        + 0.2 * amp * (2 * (i % (samplingRate / note1_freq3)) / (samplingRate / note1_freq3) - 1)
                        + 0.05 * amp * (2 * (i % (samplingRate / note1_freq4)) / (samplingRate / note1_freq4) - 1)) - amp;
                double note2_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note2_freq)) / (samplingRate / note2_freq) - 1)
                        + 0.3 * amp * (2 * (i % (samplingRate / note2_freq2)) / (samplingRate / note2_freq2) - 1)
                        + 0.2 * amp * (2 * (i % (samplingRate / note2_freq3)) / (samplingRate / note2_freq3) - 1)
                        + 0.05 * amp * (2 * (i % (samplingRate / note2_freq4)) / (samplingRate / note2_freq4) - 1)) - amp;

                double note3_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note3_freq)) / (samplingRate / note3_freq) - 1)
                        + 0.3 * amp * (2 * (i % (samplingRate / note3_freq2)) / (samplingRate / note3_freq2) - 1)
                        + 0.2 * amp * (2 * (i % (samplingRate / note3_freq3)) / (samplingRate / note3_freq3) - 1)
                        + 0.05 * amp * (2 * (i % (samplingRate / note3_freq4)) / (samplingRate / note3_freq4) - 1)) - amp;

                double note4_wavePoint = Math.abs(0.45 * amp * (2 * (i % (samplingRate / note4_freq)) / (samplingRate / note4_freq) - 1)
                        + 0.3 * amp * (2 * (i % (samplingRate / note4_freq2)) / (samplingRate / note4_freq2) - 1)
                        + 0.2 * amp * (2 * (i % (samplingRate / note4_freq3)) / (samplingRate / note4_freq3) - 1)
                        + 0.05 * amp * (2 * (i % (samplingRate / note4_freq4)) / (samplingRate / note4_freq4) - 1)) - amp;

                double wavePoint = 0.25 * note1_wavePoint +
                        0.25 * note2_wavePoint +
                        0.25 * note3_wavePoint +
                        0.25 * note4_wavePoint;


                samples[i] = (short) (wavePoint * adsr[i]);

            }
        }

        return samples;
    }

    public void setVolume(int loudness){
        this.volume = loudness;
        switch(this.volume){
            case 0: volumeText = "Low";
                break;
            case 1: volumeText = "Medium";
                break;
            case 2: volumeText = "High";
                break;
        }
    }

    public void setOctave(int newOctave){
        this.octave = newOctave;
        switch(octave){
            case 0: octaveText = "Low";
                break;
            case 1: octaveText = "Medium";
                break;
            case 2: octaveText = "High";
                break;
        }
    }



}

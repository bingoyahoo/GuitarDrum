package sg.edu.nus.guitardrum;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

import jAudioFeatureExtractor.GeneralTools.Statistics;

import static android.R.attr.value;
import static android.R.attr.x;
import static android.R.attr.y;

public class NewDrumActivity extends AppCompatActivity implements SensorEventListener {

    private MediaPlayer mPlayerHitHat, mPlayerKick, mPlayerSnare, mPlayerCrash; /* For Drum */
    private static float SHAKE_THRESHOLD = 1000;
    private int drum_type;


    //variables for checking sampling rate
    private final static long sampling_interval = 10000000; //ns
    private final static long sampling_interval_error_margin = 2000000;//20%
    private double last_x;
    private double last_y;
    private double last_z;
    private long lastTimeStamp;
    private long lastUpdate;
    private final static int MAX_VOLUME = 100;


    //Buffer variables
    private boolean bufferisReady = false;
    private double[][] buffer;
    private double[][] nextBuffer;
    private final static int bufferLen = 1024;
    private final static int bufferOverlap = 512;
    //    private final static int bufferLen = 64;
//    private final static int bufferOverlap = 32;
    private int bufferIndex;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private FeaturesExtractor featureExtractor;

    Queue    queueBufferX;
    Queue    queueBufferY;
    Queue    queueBufferZ;
    double[] latestBufferX;
    double[] latestBufferY;
    double[] latestBufferZ;
    public static int BUFFER_LEN = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drum);

        queueBufferX = new PriorityQueue<Double>(BUFFER_LEN);
        queueBufferY = new PriorityQueue<Double>(BUFFER_LEN);
        queueBufferZ = new PriorityQueue<Double>(BUFFER_LEN);

        // For playing drum sounds when acceleration detected
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // For Buffers
        buffer = new double[3][bufferLen];
        nextBuffer = new double[3][bufferLen];
        bufferIndex = 0;
        latestBufferX = new double[BUFFER_LEN];
        latestBufferY = new double[BUFFER_LEN];
        latestBufferZ = new double[BUFFER_LEN];

        featureExtractor = null;

        drum_type = 0;

        FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.drum_button_1);
        FloatingActionButton fab2 = (FloatingActionButton)findViewById(R.id.drum_button_2);
        FloatingActionButton fab3 = (FloatingActionButton)findViewById(R.id.drum_button_3);
        FloatingActionButton fab4 = (FloatingActionButton)findViewById(R.id.drum_button_4);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(1);
                drum_type = 0;
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(2);
                drum_type = 1;
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(3);
                drum_type = 2;
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(4);
                drum_type = 3;
            }
        });

        mPlayerHitHat = MediaPlayer.create(this, R.raw.drum_hihat);
        mPlayerKick = MediaPlayer.create(this, R.raw.drum_kick);
        mPlayerSnare = MediaPlayer.create(this, R.raw.drum_snare);
        mPlayerCrash = MediaPlayer.create(this, R.raw.drum_crash);
    }


    public void setVolume(MediaPlayer mediaPlayer, int soundVolume) {
        final float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
        mediaPlayer.setVolume(volume, volume);
    }

    public void startAllAnimation(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_new_drum);
        startRippleAnimation(index);
        startColorTransition(index);
    }

    private void startColorTransition(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_new_drum);

        int color = Color.TRANSPARENT;
        Drawable background = relativeLayout.getBackground();
        if (background instanceof ColorDrawable) color = ((ColorDrawable) background).getColor();
        int colorStart = color;
        int colorEnd = Color.TRANSPARENT;
        switch (index) {
            case 1:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorDrumButton1);
                break;
            case 2:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorDrumButton2);
                break;
            case 3:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorDrumButton3);
                break;
            case 4:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorDrumButton4);
                break;

        }

        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);

        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(1500);

        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                relativeLayout.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        colorAnim.start();
    }

    public void startRippleAnimation(int index) {
        final RippleBackground rippleBackground;
        switch (index) {
            case 1:
                rippleBackground=(RippleBackground)findViewById(R.id.drum_ripple_bg_1);
                break;
            case 2:
                rippleBackground=(RippleBackground)findViewById(R.id.drum_ripple_bg_2);
                break;
            case 3:
                rippleBackground=(RippleBackground)findViewById(R.id.drum_ripple_bg_3);
                break;
            case 4:
                rippleBackground=(RippleBackground)findViewById(R.id.drum_ripple_bg_4);
                break;
            default:
                rippleBackground=(RippleBackground)findViewById(R.id.drum_ripple_bg_1);
        }
        playRippleAnimation(rippleBackground);
    }

    public void playRippleAnimation(final RippleBackground rb) {
        rb.startRippleAnimation();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                rb.stopRippleAnimation();
            }
        }, 1500);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();
        float[] values = event.values;
        if(sensor == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if(lastUpdate == 0){
                lastUpdate = curTime;
                last_x = event.values[0];
                last_y = event.values[1];
                last_z = event.values[2];
            }
            // Only allows one update every 100ms
            else if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                double x = values[0];
                double y = values[1];
                double z = values[2];
                if (queueBufferX.size() >= BUFFER_LEN) {
                    queueBufferX.poll();
                    queueBufferY.poll();
                    queueBufferZ.poll();
                }
                    queueBufferX.add(x);
                    queueBufferY.add(y);
                    queueBufferZ.add(z);


                double speed = (x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD){
                    Object[] arrayX = queueBufferX.toArray();
                    Object[] arrayY = queueBufferY.toArray();
                    Object[] arrayZ = queueBufferZ.toArray();
                    for (int j=0; j< arrayX.length; j++){
                        latestBufferX[j] = (Double)arrayX[j];
                        latestBufferY[j] = (Double)arrayY[j];
                        latestBufferZ[j] = (Double)arrayZ[j];
                    }

                    double avgValueX = Statistics.getAverage(latestBufferX);
                    double avgValueY = Statistics.getAverage(latestBufferY);
                    double avgValueZ = Statistics.getAverage(latestBufferZ);
                    double rangeX = calculateRange(latestBufferX);
                    double rangeY = calculateRange(latestBufferY);
                    double rangeZ = calculateRange(latestBufferZ);
                    Log.d("sensor", "shake detected w/ speed: " + speed + " " + avgValueX);
                    Log.d("sensor", "speed X: " + avgValueX);
                    Log.d("sensor", "speed Y: " + avgValueY);
                    Log.d("sensor", "speed Z: " + avgValueZ);
                    Log.d("sensor", "range X: " + rangeX);
                    Log.d("sensor", "range Y: " + rangeY);
                    Log.d("sensor", "range Z: " + rangeZ);
                    playDrumSound();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }


//        makeBuffer(event);
//        if (bufferisReady) {
//            doSomeCalculations(buffer);
//            bufferisReady = false;
//            //copy nextBuffer into buffer
//            for (int i = 0; i < bufferOverlap; i++) {
//                for (int j = 0; j < 3; j++) {
//                    buffer[j][i] = nextBuffer[j][i];
//                }
//            }
//        }
    }

    public double calculateRange(double[] samples){
        int indexLargest = Statistics.getIndexOfLargest(samples);

        double highestPeak = samples[indexLargest];

        int indexSmallest = Statistics.getIndexOfSmallest(samples);

        double smallestValue = samples[indexSmallest];

        double range = highestPeak - smallestValue;
        return range;
    }

    // Play this sound if shaking detected
    private void playDrumSound(){
        switch (drum_type){
            case 1:
                if(mPlayerSnare.isPlaying()) {
                    mPlayerSnare.stop();
                    try {
                        mPlayerSnare.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mPlayerSnare.start();
                }
                break;
            case 2:
                if(mPlayerKick.isPlaying()) {
                    mPlayerKick.stop();
                    try {
                        mPlayerKick.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mPlayerKick.start();
                }
                break;
            case 3:
                if(mPlayerCrash.isPlaying()) {
//                    mPlayerCrash.stop();
//                    try {
////                        mPlayerCrash.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                else {
                    mPlayerCrash.start();
                }
                break;
            default:
                if(mPlayerHitHat.isPlaying()) {
                    mPlayerHitHat.stop();
                    try {
                        mPlayerHitHat.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mPlayerHitHat.start();
                }
        }
    }


    private void makeBuffer(SensorEvent event){
        int sensor = event.sensor.getType();
        float[] values = event.values;

        if(sensor == Sensor.TYPE_ACCELEROMETER) {
            if(lastTimeStamp == 0){
                lastTimeStamp = event.timestamp;
            }
            else {
                long timeInterval = event.timestamp - lastTimeStamp;
                buffer[0][bufferIndex] = values[0];
                buffer[1][bufferIndex] = values[1];
                buffer[2][bufferIndex] = values[2];
                last_x = values[0];
                last_y = values[1];
                last_z = values[2];
                lastTimeStamp = event.timestamp;
                bufferIndex += 1;
                //buffer is full
                if(bufferIndex == bufferLen){
                    bufferisReady = true;
                    //copy values into new buffer
                    for(int i = 0; i < bufferOverlap; i++){
                        for(int j=0; j<3; j++){
                            nextBuffer[j][i] = buffer[j][i + bufferOverlap];
                        }
                    }
                    bufferIndex = bufferOverlap;
                }

            }
        }
    }

    private void doSomeCalculations(double[][] buffer){
        try {
            Log.v("Buffer is ", Arrays.toString(buffer[0]));
            featureExtractor = new FeaturesExtractor(buffer[0], 100);
        } catch (Exception e) {
            Toast.makeText(this, "FeaturesExtractor cannot launch", Toast.LENGTH_SHORT).show();
        }

        try {
            featureExtractor.calculateFeatuers();
//            d.calculateFeatuersMean();
        } catch (Exception e) {
            Toast.makeText(this, "Features problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

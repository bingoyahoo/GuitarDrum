package sg.edu.nus.guitardrum;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity  implements SensorEventListener {

    //variables for checking sampling rate
    private final static long sampling_interval = 10000000; //ns
    private final static long sampling_interval_error_margin = 2000000;//20%
    private double last_x;
    private double last_y;
    private double last_z;
    private long lastTimeStamp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // For playing guitar sounds when acceleration detected
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        final ImageButton guitarButton = (ImageButton)findViewById(R.id.imageButton_home_guitar);
        final ImageButton drumButton = (ImageButton)findViewById(R.id.imageButton_home_drum);

        final LinearLayout guitarView = (LinearLayout)findViewById(R.id.guitar_view);
        final LinearLayout drumView = (LinearLayout)findViewById(R.id.drum_view);

        // I recorded the guitar sound myself, hope it sounds ok haha
        // Drum beat taken from https://www.freesoundeffects.com/free-sounds/drum-loops-10031/
        final MediaPlayer guitarPlayer = MediaPlayer.create(this, R.raw.guitar_intro);
        final MediaPlayer drumPlayer = MediaPlayer.create(this, R.raw.drum_intro);


        // For Buffers
        buffer = new double[3][bufferLen];
        nextBuffer = new double[3][bufferLen];
        bufferIndex = 0;

        featureExtractor = null;

        /*
            To be added:
            Credit Page or other forms to credit the author
            Image assets of guitar and drum: Dmitry Ryabov (https://www.behance.net/gallery/29912703/Low-Poly-Musical-Instruments)
            License: CC BY_NY 4.0 (https://creativecommons.org/licenses/by-nc/4.0/deed.en_US)
         */


        /*
            Guitar ImageButton click animation and sound
         */
        guitarButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int oldPadding = (int) Util.dpToPx(getApplicationContext(), 25);
                int newPadding = (int) Util.dpToPx(getApplicationContext(), 5);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // detect if move outside the view
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        } else {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                            guitarPlayer.start();
                            // go to guitar page
                            startActivity(new Intent(HomeActivity.this, GuitarActivity.class));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        guitarView.setPadding(newPadding, newPadding, newPadding, newPadding);
                        break;
                }
                return false;
            }
        });

        /*
            Drum ImageButton click animation and sound
         */
        drumButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int oldPadding = (int)Util.dpToPx(getApplicationContext(), 35);
                int newPadding = (int)Util.dpToPx(getApplicationContext(), 5);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        } else {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                            drumPlayer.start();
                            startActivity(new Intent(HomeActivity.this, NewDrumActivity.class));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        drumView.setPadding(newPadding, newPadding, newPadding, newPadding);
                        break;
                }
                return false;
            }
        });
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        makeBuffer(event);
        if (bufferisReady) {
            doSomeCalculations(buffer);
            bufferisReady = false;
            //copy nextBuffer into buffer
            for (int i = 0; i < bufferOverlap; i++) {
                for (int j = 0; j < 3; j++) {
                    buffer[j][i] = nextBuffer[j][i];
                }
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
//                Log.v("Accelerometer x", String.valueOf(values[0]));
//                Log.v("Accelerometer y", String.valueOf(values[1]));
//                Log.v("Accelerometer z", String.valueOf(values[2]));
                long timeInterval = event.timestamp - lastTimeStamp;
                //Log.d("timestamp", "timediff " + timeInterval);
                //Put values into buffer
//                if(timeInterval > sampling_interval + 2 * sampling_interval_error_margin){
//                    //Will this happen?
////                    Log.d("buffer", "timediff is twice error margin" + timeInterval);
//                }
//                if(timeInterval > sampling_interval + sampling_interval_error_margin){
//                    //If timestamp > sampling rate
//                    //interpolate values
                    //TODO: why is this not working?
//                    long ratio = timeInterval / sampling_interval;
//                    buffer[0][bufferIndex]= last_x + (values[0] - last_x) * ratio;
//                    buffer[1][bufferIndex] = last_y + (values[1] - last_y) * ratio;
//                    buffer[2][bufferIndex] = last_z + (values[2] - last_z) * ratio;
//                    last_x = buffer[0][bufferIndex];
//                    last_y = buffer[1][bufferIndex];
//                    last_z = buffer[2][bufferIndex];
//                    lastTimeStamp = lastTimeStamp + sampling_interval;
//                }
//                else if (timeInterval < sampling_interval - sampling_interval_error_margin){
//                    //don't do anything
//                    //Not sure if we should do this, or extrapolate values?
//                }
//                else {
//                    buffer[0][bufferIndex] = values[0];
//                    buffer[1][bufferIndex] = values[1];
//                    buffer[2][bufferIndex] = values[2];
//                    last_x = values[0];
//                    last_y = values[1];
//                    last_z = values[2];
//                    lastTimeStamp = event.timestamp;
//                }
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
//        Log.v("Calculating", " features");
        //dummy function
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

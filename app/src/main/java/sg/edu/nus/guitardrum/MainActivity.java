package sg.edu.nus.guitardrum;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView textViewFinger1id, textViewFinger1xcoor, textViewFinger1ycoor, textViewFinger2id, textViewFinger2xcoor, textViewFinger2ycoor, textViewFinger3id, textViewFinger3xcoor, textViewFinger3ycoor;
    private SparseArray<PointF> mActivePointers;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private MediaPlayer mPlayerCymbal; /* For Drum */
    private MediaPlayer mPlayerGuitar, mPlayerGuitarHit,
            mPlayerGuitarFirstStringE, mPlayerGuitarSecondStringB,
            mPlayerGuitarThirdStringG, mPlayerGuitarFourthStringD,
            mPlayerGuitarFifthStringA, mPlayerGuitarSixthStringE; /* For Guitar */
    private final static double SMASH_THRESHOLD = 28;
    private static float SHAKE_THRESHOLD = 1500;

    //variables for checking sampling rate
    private final static long sampling_interval = 10000000; //ns
    private final static long sampling_interval_error_margin = 2000000;//20%
    private float last_x;
    private float last_y;
    private float last_z;
    private long lastTimeStamp;
    //Buffer variables
    private boolean bufferisReady = false;
    private float[][] buffer;
    private float[][] nextBuffer;
    private final static int bufferLen = 1024;
    private final static int bufferOverlap = 512;
    private int bufferIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // For playing guitar sounds when acceleration detected
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mPlayerGuitar = MediaPlayer.create(this, R.raw.beat);
        mPlayerGuitarHit = MediaPlayer.create(this, R.raw.guitar_hit);
        mPlayerGuitarFirstStringE = MediaPlayer.create(this, R.raw.guitar_first);
        mPlayerGuitarSecondStringB = MediaPlayer.create(this, R.raw.guitar_second);
        mPlayerGuitarThirdStringG = MediaPlayer.create(this, R.raw.guitar_third);
        mPlayerGuitarFourthStringD = MediaPlayer.create(this, R.raw.guitar_fourth);
        mPlayerGuitarFifthStringA = MediaPlayer.create(this, R.raw.guitar_fifth);
        mPlayerGuitarSixthStringE = MediaPlayer.create(this, R.raw.guitar_sixth);

        // For Multi-touch
        mActivePointers = new SparseArray<PointF>();
        textViewFinger1id = (TextView) findViewById(R.id.finger1_id);
        textViewFinger2id = (TextView) findViewById(R.id.finger2_id);
        textViewFinger3id = (TextView) findViewById(R.id.finger3_id);
        textViewFinger1xcoor = (TextView) findViewById(R.id.finger1_xcoor);
        textViewFinger2xcoor = (TextView) findViewById(R.id.finger2_xcoor);
        textViewFinger3xcoor = (TextView) findViewById(R.id.finger3_xcoor);
        textViewFinger1ycoor = (TextView) findViewById(R.id.finger1_ycoor);
        textViewFinger2ycoor = (TextView) findViewById(R.id.finger2_ycoor);
        textViewFinger3ycoor = (TextView) findViewById(R.id.finger3_ycoor);

        // For Buffers
        buffer = new float[bufferLen][3];
        nextBuffer = new float[bufferLen][3];
        bufferIndex = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toggle_instruments) {
            Intent changeInstrumentIntent = new Intent(this, DrumActivity.class);
            startActivity(changeInstrumentIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void makeBuffer(SensorEvent event){
        int sensor = event.sensor.getType();
        float[] values = event.values;

        if(sensor == Sensor.TYPE_ACCELEROMETER) {
            if(lastTimeStamp == 0){
                lastTimeStamp = event.timestamp;
            }
            else {
                long timeInterval = event.timestamp - lastTimeStamp;
                //Log.d("timestamp", "timediff " + timeInterval);
                //Put values into buffer
                if(timeInterval > sampling_interval + 2 * sampling_interval_error_margin){
                    //Will this happen?
                    Log.d("buffer", "timediff is twice error margin" + timeInterval);
                }
                if(timeInterval > sampling_interval + sampling_interval_error_margin){
                    //If timestamp > sampling rate
                    //interpolate values
                    long ratio = timeInterval / sampling_interval;
                    buffer[bufferIndex][0] = last_x + (values[0] - last_x) * ratio;
                    buffer[bufferIndex][1] = last_y + (values[1] - last_y) * ratio;
                    buffer[bufferIndex][2] = last_z + (values[2] - last_z) * ratio;
                    last_x = buffer[bufferIndex][0];
                    last_y = buffer[bufferIndex][1];
                    last_z = buffer[bufferIndex][2];
                    lastTimeStamp = lastTimeStamp + sampling_interval;
                }
                else if (timeInterval < sampling_interval - sampling_interval_error_margin){
                    //don't do anything
                    //Not sure if we should do this, or extrapolate values?
                }
                else {
                    buffer[bufferIndex][0] = values[0];
                    buffer[bufferIndex][1] = values[1];
                    buffer[bufferIndex][2] = values[2];
                    last_x = values[0];
                    last_y = values[1];
                    last_z = values[2];
                    lastTimeStamp = event.timestamp;
                }
                bufferIndex += 1;
                //buffer is full
                if(bufferIndex == bufferLen){
                    bufferisReady = true;
                    //copy values into new buffer
                    for(int i = 0; i < bufferOverlap; i++){
                        for(int j=0; j<3; j++){
                            nextBuffer[i][j] = buffer[i + bufferOverlap][j];
                        }
                    }
                    bufferIndex = bufferOverlap;
                }

            }
        }
    }

    private void doSomeCalculations(float[][] buffer){
        //dummy function
    }

    public void onSensorChanged(SensorEvent event) {
        makeBuffer(event);
        if(bufferisReady){
            doSomeCalculations(buffer);
            bufferisReady = false;
            //copy nextBuffer into buffer
            for(int i = 0; i < bufferOverlap; i++){
                for (int j=0; j<3; j++){
                    buffer[i][j] = nextBuffer[i][j];
                }
            }
        }
//        int sensor = event.sensor.getType();
//        float[] values = event.values;
//        if(sensor == Sensor.TYPE_ACCELEROMETER) {
//            long curTime = System.currentTimeMillis();
//            if(lastUpdate == 0){
//                lastUpdate = curTime;
//                last_x = event.values[0];
//                last_y = event.values[1];
//                last_z = event.values[2];
//            }
//            // Only allows one update every 100ms
//            else if ((curTime - lastUpdate) > 100) {
//                long diffTime = (curTime - lastUpdate);
//                lastUpdate = curTime;
//                float x = values[0];
//                float y = values[1];
//                float z = values[2];
//
//                // Delvin: Smash
//                if (values[2] > SMASH_THRESHOLD) {
//                    Log.i("sensor", "running");
//                    Log.e("Accelerometer Triggered", " " + x + " " + y + " " + z);
//                    playSmashSound();
//
//                } else { // Krystal: Shake
//                    float speed = (x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//                    if (speed > SHAKE_THRESHOLD) {
//                        Log.d("sensor", "shake detected w/ speed: " + speed);
//                        playGuitarChordOrNote();
//                    }
//                }
//                last_x = x;
//                last_y = y;
//                last_z = z;
//            }
//        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Play this sound if smashing detected
    private void playSmashSound(){
        if(mPlayerGuitarHit.isPlaying()) {
            mPlayerGuitarHit.stop();
            try {
                mPlayerGuitarHit.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            mPlayerGuitarHit.start();
        }
    }

    // Play this sound if strumming detected
    private void playGuitarChordOrNote() {
        int numFingers = mActivePointers.size();
        if (numFingers == 0) {
            if(mPlayerGuitarFirstStringE.isPlaying()) {
                mPlayerGuitarFirstStringE.stop();
                try {
                    mPlayerGuitarFirstStringE.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                mPlayerGuitarFirstStringE.start();
            }
        } else if (numFingers == 1){
            if(mPlayerGuitarSecondStringB.isPlaying()) {
                mPlayerGuitarSecondStringB.stop();
                try {
                    mPlayerGuitarSecondStringB.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                mPlayerGuitarSecondStringB.start();
            }
        } else {
            if(mPlayerGuitarThirdStringG.isPlaying()) {
                mPlayerGuitarThirdStringG.stop();
                try {
                    mPlayerGuitarThirdStringG.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                mPlayerGuitarThirdStringG.start();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();

        switch(maskedAction){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:{
                // new pointer, add to list
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId,f);
                final float x = f.x;
                final float y = f.y;
                if (pointerId==0){
                    textViewFinger1id.setText(Float.toString(pointerId));
                    textViewFinger1xcoor.setText(Float.toString(x));
                    textViewFinger1ycoor.setText(Float.toString(y));
                }
                if (pointerId==1){
                    textViewFinger2id.setText(Float.toString(pointerId));
                    textViewFinger2xcoor.setText(Float.toString(x));
                    textViewFinger2ycoor.setText(Float.toString(y));
                }
                if (pointerId==2){
                    textViewFinger3id.setText(Float.toString(pointerId));
                    textViewFinger3xcoor.setText(Float.toString(x));
                    textViewFinger3ycoor.setText(Float.toString(y));
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                for( int size = event.getPointerCount(),i=0;i<size;i++){
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if(point !=null){
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                        final float x = point.x;
                        final float y = point.y;
                        if (i==0){
                            textViewFinger1id.setText(Float.toString(i));
                            textViewFinger1xcoor.setText(Float.toString(x));
                            textViewFinger1ycoor.setText(Float.toString(y));
                        }
                        if (i==1){
                            textViewFinger2id.setText(Float.toString(i));
                            textViewFinger2xcoor.setText(Float.toString(x));
                            textViewFinger2ycoor.setText(Float.toString(y));
                        }
                        if (i==2){
                            textViewFinger3id.setText(Float.toString(i));
                            textViewFinger3xcoor.setText(Float.toString(x));
                            textViewFinger3ycoor.setText(Float.toString(y));
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:{
                mActivePointers.remove(pointerId);
                if (pointerId==0){
                    textViewFinger1id.setText(Float.toString(pointerId));
                    textViewFinger1xcoor.setText(Float.toString(0));
                    textViewFinger1ycoor.setText(Float.toString(0));
                }
                if (pointerId==1){
                    textViewFinger2id.setText(Float.toString(pointerId));
                    textViewFinger2xcoor.setText(Float.toString(0));
                    textViewFinger2ycoor.setText(Float.toString(0));
                }
                if (pointerId==2){
                    textViewFinger3id.setText(Float.toString(pointerId));
                    textViewFinger3xcoor.setText(Float.toString(0));
                    textViewFinger3ycoor.setText(Float.toString(0));
                }
                break;
            }
        }
        return true;
    }
}
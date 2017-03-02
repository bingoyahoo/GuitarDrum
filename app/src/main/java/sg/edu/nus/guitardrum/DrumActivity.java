package sg.edu.nus.guitardrum;

import android.content.Context;
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

public class DrumActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xText, yText, zText;
    private Sensor        mProximitySensor;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private MediaPlayer mPlayerDrumHihat,
            mPlayerDrumCrash, mPlayerDrumKickDrum,
            mPlayerDrumSnare; /* For Drum */
    private final static double SMASH_THRESHOLD = 28;
    private static float SHAKE_THRESHOLD = 1500;

    TextView textViewFinger1id, textViewFinger1xcoor, textViewFinger1ycoor, textViewFinger2id, textViewFinger2xcoor, textViewFinger2ycoor, textViewFinger3id, textViewFinger3xcoor, textViewFinger3ycoor;
    private SparseArray<PointF> mActivePointers;

    private float last_x;
    private float last_y;
    private float last_z;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Create sensor manager for proximity
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        xText = (TextView)findViewById(R.id.xText);

        // For playing drum sounds when acceleration detected
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mPlayerDrumHihat = MediaPlayer.create(this, R.raw.drum_hihat);
        mPlayerDrumCrash = MediaPlayer.create(this, R.raw.drum_crash);
        mPlayerDrumKickDrum = MediaPlayer.create(this, R.raw.drum_kick);
        mPlayerDrumSnare = MediaPlayer.create(this, R.raw.drum_snare);

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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // Play this sound if smashing detected
    private void playSmashSound(){
        if(mPlayerDrumCrash.isPlaying()) {
            mPlayerDrumCrash.stop();
            try {
                mPlayerDrumCrash.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayerDrumCrash.start();
        } else {
            mPlayerDrumCrash.start();
        }
    }

    // Play this sound if strumming detected
    private void playGuitarChordOrNote() {
        int numFingers = mActivePointers.size();
        if (numFingers == 0) {
            if(mPlayerDrumHihat.isPlaying()) {
                mPlayerDrumHihat.stop();
                try {
                    mPlayerDrumHihat.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayerDrumHihat.start();
            } else {
                mPlayerDrumHihat.start();
            }
        } else if (numFingers == 1){

            if(mPlayerDrumSnare.isPlaying()){
                mPlayerDrumSnare.stop();
                try {
                    mPlayerDrumSnare.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayerDrumSnare.start();
            } else {
                mPlayerDrumSnare.start();
            }
        } else {
            if(mPlayerDrumKickDrum.isPlaying())
            {
                mPlayerDrumKickDrum.stop();
                try {
                    mPlayerDrumKickDrum.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayerDrumKickDrum.start();
            }
            else {
                mPlayerDrumKickDrum.start();
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();
        float[] values = event.values;
//        if (sensor == Sensor.TYPE_PROXIMITY){
//            if (event.values[0] > 0) {
//                xText.setText("Hand Away");
//            } else {
//                xText.setText("Hand Near");
//            }
//        } else
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
                float x = values[0];
                float y = values[1];
                float z = values[2];

                // Delvin: Smash
                if (values[2] > SMASH_THRESHOLD) {
                    Log.i("sensor", "running");
                    Log.e("Accelerometer Triggered", " " + x + " " + y + " " + z);
                    playSmashSound();

                } else { // Krystal: Shake
                    float speed = (x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                    if (speed > SHAKE_THRESHOLD) {
                        Log.d("sensor", "shake detected w/ speed: " + speed);
                        playGuitarChordOrNote();
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // no use
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

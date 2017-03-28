package com.octus.nuruddin.trainingApp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main extends Activity implements SensorEventListener {

    private Sensor senAccelerometer;
    private SensorManager senSensorManager;
    private Spinner spinner1;

    private long lastUpdate = 0;
    private long startTime = 0;
    private long mShakeTimeStamp = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1000;
    private static final int SHAKE_COUNT_TIME = 2000;
    private int count = 0;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<Long> timestampList = new ArrayList<Long>();
    private String direction;
    private int fileCount = 0;
    private double samplingRate = 0.0;
    private boolean startRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_UI);
        addListenerOnSpinnerItemSelection();
        spinner1 = (Spinner) findViewById(R.id.spinner);

    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    @Override
    // To detect shake gesture
    // Invoke every time the built-in sensor detects a change
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;



        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (!spinner1.getSelectedItem().toString().equals("--Select--")) {
                direction = spinner1.getSelectedItem().toString();
                spinner1.setEnabled(false);
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {

                        if (startRecording == false) {

                            startTime = System.currentTimeMillis();
                            startRecording = true;
                        }

                        setCoordinates(x, y, z);
                        long timeInMillis = (new Date()).getTime()
                                + (event.timestamp - System.nanoTime()) / 1000000L;
                        timestampList.add(timeInMillis);
                        float[] coords = {x, y, z};
                        //BigDecimal result;
                        //result = round(x,6);
                        list.add(Arrays.toString(coords));
                        if (count == 100) {

                            FileStorage fs = new FileStorage();
                            StringBuilder sb = new StringBuilder();
                            long now = System.currentTimeMillis();
                            samplingRate = count / ((now - startTime) / 1000.0);
                            sb.append(direction);
                            sb.append(';');
                            sb.append(String.format(
                                    "%.2f", samplingRate)+"Hz");
                            sb.append(';');
                            for (int i = 0; i < list.size(); i++) {
                                sb.append("{");
                                sb.append(list.get(i));
                                //sb.append(timestampList.get(i).toString());
                                sb.append("}");
                            }

                            fs.storeData(direction, fileCount, sb.toString(), this);
                            TextView coord_fileCount = (TextView)findViewById(R.id.textViewFileCount);
                            coord_fileCount.setText(direction+String.valueOf(fileCount));
                            fileCount = fileCount + 1;
                        }

                        else if (count > 100){
                            count = 0;
                            startRecording = false;
                            list.clear();
                        }

                    } // end of checking of SHAKE_THRESHOLD

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        } // end of checking if sensor is accelerometer
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    // Good practice to unregister the sensor when the application hibernates
    // and register the sensor again when the application resumes.

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }
    protected void onResume(){
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void setCoordinates(float x, float y, float z){

        TextView coord_X = (TextView)findViewById(R.id.text_X);
        coord_X.setText(String.valueOf(x));
        TextView coord_Y = (TextView)findViewById(R.id.text_Y);
        coord_Y.setText(String.valueOf(y));
        TextView coord_Z = (TextView)findViewById(R.id.text_Z);
        coord_Z.setText(String.valueOf(z));
        TextView countText = (TextView)findViewById(R.id.text_Count);
        count = count + 1;
        countText.setText((String.valueOf(count)));
    }
    private static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

}

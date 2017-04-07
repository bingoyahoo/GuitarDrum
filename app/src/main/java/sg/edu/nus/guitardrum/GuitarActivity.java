package sg.edu.nus.guitardrum;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;

/*
            Make sure to Sync Project With Gradle Files (Tools -> Android -> Sync Project With Gradle Files) and rebuild the app
            for usage of RippleBackground refer to: https://github.com/skyfishjy/android-ripple-background
*/

public class GuitarActivity extends AppCompatActivity implements SensorEventListener {
    /** Called when the activity is first created. */
    //variables for checking sampling rate
    private final static long sampling_interval = 10000000; //ns
    private final static long sampling_interval_error_margin = 2000000;//20%
    private double last_x;
    private double last_y;
    private double last_z;
    private String soundVolume = "medium volume";
    private String octaveValue = "medium octave";
    private long lastTimeStamp;
    //Buffer variables
    private boolean bufferisReady = false;
    private double[][] buffer;
    private double[][] nextBuffer;
    private final static int bufferLen = 100;
    private final static int bufferOverlap = 25;
    //    private final static int bufferLen = 64;
//    private final static int bufferOverlap = 32;
    private int bufferIndex;

    private SensorManager     mSensorManager;
    private Sensor            mAccelerometer;
    private FeaturesExtractor featureExtractor_x;
    private FeaturesExtractor featureExtractor_y;
    private FeaturesExtractor featureExtractor_z;
//    private J48       tree;
    private Classifier cModel;
//    Classifier cModel;
    TextView tv_label;
    final ArrayList<String> labels = new ArrayList<String>(Arrays.asList("front", "back", "up", "down", "left", "right", "standing"));

    SoundSynthesizer synthesizer ; //synthesizer_E, synthesizer_A, synthesizer_D, synthesizer_G,
           // synthesizer_B, synthesizer_E_thick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar);

        tv_label = (TextView) findViewById(R.id.textView5);

        // For playing guitar sounds when acceleration detected
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // For Buffers
        buffer = new double[3][bufferLen];
        nextBuffer = new double[3][bufferLen];
        bufferIndex = 0;

        featureExtractor_x = null;
        featureExtractor_y = null;
        featureExtractor_z = null;

        synthesizer = new SoundSynthesizer();

        // deserialize model
        try{
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(Environment.getExternalStorageDirectory()+"/Download/classifier.model"));
            cModel = (Classifier) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Downloads/classifier.model not found. Run TrainingActivity First.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e){

        }


        FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.string_button_1);
        FloatingActionButton fab2 = (FloatingActionButton)findViewById(R.id.string_button_2);
        FloatingActionButton fab3 = (FloatingActionButton)findViewById(R.id.string_button_3);
        FloatingActionButton fab4 = (FloatingActionButton)findViewById(R.id.string_button_4);
        FloatingActionButton fab5 = (FloatingActionButton)findViewById(R.id.string_button_5);
        FloatingActionButton fab6 = (FloatingActionButton)findViewById(R.id.string_button_6);
        FloatingActionButton fab7 = (FloatingActionButton)findViewById(R.id.string_button_7);
        FloatingActionButton fab8 = (FloatingActionButton)findViewById(R.id.string_button_8);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(1);
                synthesizer.setNote("C",false);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(2);
                synthesizer.setNote("D",false);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(3);
                synthesizer.setNote("E",false);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(4);
                synthesizer.setNote("F",false);
            }
        });
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(5);
                synthesizer.setNote("G",false);
            }
        });
        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(6);
                synthesizer.setNote("A",false);
            }
        });
        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(7);
                synthesizer.setNote("B",false);
            }
        });
        fab8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(8);
                synthesizer.setNote("C+",false);
            }
        });

        final Button goToChordBtn = (Button)findViewById(R.id.go_to_chord);
        goToChordBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(GuitarActivity.this, ChordActivity.class));
            }
        });

    }

    public void startAllAnimation(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_guitar);
        startRippleAnimation(index);
        startColorTransition(index);
    }

    private void startColorTransition(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_guitar);

        int color = Color.TRANSPARENT;
        Drawable background = relativeLayout.getBackground();
        if (background instanceof ColorDrawable) color = ((ColorDrawable) background).getColor();
        int colorStart = color;
        int colorEnd = Color.TRANSPARENT;
        switch (index) {
            case 1:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton1);
                break;
            case 2:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton2);
                break;
            case 3:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton3);
                break;
            case 4:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton4);
                break;
            case 5:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton5);
                break;
            case 6:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton6);
                break;
            case 7:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton7);
                break;
            case 8:
                colorEnd =  ContextCompat.getColor(getApplicationContext(), R.color.colorGuitarButton8);
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
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_1);
                break;
            case 2:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_2);
                break;
            case 3:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_3);
                break;
            case 4:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_4);
                break;
            case 5:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_5);
                break;
            case 6:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_6);
                break;
            case 7:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_7);
                break;
            case 8:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_8);
                break;
            default:
                rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_1);
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
                long timeInterval = event.timestamp - lastTimeStamp;
                if (timeInterval > 5) {
                    buffer[0][bufferIndex] = values[0];
                    buffer[1][bufferIndex] = values[1];
                    buffer[2][bufferIndex] = values[2];
                    last_x = values[0];
                    last_y = values[1];
                    last_z = values[2];
                    lastTimeStamp = event.timestamp;
                    bufferIndex += 1;
                    //buffer is full
                    if (bufferIndex == bufferLen) {
                        bufferisReady = true;
                        //copy values into new buffer
                        for (int i = 0; i < bufferOverlap; i++) {
                            for (int j = 0; j < 3; j++) {
                                nextBuffer[j][i] = buffer[j][i + bufferOverlap];
                            }
                        }
                        bufferIndex = bufferOverlap;
                    }
                }

            }
        }
    }

    private void doSomeCalculations(double[][] buffer){
        List<Double> combined_features = new ArrayList<Double>();
        try {
            Log.v("Buffer is ", Arrays.toString(buffer[0]));
            featureExtractor_x = new FeaturesExtractor(buffer[0], 48);
            featureExtractor_y = new FeaturesExtractor(buffer[1], 48);
            featureExtractor_z = new FeaturesExtractor(buffer[2], 48);
        } catch (Exception e) {
            Toast.makeText(this, "FeaturesExtractor cannot launch", Toast.LENGTH_SHORT).show();
        }

        try {
            combined_features.clear();
            combined_features.addAll(featureExtractor_x.calculateFeatuers());
            combined_features.addAll(featureExtractor_y.calculateFeatuers());
            combined_features.addAll(featureExtractor_z.calculateFeatuers());
            System.out.println(Arrays.toString(combined_features.toArray()));
        } catch (Exception e) {
            Toast.makeText(this, "Features problem", Toast.LENGTH_SHORT).show();
        }
        classify(combined_features);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        synthesizer.stop();
    }

    private void classify(List<Double> list){
        // Call machine learning to classify new buffer features
            ActionClassifier ac = new ActionClassifier();
            double result = ac.classify(cModel, list);
            int x = (int) result;
            String action_label = labels.get(x);
            tv_label.setText(action_label);

            // Modulate the sound based on result
            modulateSoundBasedOnAction(action_label);

    }
    public void modulateSoundBasedOnAction(String action_label){
//        if (action_label.equalsIgnoreCase("front")){
//            synthesizer.longPlay();
//        } else if (action_label.equalsIgnoreCase("back")){
//            synthesizer.shortPlay();
//        } else if (action_label.equalsIgnoreCase("left")) {
//            octaveValue = synthesizer.makeHigher();
//        } else if (action_label.equalsIgnoreCase("right")) {
//            octaveValue = synthesizer.makeLower();
//        } else if (action_label.equalsIgnoreCase("up")) {
//            //increase volume
//            soundVolume = synthesizer.makeLouder();
//        } else if (action_label.equalsIgnoreCase("down")) {
//            // decrease volume
//            soundVolume = synthesizer.makeSofter();
//        } else{
//            // TODO: Do nothing
//            synthesizer.shortPlay();
//        }
        synthesizer.shortPlay();
    }


}

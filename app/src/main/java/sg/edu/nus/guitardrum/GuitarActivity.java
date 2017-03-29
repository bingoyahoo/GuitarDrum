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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Debug;
import weka.core.Instances;

import static android.R.attr.direction;
import static android.R.id.list;

/*
            Make sure to Sync Project With Gradle Files (Tools -> Android -> Sync Project With Gradle Files) and rebuild the app
            for usage of RippleBackground refer to: https://github.com/skyfishjy/android-ripple-background
*/

public class GuitarActivity extends AppCompatActivity implements SensorEventListener {
    /** Called when the activity is first created. */
    private static final String TAG = "Libsvm";

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
    private final static int bufferLen = 100;
    private final static int bufferOverlap = 1;
    //    private final static int bufferLen = 64;
//    private final static int bufferOverlap = 32;
    private int bufferIndex;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private FeaturesExtractor featureExtractor_x;
    private FeaturesExtractor featureExtractor_y;
    private FeaturesExtractor featureExtractor_z;
    private J48 tree;

    final ArrayList<String> labels = new ArrayList<String>(Arrays.asList("front", "back", "up", "down", "left", "right", "standing"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar);

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

        BufferedReader reader = null;
        try {
            String trainingFileLoc = Environment.getExternalStorageDirectory()+"/Download/training.arff";
            reader = new BufferedReader(new FileReader(trainingFileLoc));
            Instances data = new Instances(reader);
            reader.close();
            data.setClassIndex(data.numAttributes() - 1);

            String[] options = new String[1];
            options[0] = "-U";            // unpruned tree
            tree = new J48();         // new instance of tree
            tree.setOptions(options);     // set the options
            tree.buildClassifier(data);   // build classifier


            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree, data, 10, new Debug.Random(1));
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            Toast.makeText(this, "Success Trained!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){

        }

            FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.string_button_1);
        FloatingActionButton fab2 = (FloatingActionButton)findViewById(R.id.string_button_2);
        FloatingActionButton fab3 = (FloatingActionButton)findViewById(R.id.string_button_3);
        FloatingActionButton fab4 = (FloatingActionButton)findViewById(R.id.string_button_4);
        FloatingActionButton fab5 = (FloatingActionButton)findViewById(R.id.string_button_5);
        FloatingActionButton fab6 = (FloatingActionButton)findViewById(R.id.string_button_6);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(1);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(2);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(3);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(4);
            }
        });
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(5);
            }
        });
        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllAnimation(6);
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

    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 1000;
    private boolean startRecording = false;
    private long startTime = 0;
    private int count = 0;
    private double samplingRate = 0.0;
    private ArrayList<Long> timestampList = new ArrayList<Long>();
    private ArrayList<String> list1 = new ArrayList<String>();
//
//    public void onSensorChanged(SensorEvent event) {
//        Sensor mySensor = event.sensor;
//        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
////            System.out.println("here");
//                float x = event.values[0];
//                float y = event.values[1];
//                float z = event.values[2];
//
//                long curTime = System.currentTimeMillis();
//
//                if ((curTime - lastUpdate) > 100) {
//                    long diffTime = (curTime - lastUpdate);
//                    lastUpdate = curTime;
//
//                    double speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//
////                    if (speed > SHAKE_THRESHOLD) {
//
//                        if (startRecording == false) {
//
//                            startTime = System.currentTimeMillis();
//                            startRecording = true;
//                        }
////                        setCoordinates(x, y, z);
//                        buffer[0][count] = x;
//                        buffer[1][count] = y;
//                        buffer[2][count] = z;
//                        long timeInMillis = (new Date()).getTime()
//                                + (event.timestamp - System.nanoTime()) / 1000000L;
//                        timestampList.add(timeInMillis);
//                        float[] coords = {x, y, z};
//                        //BigDecimal result;
//                        //result = round(x,6);
//                        list1.add(Arrays.toString(coords));
//                        if (count == 99) {
//                            long now = System.currentTimeMillis();
//                            samplingRate = (count+1.0) / ((now - startTime) / 1000.0);
//                            doSomeCalculations(buffer);
//                            count = 0;
//                            startRecording = false;
//                            list1.clear();
//                        }
//                    count += 1;
//
//                    count = count % bufferLen;
////                    } // end of checking of SHAKE_THRESHOLD
//
//                    last_x = x;
//                    last_y = y;
//                    last_z = z;
//                }
//        } // end of checking if sensor is accelerometer
//    }

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
        List<Double> combined_features = new ArrayList<Double>();
        try {
            Log.v("Buffer is ", Arrays.toString(buffer[0]));
            featureExtractor_x = new FeaturesExtractor(buffer[0], samplingRate);
            featureExtractor_y = new FeaturesExtractor(buffer[1], samplingRate);
            featureExtractor_z = new FeaturesExtractor(buffer[2], samplingRate);
        } catch (Exception e) {
            Toast.makeText(this, "FeaturesExtractor cannot launch", Toast.LENGTH_SHORT).show();
        }

        try {
            combined_features.clear();
            combined_features.addAll(featureExtractor_x.calculateFeatuers());
            combined_features.addAll(featureExtractor_y.calculateFeatuers());
            combined_features.addAll(featureExtractor_z.calculateFeatuers());
            System.out.println(Arrays.toString(combined_features.toArray()));
            classify(combined_features);
//            d.calculateFeatuersMean();
        } catch (Exception e) {
            Toast.makeText(this, "Features problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void classify(List<Double> list){

//
//            Double[] doubleArray = {7.455246499373751,60.0,0.29087540648495147,370.59837721885856,
//                    39.150463030765216,12.734256465601685,60.0,0.18091646650363757,385.34431007080315,
//                    31.89150908203104, 5.351423635325828,53.0,0.0017771684478375433,319.40381718791707,
//                    31.948380719894285}; //up. Expected: 5.0
////            Double[] doubleArray = {4.600646720617296,36.0,-0.12234045583418018,292.38929804443194,
////                    24.408694383140627,7.724959309821786,0.0,-0.8577749609120245,475.6558379766474,
////                    2.8731960701758026,14.837271391181428,66.0,0.5127651790434414,348.79690502685065,
////                    43.45089525554437}; // back. Expected: 0.0
//            List<Double> list = Arrays.asList(doubleArray);



            ActionClassifier ac = new ActionClassifier();
            double result = ac.classify(tree, list);
            System.out.println("CLASSIFICATION: " + String.valueOf(result));
            int x = (int) result;
            Toast.makeText(this, "Class is " + labels.get(x), Toast.LENGTH_SHORT).show();

        // setting class attribute
    }
//
//    private void train() {
//        // Svm training
//        int kernelType = 2; // Radial basis function
//        int cost = 4; // Cost
//        int isProb = 0;
//        float gamma = 0.25f; // Gamma
//        String trainingFileLoc = Environment.getExternalStorageDirectory()+"/Download/training_set";
//        String modelFileLoc = Environment.getExternalStorageDirectory()+"/model";
//        if (trainClassifierNative(trainingFileLoc, kernelType, cost, gamma, isProb,
//                modelFileLoc) == -1) {
//            Log.d(TAG, "training err");
//            finish();
//        }
//        Toast.makeText(this, "Training is done", 2000).show();
//    }
//
//    /**
//     * classify generate labels for features.
//     * Return:
//     * 	-1: Error
//     * 	0: Correct
//     */
//    public int callSVM(float values[][], int indices[][], int groundTruth[], int isProb, String modelFile,
//                       int labels[], double probs[]) {
//        // SVM type
//        final int C_SVC = 0;
//        final int NU_SVC = 1;
//        final int ONE_CLASS_SVM = 2;
//        final int EPSILON_SVR = 3;
//        final int NU_SVR = 4;
//
//        // For accuracy calculation
//        int correct = 0;
//        int total = 0;
//        float error = 0;
//        float sump = 0, sumt = 0, sumpp = 0, sumtt = 0, sumpt = 0;
//        float MSE, SCC, accuracy;
//
//        int num = values.length;
////    	int svm_type = C_SVC;
//        int svm_type = NU_SVC;
//        if (num != indices.length)
//            return -1;
//        // If isProb is true, you need to pass in a real double array for probability array
//        int r = doClassificationNative(values, indices, isProb, modelFile, labels, probs);
//
//        // Calculate accuracy
//        if (groundTruth != null) {
//            if (groundTruth.length != indices.length) {
//                return -1;
//            }
//            for (int i = 0; i < num; i++) {
//                int predict_label = labels[i];
//                int target_label = groundTruth[i];
//                if(predict_label == target_label)
//                    ++correct;
//                error += (predict_label-target_label)*(predict_label-target_label);
//                sump += predict_label;
//                sumt += target_label;
//                sumpp += predict_label*predict_label;
//                sumtt += target_label*target_label;
//                sumpt += predict_label*target_label;
//                ++total;
//            }
//
//            if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
//            {
//                MSE = error/total; // Mean square error
//                SCC = ((total*sumpt-sump*sumt)*(total*sumpt-sump*sumt)) / ((total*sumpp-sump*sump)*(total*sumtt-sumt*sumt)); // Squared correlation coefficient
//            }
//            accuracy = (float)correct/total*100;
//            Log.d(TAG, "Classification accuracy is " + accuracy);
//        }
//
//        return r;
//    }
//
//    private void classify() {
//        // Svm classification
//        float[][] values = {
//                {-0.0237059f ,0.0345867f ,0.697105f ,-0.694704f ,-0.45f ,-1f ,-0.208179f ,-1f ,-1f }
//                ,{-0.581207f ,-0.359398f ,0.567929f ,0.140187f ,0.0178582f ,-0.777778f ,-0.527881f ,-1f ,-1f }
//                ,{-0.517123f ,-0.275188f ,0.536748f ,-0.0841122f ,-0.0464275f ,-0.806763f ,-0.451673f ,-1f ,-0.333333f }
//                ,{-0.118519f ,-0.326316f ,-0.853007f ,-0.239875f ,0.278571f ,-0.958132f ,0.0855019f ,-1f ,0.0980392f }
//                ,{-0.35294f  ,0.0105266f ,-0.0244988f ,-0.146417f ,0.0214277f ,-1f ,-0.276952f ,-1f ,-1f }
//                ,{0.0974609f ,0.521805f ,-0.184855f ,-0.364486f ,-0.778571f ,-0.900161f ,-0.408922f ,0.0666666f ,-1f }
//
//        };
//        int[][] indices = {
//                {1,2,3,4,5,6,7,8,9}
//                ,{1,2,3,4,5,6,7,8,9}
//                ,{1,2,3,4,5,6,7,8,9}
//                ,{1,2,3,4,5,6,7,8,9}
//                ,{1,2,3,4,5,6,7,8,9}
//                ,{1,2,3,4,5,6,7,8,9}
//        };
//        int[] groundTruth = {1, 2, 3, 5, 6, 7};
//
//        int[] labels = new int[6];
//        double[] probs = new double[6];
//        int isProb = 0; // Wheter is probability prediction
//        String modelFileLoc = Environment.getExternalStorageDirectory()+"/model";
//
//        if (callSVM(values, indices, groundTruth, isProb, modelFileLoc, labels, probs) != 0) {
//            Log.d(TAG, "Classification is incorrect");
//        }
//        else {
//            String m = "";
//            for (int l : labels)
//                m += l + ", ";
//            Toast.makeText(this, "Classification is done, the result is " + m, Toast.LENGTH_SHORT).show();
//        }
//    }


}

package sg.edu.nus.guitardrum;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ClassificationActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "Libsvm";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classification);
        Button trainButton    = (Button)findViewById(R.id.train);
        Button classifyButton = (Button)findViewById(R.id.classifiy);

//        trainButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                train();
//            }
//        });
//        classifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                classify();
//            }
//        });
    }

    private void train() {
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
        Toast.makeText(this, "Training is done", 2000).show();
    }

    /**
     * classify generate labels for features.
     * Return:
     * 	-1: Error
     * 	0: Correct
     */
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

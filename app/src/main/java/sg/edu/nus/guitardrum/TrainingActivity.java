package sg.edu.nus.guitardrum;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class TrainingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        final ArrayList<String> labels = new ArrayList<String>(Arrays.asList("front", "up", "right", "standing"));

        final Button trainingButton = (Button)findViewById(R.id.button_choose_text_file);
        trainingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String data = "";
                String data_arff = "";
                data_arff += "@RELATION physical_activity\n";
                data_arff += "@ATTRIBUTE X_RMS NUMERIC\n";
                data_arff += "@ATTRIBUTE X_ZC NUMERIC\n";
                data_arff += "@ATTRIBUTE X_LPC NUMERIC\n";
                data_arff += "@ATTRIBUTE X_CT NUMERIC\n";
                data_arff += "@ATTRIBUTE X_SC NUMERIC\n";
                data_arff += "@ATTRIBUTE X_CHROMA NUMERIC\n";
                data_arff += "@ATTRIBUTE X_SV NUMERIC\n";
                data_arff += "@ATTRIBUTE X_AVG NUMERIC\n";
                data_arff += "@ATTRIBUTE X_STD_DEV NUMERIC\n";
                data_arff += "@ATTRIBUTE X_INDEX_LARGEST NUMERIC\n";
                data_arff += "@ATTRIBUTE X_HIGHEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE X_INDEX_SMALLEST NUMERIC\n";
                data_arff += "@ATTRIBUTE X_SMALLEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE X_RANGE NUMERIC\n";

                data_arff += "@ATTRIBUTE Y_RMS NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_ZC NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_LPC NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_CT NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_SC NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_CHROMA NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_SV NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_AVG NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_STD_DEV NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_INDEX_LARGEST NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_HIGHEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_INDEX_SMALLEST NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_SMALLEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE Y_RANGE NUMERIC\n";

                data_arff += "@ATTRIBUTE Z_RMS NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_ZC NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_LPC NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_CT NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_SC NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_CHROMA NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_SV NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_AVG NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_STD_DEV NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_INDEX_LARGEST NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_HIGHEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_INDEX_SMALLEST NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_SMALLEST_VALUE NUMERIC\n";
                data_arff += "@ATTRIBUTE Z_RANGE NUMERIC\n";

                data_arff += "@ATTRIBUTE class {front,up,right,standing}\n";
                data_arff += "\n";
                data_arff += "@DATA\n";
                InputStream inputStream = TrainingActivity.this.getResources().openRawResource(R.raw.results);

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Do reading, usually loop until end of file reading
                StringBuilder sb = new StringBuilder();
                ArrayList<Double> features;
                try{
                    String mLine = reader.readLine();
//                    System.out.println("First line " + mLine);
                    int counter = 0;
                    int xyz_counter = 0; // 0 to 2 corresponding to x, y or z
                    double sampling_rate = 0.0;
                    String label = "Unknown";

                    while (mLine != null) {

                        sb.append(mLine);
                        // process line
                        mLine = reader.readLine();

                        counter += 1;
                        counter = counter % 5;
                        if (mLine == null){ // End of file
                            break;
                        }
                        //File should have a series of 5 lines: fs, label, x, y, z
                        // Add newline every 1st line
                        if (counter == 0){
                            data = data.concat("\n");
                            sampling_rate = Double.valueOf(mLine);
                        } else if (counter == 1){ // Add label every 2nd line
                            label = mLine.toLowerCase();
                            data = data.concat(String.valueOf(labels.indexOf(label)) + " ");
                        } else {
                            // Call features extractor every 3rd to 5th line
                            // Read in data from text file, convert to Double and extract FEATURES
                            String[] readings = mLine.split(" ");
                            double[] numbers = new double[readings.length];
                            for (int i = 0; i < readings.length; ++i) {
                                double number = Double.parseDouble(readings[i]);
                                numbers[i] = number;
                            }
                            xyz_counter = xyz_counter % 3;

                            // Call features extractor every 3rd to 5th line
                            FeaturesExtractor fe = new FeaturesExtractor(numbers, sampling_rate);
                            features = fe.calculateFeatuers();
                            for (int j=1; j<= features.size(); j++){
                                data = data.concat(String.valueOf(j + (xyz_counter * features.size()) + ":" + String.valueOf(features.get(j-1)) + " "));
                                data_arff = data_arff.concat(String.valueOf(features.get(j-1)) + ",");

                                if (j +(xyz_counter * features.size()) == 3 * features.size()){
                                    data_arff = data_arff.concat(label+"\n");
                                }
                            }
                            data = data.trim();
                            xyz_counter += 1;
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TrainingActivity.this, "The text file format is incorrect", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                writeToFile(data, TrainingActivity.this, "training.txt");
                writeToFile(data_arff, TrainingActivity.this, "training.arff");

                BufferedReader reader1 = null;
                try {
                    String trainingFileLoc = Environment.getExternalStorageDirectory()+"/Download/training.arff";
                    reader1 = new BufferedReader(new FileReader(trainingFileLoc));
                    Instances data1 = new Instances(reader1);
                    reader1.close();
                    data1.setClassIndex(data1.numAttributes() - 1);

                    String[] options = new String[2];
                    options[0] = "-I"; // Fix number of logitboost
                    options[1] = "-1"; // Fix number of logitboost
                    Classifier cModel = new RandomForest();
                    cModel.buildClassifier(data1);   // build classifier

                    // serialize model
                    ObjectOutputStream oos = new ObjectOutputStream(
                            new FileOutputStream(Environment.getExternalStorageDirectory()+"/Download/classifier.model"));
                    oos.writeObject(cModel);
                    oos.flush();
                    oos.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){

                }
            }

        });
    }

    private void writeToFile(String data,Context context, String outputfile) {
        try {
            // Get the directory for the user's public Downloads directory.
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), outputfile);

            FileOutputStream   fOut        = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(context, "Success! Saved into Downloads/" + outputfile , Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}

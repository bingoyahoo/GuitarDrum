package sg.edu.nus.guitardrum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SimpleLogistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by delvinlow on 29/3/17.
 */

public class ActionClassifier {
    private Instance inst_co;
    public static int NUM_ATTRIBUTES = 42;

    public double classify(Classifier cls_co, List<Double> features_values)  {

        // Create attributes to be used with classifiers
        // Test the model
        double result = -1;
        try {

            ArrayList<Attribute> attributeList = new ArrayList<Attribute>();

            Attribute X_RMS = new Attribute("X_RMS");
            Attribute X_ZC = new Attribute("X_ZC");
            Attribute X_LPC = new Attribute("X_LPC");
            Attribute X_CT = new Attribute("X_CT");
            Attribute X_SC = new Attribute("X_SC");
            Attribute X_CHROMA = new Attribute("X_CHROMA");
            Attribute X_SV = new Attribute("X_SV");
            Attribute X_AVG = new Attribute("X_AVG");
            Attribute X_STD_DEV = new Attribute("X_STD_DEV");
            Attribute X_INDEX_LARGEST = new Attribute("X_INDEX_LARGEST");
            Attribute X_HIGHEST_VALUE = new Attribute("X_HIGHEST_VALUE");
            Attribute X_INDEX_SMALLEST = new Attribute("X_INDEX_SMALLEST");
            Attribute X_SMALLEST_VALUE = new Attribute("X_SMALLEST_VALUE");
            Attribute X_RANGE = new Attribute("X_RANGE");

            Attribute Y_RMS = new Attribute("Y_RMS");
            Attribute Y_ZC = new Attribute("Y_ZC");
            Attribute Y_LPC = new Attribute("Y_LPC");
            Attribute Y_CT = new Attribute("Y_CT");
            Attribute Y_SC = new Attribute("Y_SC");
            Attribute Y_CHROMA = new Attribute("Y_CHROMA");
            Attribute Y_SV = new Attribute("Y_SV");
            Attribute Y_AVG = new Attribute("Y_AVG");
            Attribute Y_STD_DEV = new Attribute("Y_STD_DEV");
            Attribute Y_INDEX_LARGEST = new Attribute("Y_INDEX_LARGEST");
            Attribute Y_HIGHEST_VALUE = new Attribute("Y_HIGHEST_VALUE");
            Attribute Y_INDEX_SMALLEST = new Attribute("Y_INDEX_SMALLEST");
            Attribute Y_SMALLEST_VALUE = new Attribute("Y_SMALLEST_VALUE");
            Attribute Y_RANGE = new Attribute("Y_RANGE");

            Attribute Z_RMS = new Attribute("Z_RMS");
            Attribute Z_ZC = new Attribute("Z_ZC");
            Attribute Z_LPC = new Attribute("Z_LPC");
            Attribute Z_CT = new Attribute("Z_CT");
            Attribute Z_SC = new Attribute("Z_SC");
            Attribute Z_CHROMA = new Attribute("Z_CHROMA");
            Attribute Z_SV = new Attribute("Z_SV");
            Attribute Z_AVG = new Attribute("Z_AVG");
            Attribute Z_STD_DEV = new Attribute("Z_STD_DEV");
            Attribute Z_INDEX_LARGEST = new Attribute("Z_INDEX_LARGEST");
            Attribute Z_HIGHEST_VALUE = new Attribute("Z_HIGHEST_VALUE");
            Attribute Z_INDEX_SMALLEST = new Attribute("Z_INDEX_SMALLEST");
            Attribute Z_SMALLEST_VALUE = new Attribute("Z_SMALLEST_VALUE");
            Attribute Z_RANGE = new Attribute("Z_RANGE");

            ArrayList<String> classVal = new ArrayList<String>();
            final ArrayList<String> labels = new ArrayList<String>(
                    Arrays.asList("front", "up", "right", "standing"));
            classVal.add("front");
//            classVal.add("back");
            classVal.add("up");
//            classVal.add("down");
//            classVal.add("left");
            classVal.add("right");
            classVal.add("standing");


            attributeList.add(X_RMS);
            attributeList.add(X_ZC);
            attributeList.add(X_LPC);
            attributeList.add(X_CT);
            attributeList.add(X_SC);
            attributeList.add(X_CHROMA);
            attributeList.add(X_SV);
            attributeList.add(X_AVG);
            attributeList.add(X_STD_DEV);
            attributeList.add(X_INDEX_LARGEST);
            attributeList.add(X_HIGHEST_VALUE);
            attributeList.add(X_INDEX_SMALLEST);
            attributeList.add(X_SMALLEST_VALUE);
            attributeList.add(X_RANGE);

            attributeList.add(Y_RMS);
            attributeList.add(Y_ZC);
            attributeList.add(Y_LPC);
            attributeList.add(Y_CT);
            attributeList.add(Y_SC);
            attributeList.add(Y_CHROMA);
            attributeList.add(Y_SV);
            attributeList.add(Y_AVG);
            attributeList.add(Y_STD_DEV);
            attributeList.add(Y_INDEX_LARGEST);
            attributeList.add(Y_HIGHEST_VALUE);
            attributeList.add(Y_INDEX_SMALLEST);
            attributeList.add(Y_SMALLEST_VALUE);
            attributeList.add(Y_RANGE);

            attributeList.add(Z_RMS);
            attributeList.add(Z_ZC);
            attributeList.add(Z_LPC);
            attributeList.add(Z_CT);
            attributeList.add(Z_SC);
            attributeList.add(Z_CHROMA);
            attributeList.add(Z_SV);
            attributeList.add(Z_AVG);
            attributeList.add(Z_STD_DEV);
            attributeList.add(Z_INDEX_LARGEST);
            attributeList.add(Z_HIGHEST_VALUE);
            attributeList.add(Z_INDEX_SMALLEST);
            attributeList.add(Z_SMALLEST_VALUE);
            attributeList.add(Z_RANGE);

            attributeList.add(new Attribute("class",classVal));


            //Create empty testing set and set class index
            Instances data = new Instances("physical_activity", attributeList, 1);
            data.setClassIndex(NUM_ATTRIBUTES); // CHANGE THIS IF ATTRIBUTES CHANGE

            // Create instances for each pollutant with attribute values latitude,
            // longitude and pollutant itself
            Instance inst_co = new DenseInstance(data.numAttributes());

            // Set instance's values for the attributes "latitude", "longitude", and
            // "pollutant concentration"
            inst_co.setValue(X_RMS, features_values.get(0));
            inst_co.setValue(X_ZC, features_values.get(1));
            inst_co.setValue(X_LPC, features_values.get(2));
            inst_co.setValue(X_CT, features_values.get(3));
            inst_co.setValue(X_SC, features_values.get(4));
            inst_co.setValue(X_CHROMA, features_values.get(5));
            inst_co.setValue(X_SV, features_values.get(6));
            inst_co.setValue(X_AVG, features_values.get(7));
            inst_co.setValue(X_STD_DEV, features_values.get(8));
            inst_co.setValue(X_INDEX_LARGEST, features_values.get(9));
            inst_co.setValue(X_HIGHEST_VALUE, features_values.get(10));
            inst_co.setValue(X_INDEX_SMALLEST, features_values.get(11));
            inst_co.setValue(X_SMALLEST_VALUE, features_values.get(12));
            inst_co.setValue(X_RANGE, features_values.get(13));


            inst_co.setValue(Y_RMS, features_values.get(14));
            inst_co.setValue(Y_ZC, features_values.get(15));
            inst_co.setValue(Y_LPC, features_values.get(16));
            inst_co.setValue(Y_CT, features_values.get(17));
            inst_co.setValue(Y_SC, features_values.get(18));
            inst_co.setValue(Y_CHROMA, features_values.get(19));
            inst_co.setValue(Y_SV, features_values.get(20));
            inst_co.setValue(Y_AVG, features_values.get(21));
            inst_co.setValue(Y_STD_DEV, features_values.get(22));
            inst_co.setValue(Y_INDEX_LARGEST, features_values.get(23));
            inst_co.setValue(Y_HIGHEST_VALUE, features_values.get(24));
            inst_co.setValue(Y_INDEX_SMALLEST, features_values.get(25));
            inst_co.setValue(Y_SMALLEST_VALUE, features_values.get(26));
            inst_co.setValue(Y_RANGE, features_values.get(27));

            inst_co.setValue(Z_RMS, features_values.get(28));
            inst_co.setValue(Z_ZC, features_values.get(29));
            inst_co.setValue(Z_LPC, features_values.get(30));
            inst_co.setValue(Z_CT, features_values.get(31));
            inst_co.setValue(Z_SC, features_values.get(32));
            inst_co.setValue(Z_CHROMA, features_values.get(33));
            inst_co.setValue(Z_SV, features_values.get(34));
            inst_co.setValue(Z_AVG, features_values.get(35));
            inst_co.setValue(Z_STD_DEV, features_values.get(36));
            inst_co.setValue(Z_INDEX_LARGEST, features_values.get(37));
            inst_co.setValue(Z_HIGHEST_VALUE, features_values.get(38));
            inst_co.setValue(Z_INDEX_SMALLEST, features_values.get(39));
            inst_co.setValue(Z_SMALLEST_VALUE, features_values.get(40));
            inst_co.setValue(Z_RANGE, features_values.get(41));

//            for (int j = 0; j < features_values.size(); j++){
//                System.out.println(features_values.get(j));
//            }

            data.add(inst_co);
            // Specify the instance belong to training_Set to inherit headers
            inst_co.setDataset(data);
            if (cls_co == null){
                System.out.println("Classifier is null");
            } else {
                result = cls_co.classifyInstance(inst_co);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}

package sg.edu.nus.guitardrum;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import static android.R.attr.data;
import static android.R.attr.value;

/**
 * Created by delvinlow on 29/3/17.
 */

public class ActionClassifier {
    private Instance inst_co;

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

            Attribute Y_RMS = new Attribute("Y_RMS");
            Attribute Y_ZC = new Attribute("Y_ZC");
            Attribute Y_LPC = new Attribute("Y_LPC");
            Attribute Y_CT = new Attribute("Y_CT");
            Attribute Y_SC = new Attribute("Y_SC");

            Attribute Z_RMS = new Attribute("Z_RMS");
            Attribute Z_ZC = new Attribute("Z_ZC");
            Attribute Z_LPC = new Attribute("Z_LPC");
            Attribute Z_CT = new Attribute("Z_CT");
            Attribute Z_SC = new Attribute("Z_SC");

            ArrayList<String> classVal = new ArrayList<String>();
            classVal.add("back");
            classVal.add("down");
            classVal.add("front");
            classVal.add("left");
            classVal.add("right");
            classVal.add("up");
            classVal.add("standing");


            attributeList.add(X_RMS);
            attributeList.add(X_ZC);
            attributeList.add(X_LPC);
            attributeList.add(X_CT);
            attributeList.add(X_SC);

            attributeList.add(Y_RMS);
            attributeList.add(Y_ZC);
            attributeList.add(Y_LPC);
            attributeList.add(Y_CT);
            attributeList.add(Y_SC);

            attributeList.add(Z_RMS);
            attributeList.add(Z_ZC);
            attributeList.add(Z_LPC);
            attributeList.add(Z_CT);
            attributeList.add(Z_SC);
            attributeList.add(new Attribute("@@class@@",classVal));

            Instances data = new Instances("physical_activity", attributeList, 1);
            data.setClassIndex(15);

//            Instance data = new Instance();

            // Create instances for each pollutant with attribute values latitude,
            // longitude and pollutant itself
            inst_co = new DenseInstance(data.numAttributes());
            data.add(inst_co);

            // Set instance's values for the attributes "latitude", "longitude", and
            // "pollutant concentration"
            inst_co.setValue(X_RMS, features_values.get(0));
            inst_co.setValue(X_ZC, features_values.get(1));
            inst_co.setValue(X_LPC, features_values.get(2));
            inst_co.setValue(X_CT, features_values.get(3));
            inst_co.setValue(X_SC, features_values.get(4));

            inst_co.setValue(Y_RMS, features_values.get(5));
            inst_co.setValue(Y_ZC, features_values.get(6));
            inst_co.setValue(Y_LPC, features_values.get(7));
            inst_co.setValue(Y_CT, features_values.get(8));
            inst_co.setValue(Y_SC, features_values.get(9));

            inst_co.setValue(Z_RMS, features_values.get(10));
            inst_co.setValue(Z_ZC, features_values.get(11));
            inst_co.setValue(Z_LPC, features_values.get(12));
            inst_co.setValue(Z_CT, features_values.get(13));
            inst_co.setValue(Z_SC, features_values.get(14));

//            for (Double element : features_values) {
//                System.out.println("Element: " + String.valueOf(element));
//            }
            inst_co.setDataset(data);

            // inst_co.setMissing(cluster);
//
//            // load classifier from file
//            Classifier cls_co = (Classifier) weka.core.SerializationHelper
//                    .read("/CO_J48Model.model");

            result = cls_co.classifyInstance(inst_co);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}

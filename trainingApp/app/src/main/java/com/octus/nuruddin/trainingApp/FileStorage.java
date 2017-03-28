package com.octus.nuruddin.trainingApp;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by Nuruddin on 21/3/2017.
 */

public class FileStorage {

    FileOutputStream outputStream;
    String filename;

    // Checks if 'direction' folder exist
    private void createFolder(String direction){

        boolean folderExist = false;
        File f = new File(Environment.getExternalStorageDirectory(), direction);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    public void storeData(String direction, int fileCount, String data, Context ctx)
    {
        File file;
        FileOutputStream fOut;

        try{
            file = new File(Environment.getExternalStorageDirectory(), direction+fileCount+"-file.txt");
            fOut = new FileOutputStream(file);

            //ObjectOutputStream osw = new ObjectOutputStream(fOut);
            //osw.writeObject(data.getBytes());
            //osw.close();
            fOut.write(data.getBytes());
            fOut.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }
        /*
        try {
            FileOutputStream fOut = ctx.openFileOutput("samplefile.txt",
                    ctx.MODE_PRIVATE);
            ObjectOutputStream osw = new ObjectOutputStream(fOut);
            String h = ctx.getFilesDir().toString();

            // Write the string to the file
            osw.writeObject(data);

       /* ensure that everything is
        * really written out and close
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        */

    }
}

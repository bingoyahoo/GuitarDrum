package sg.edu.nus.guitardrum;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ImageButton guitarButton = (ImageButton)findViewById(R.id.imageButton_home_guitar);
        final ImageButton drumButton = (ImageButton)findViewById(R.id.imageButton_home_drum);

        final LinearLayout guitarView = (LinearLayout)findViewById(R.id.guitar_view);
        final LinearLayout drumView = (LinearLayout)findViewById(R.id.drum_view);

        // I recorded the guitar sound myself, hope it sounds ok haha
        // Drum beat taken from https://www.freesoundeffects.com/free-sounds/drum-loops-10031/
        final MediaPlayer guitarPlayer = MediaPlayer.create(this, R.raw.guitar_intro);
        final MediaPlayer drumPlayer = MediaPlayer.create(this, R.raw.drum_intro);


        /*
            To be added:
            Credit Page or other forms to credit the author
            Image assets of guitar and drum: Dmitry Ryabov (https://www.behance.net/gallery/29912703/Low-Poly-Musical-Instruments)
            License: CC BY_NY 4.0 (https://creativecommons.org/licenses/by-nc/4.0/deed.en_US)
         */


        /*
            Guitar ImageButton click animation and sound
         */
        guitarButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int oldPadding = (int) Util.dpToPx(getApplicationContext(), 25);
                int newPadding = (int) Util.dpToPx(getApplicationContext(), 5);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // detect if move outside the view
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        } else {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                            guitarPlayer.start();
                            // go to guitar page
                            startActivity(new Intent(HomeActivity.this, GuitarActivity.class));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            guitarView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        guitarView.setPadding(newPadding, newPadding, newPadding, newPadding);
                        break;
                }
                return false;
            }
        });

        /*
            Drum ImageButton click animation and sound
         */
        drumButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int oldPadding = (int)Util.dpToPx(getApplicationContext(), 35);
                int newPadding = (int)Util.dpToPx(getApplicationContext(), 5);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        } else {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                            drumPlayer.start();
                            startActivity(new Intent(HomeActivity.this, NewDrumActivity.class));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getX()<0 || motionEvent.getY()<0 || motionEvent.getX()>v.getMeasuredWidth() || motionEvent.getY()>v.getMeasuredHeight()) {
                            drumView.setPadding(oldPadding, oldPadding, oldPadding, oldPadding);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        drumView.setPadding(newPadding, newPadding, newPadding, newPadding);
                        break;
                }
                return false;
            }
        });
    }
}

package sg.edu.nus.guitardrum;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ImageButton          guitarButton   = (ImageButton)findViewById(R.id.imageButton_home_guitar);
        final ImageButton          drumButton     = (ImageButton)findViewById(R.id.imageButton_home_drum);
        final FloatingActionButton trainingButton = (FloatingActionButton)findViewById(R.id.training_button);

        final LinearLayout guitarView = (LinearLayout)findViewById(R.id.guitar_view);
        final LinearLayout drumView = (LinearLayout)findViewById(R.id.drum_view);

        // Drum beat taken from https://www.freesoundeffects.com/free-sounds/drum-loops-10031/

        /*
            To be added:
            Credit Page or other forms to credit the author
            Image assets of guitar and drum: Dmitry Ryabov (https://www.behance.net/gallery/29912703/Low-Poly-Musical-Instruments)
            License: CC BY_NY 4.0 (https://creativecommons.org/licenses/by-nc/4.0/deed.en_US)
         */
        trainingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent training_intent = new Intent(HomeActivity.this, TrainingActivity.class);
                startActivity(training_intent);
            }

        });

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

package sg.edu.nus.guitardrum;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.skyfishjy.library.*;

public class GuitarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar);

        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_guitar);
        /*
            Make sure to Sync Project With Gradle Files (Tools -> Android -> Sync Project With Gradle Files) and rebuild the app
            for usage of RippleBackground refer to: https://github.com/skyfishjy/android-ripple-background
         */
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ripple_bg_1);
        ImageView imageView=(ImageView)findViewById(R.id.center_button_1);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.string_button_1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rippleBackground.startRippleAnimation();
                //relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorGuitarButton1));
                int colorStart = relativeLayout.getSolidColor();
                int colorEnd = R.color.colorGuitarButton6;

                final ValueAnimator colorAnim = new ValueAnimator();

                colorAnim.setDuration(1000);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setIntValues(colorStart, colorEnd);
                colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        relativeLayout.setBackgroundColor((int) colorAnim.getAnimatedValue());
                    }
                });
                colorAnim.start();
//
//                rippleBackground.startRippleAnimation();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        rippleBackground.stopRippleAnimation();
//                    }
//                }, 1500);

            }
        });



    }
}

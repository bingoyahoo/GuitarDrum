package sg.edu.nus.guitardrum;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.skyfishjy.library.RippleBackground;

public class ChordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord);

        FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.chord_button_1);
        FloatingActionButton fab2 = (FloatingActionButton)findViewById(R.id.chord_button_2);
        FloatingActionButton fab3 = (FloatingActionButton)findViewById(R.id.chord_button_3);
        FloatingActionButton fab4 = (FloatingActionButton)findViewById(R.id.chord_button_4);

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

        final Button goToNoteBtn = (Button)findViewById(R.id.go_to_note);
        goToNoteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(ChordActivity.this, GuitarActivity.class));
            }
        });
    }

    public void startAllAnimation(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_chord);
        startRippleAnimation(index);
        startColorTransition(index);
    }

    private void startColorTransition(int index) {
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_chord);

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
                rippleBackground=(RippleBackground)findViewById(R.id.chord_ripple_bg_1);
                break;
            case 2:
                rippleBackground=(RippleBackground)findViewById(R.id.chord_ripple_bg_2);
                break;
            case 3:
                rippleBackground=(RippleBackground)findViewById(R.id.chord_ripple_bg_3);
                break;
            case 4:
                rippleBackground=(RippleBackground)findViewById(R.id.chord_ripple_bg_4);
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
}

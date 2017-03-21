package sg.edu.nus.guitardrum;

import android.content.Context;

/**
 * Created by Rain on 3/20/2017.
 */

public class Util {
    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    public static float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}

package tw.edu.ntu.ee.arbor.apphost;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Linzy on 2014/7/10.
 */
public class LauncherListener implements View.OnClickListener {

    private Context mContext;

    public LauncherListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View view) {
//        view.setVisibility(View.INVISIBLE);

        WindowManager wm = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        CircularView circularView = new CircularView(mContext);
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mWindowParams.gravity = Gravity.CENTER;
//        mWindowParams.x = 0;
//        mWindowParams.y = 100;
        mWindowParams.width = 400;
        mWindowParams.height = 400;

        wm.addView(circularView, mWindowParams);
    }
}

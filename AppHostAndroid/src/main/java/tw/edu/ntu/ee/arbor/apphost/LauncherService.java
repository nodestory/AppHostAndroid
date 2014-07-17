package tw.edu.ntu.ee.arbor.apphost;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Linzy on 2014/7/8.
 */
public class LauncherService extends Service {

    private CircularView mLauncherRootView;
    private ImageView mEntranceView;
    private static final int LAUNCHER_APP_NUM = 5;
    private ImageView[] mLauncherAppViews = new ImageView[LAUNCHER_APP_NUM + 1];

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;

    private PackageManager mPackageManager;
    private List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
    private static final String[] APP_PKG_NAMES = {"com.evernote",
            "com.evernote.skitch", "com.intsig.camscanner",
            "com.google.android.apps.docs", "com.merriamwebster"};
    private static final int[] APP_ICONS = {R.drawable.ic_evernote,
            R.drawable.ic_skitch, R.drawable.ic_camscanner,
            R.drawable.ic_google_drive, R.drawable.ic_merriamwebster};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LauncherListener listener = new LauncherListener();

        mLauncherRootView = new CircularView(this);

        mEntranceView = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(72, 72);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mEntranceView.setLayoutParams(layoutParams);
        mEntranceView.setId(R.id.center);
        mEntranceView.setBackgroundColor(Color.TRANSPARENT);
        mEntranceView.setImageResource(R.drawable.ic_circle);
        mEntranceView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mEntranceView.setOnClickListener(listener);
        mEntranceView.setOnTouchListener(listener);
        mLauncherRootView.addView(mEntranceView);

        for (int i = 0; i < LAUNCHER_APP_NUM + 1; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setVisibility(View.GONE);
            imageView.setOnClickListener(listener);
            mLauncherAppViews[i] = imageView;
            mLauncherRootView.addView(imageView);
        }
        mLauncherAppViews[LAUNCHER_APP_NUM].setId(R.id.imgv6);
        mLauncherAppViews[LAUNCHER_APP_NUM].setImageResource(R.drawable.ic_action_new);

        mWindowLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT);
        mWindowLayoutParams.gravity = Gravity.CENTER;
        mWindowManager.addView(mLauncherRootView, mWindowLayoutParams);

        mPackageManager = getPackageManager();
        for (ApplicationInfo info : mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
            if (mPackageManager.getLaunchIntentForPackage(info.packageName) != null) {
                installedApps.add(info);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLauncherRootView != null) mWindowManager.removeView(mLauncherRootView);
    }

    private void expandLauncher() {
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE;
        mWindowLayoutParams.width = 500;
        mWindowLayoutParams.height = 500;
        mWindowManager.updateViewLayout(mLauncherRootView, mWindowLayoutParams);

        for (ImageView appIconView : mLauncherAppViews) {
            appIconView.setVisibility(View.VISIBLE);
        }
    }

    private void updateLauncher(int mode) {
        if (mode == LauncherListener.MODE_PREDICTION) {
            ApplicationInfo[] predictions = getPredictions();
            for (int i = 0; i < LAUNCHER_APP_NUM; i++) {
                mLauncherAppViews[i].setImageDrawable(mPackageManager.getApplicationIcon(predictions[i]));
                mLauncherAppViews[i].setTag(predictions[i]);
            }
            mEntranceView.setImageResource(R.drawable.ic_prediction);
            mLauncherAppViews[LAUNCHER_APP_NUM].setImageResource(R.drawable.ic_action_new);
            mLauncherRootView.setBackgroundColor(getResources().getColor(R.color.color_launch));
        } else {
            int[] recommendations = getRecommendations();
            mEntranceView.setImageResource(R.drawable.ic_recommendation);
            mLauncherRootView.setBackgroundColor(getResources().getColor(R.color.color_recommend));
            for (int i = 0; i < LAUNCHER_APP_NUM; i++) {
                mLauncherAppViews[i].setImageResource(recommendations[i]);
                mLauncherAppViews[i].setTag(APP_PKG_NAMES[i]);
            }
            mLauncherAppViews[LAUNCHER_APP_NUM].setImageResource(R.drawable.ic_action_back);
        }
    }

    private void collapseLauncher() {
        mLauncherRootView.setBackgroundColor(Color.GRAY);

        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.width = mEntranceView.getWidth();
        mWindowLayoutParams.height = mEntranceView.getHeight();
        mWindowManager.updateViewLayout(mLauncherRootView, mWindowLayoutParams);

        for (ImageView appIconView : mLauncherAppViews) {
            appIconView.setVisibility(View.GONE);
        }

        mEntranceView.setImageResource(R.drawable.ic_circle);
        mEntranceView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private ApplicationInfo[] getPredictions() {
        Collections.shuffle(installedApps);
        ApplicationInfo[] predictions = new ApplicationInfo[LAUNCHER_APP_NUM];
        for (int i = 0; i < LAUNCHER_APP_NUM; i++) {
            predictions[i] = installedApps.get(i);
        }
        return predictions;
    }

    private int[] getRecommendations() {
        return APP_ICONS;
    }

    public class LauncherListener implements View.OnClickListener, View.OnTouchListener {
        boolean isExpanded = false;

        static final int MODE_PREDICTION = 0;
        static final int MODE_RECOMMENDATION = 1;
        int currentMode;

        int initialX;
        int initialY;
        float initialTouchX;
        float initialTouchY;

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.center) {
                currentMode = MODE_PREDICTION;
                if (!isExpanded) {
                    expandLauncher();
                    updateLauncher(currentMode);
                } else {
                    collapseLauncher();
                }
                isExpanded = !isExpanded;
            } else if (view.getId() == R.id.imgv6) {
                currentMode = (currentMode == MODE_PREDICTION ? MODE_RECOMMENDATION : MODE_PREDICTION);
                updateLauncher(currentMode);
            } else {
                if (currentMode == MODE_PREDICTION) {
                    ApplicationInfo info = (ApplicationInfo) view.getTag();
                    Intent intent = mPackageManager.getLaunchIntentForPackage(info.packageName);
                    startActivity(intent);
                } else {
                    String appPackageName = (String) view.getTag();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=" + appPackageName));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException anfe) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://play.google.com/store/apps/details?id=" + appPackageName));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                collapseLauncher();
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId() == R.id.center) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = mWindowLayoutParams.x;
                        initialY = mWindowLayoutParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_MOVE:
                        mWindowLayoutParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        mWindowLayoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mLauncherRootView, mWindowLayoutParams);
                }
                return false;
            }
            return true;
        }
    }
}
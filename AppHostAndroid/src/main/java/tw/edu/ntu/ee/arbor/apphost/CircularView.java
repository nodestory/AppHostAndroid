package tw.edu.ntu.ee.arbor.apphost;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Linzy on 2014/7/10.
 */
public class CircularView extends RelativeLayout {
    Context context;

    CircularView parent;
    float diameter;
    View v[];
    int radius; // Radius
    int startDeg;
    int childNum; // Number of Children View
    int backgroundColor;

    Paint paint;

    public CircularView(Context context) {
        super(context);

        this.context = context;
        // Need to set this for calling onDraw();
        // https://groups.google.com/forum/?fromgroups#!topic/android-developers/oLccWfszuUo
        this.setWillNotDraw(false);
        startDeg = 0;
//        parent = (CircularView) findViewById(R.id.circularview);
        parent = this;

//        ImageButton button = new ImageButton(context);
//        button.setId(R.id.center);
//        button.setImageResource(R.drawable.ic_launcher);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        button.setLayoutParams(relativeParams);
//        parent.addView(button);
        radius = 85;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        // Need to set this for calling onDraw();
        // https://groups.google.com/forum/?fromgroups#!topic/android-developers/oLccWfszuUo
        this.setWillNotDraw(false);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularView, 0, 0);
        try {
            startDeg = a.getInteger(
                    R.styleable.CircularView_startDeg, 0);
        } finally {
            a.recycle();
        }
        parent = (CircularView) findViewById(R.id.circularview);
        radius = 80;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        invalidate();
    }

    private RelativeLayout.LayoutParams modifyLayoutParams(
            RelativeLayout.LayoutParams lp, int degree) {
        // TODO:
        int offset = getChildAt(1).getWidth()/2;

        // Determine in Quadrant or on Axis
        degree += 30;
        degree = degree % 360;
        if (degree < 0) { // Make it positive
            degree += 360;
        }
        Log.d(getClass().getName(), String.valueOf(degree));
        if (degree == 0) { // Right x-axis.
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.center);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.setMargins(radius, 0, 0, 0);
        } else if (degree > 0 && degree < 90) { // Quadrant I.
            lp.addRule(RelativeLayout.ABOVE, R.id.center);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.center);
            lp.setMargins(getMarginX(degree), 0, 0, getMarginY(degree) - offset);
        } else if (degree == 90) { // Top y-axis.
            lp.addRule(RelativeLayout.ABOVE, R.id.center); // Above Center.
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            lp.setMargins(0, 0, 0, radius);
        } else if (degree > 90 && degree < 180) { // Quadrant II.
            lp.addRule(RelativeLayout.ABOVE, R.id.center);
            lp.addRule(RelativeLayout.LEFT_OF, R.id.center);
            lp.setMargins(0, 0, getMarginX(180 - degree), getMarginY(180 - degree) - offset);
        } else if (degree == 180) { // Left x-axis. Left
            lp.addRule(RelativeLayout.LEFT_OF, R.id.center);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.setMargins(0, 0, radius, 0);
        } else if (degree > 180 && degree < 270) { // Quadrant III.
            lp.addRule(RelativeLayout.BELOW, R.id.center);
            lp.addRule(RelativeLayout.LEFT_OF, R.id.center);
            lp.setMargins(0, getMarginY(degree - 180) - offset, getMarginX(degree - 180), 0);
        } else if (degree == 270) { // Bottom y-axis
            lp.addRule(RelativeLayout.BELOW, R.id.center); // Above Center.
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            lp.setMargins(0, radius, 0, 0);
        } else if (degree > 270 && degree < 360) { // Quadrant IV.
            lp.addRule(RelativeLayout.BELOW, R.id.center);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.center);
            lp.setMargins(getMarginX(360 - degree), getMarginY(360 - degree) - offset, 0, 0);
        }
        return lp;
    }

    /**
     * X offset i.e. adjacent length
     */
    private int getMarginX(int degree) {
        return Math.abs((int) (Math.cos(Math.toRadians(degree)) * radius));
    }

    /**
     * Y offset i.e. opposite length
     */
    private int getMarginY(int degree) {
        return Math.abs((int) (Math.sin(Math.toRadians(degree)) * radius));
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);



        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(200, Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
        c.drawCircle(getWidth()/2, getHeight()/2, radius + 2*getChildAt(1).getWidth(), paint);

        int edgeColor = getResources().getColor(R.color.color_edge);
        paint.setARGB(200, Color.red(edgeColor), Color.green(edgeColor), Color.blue(edgeColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        c.drawCircle(getWidth()/2, getHeight()/2, getChildAt(1).getWidth() + 5, paint);

//        RectF oval = new  RectF(); //RectF對象
//        oval.left = 0 ; //左邊
//        oval.top = 0 ; //上邊
//        oval.right = 2*(radius + 2*getChildAt(1).getWidth() + 15); //右邊
//        oval.bottom = 2*(radius + 2*getChildAt(1).getWidth() + 15); //下邊
//        paint.setColor(Color.GRAY);
//        c.drawArc(oval, 1, 59, false, paint);
//        c.drawArc(oval, 61, 59, false, paint);
//        c.drawArc(oval, 121, 60, false, paint);
//        c.drawArc(oval, 181, 60, false, paint);
//        c.drawArc(oval, 241, 60, false, paint);
//        c.drawArc(oval, 301, 60, false, paint);


        childNum = parent.getChildCount();
        if (childNum == 1) { // Only 1 children. i.e. No actual child view.
            // Only 1 children. i.e. No actual child view.
            // (It must be View ID="center")
            return;
        }

        float degree_between_views;
        if (childNum == 2) { // Only 1 actual child
            degree_between_views = 0;
        } else { // More than 1 child
            degree_between_views = (float) (360.0 / (childNum - 1));
        }
        for (int i = 1; i < childNum; i++) {
            if (parent.getChildAt(i).getId() != R.id.center) {
                parent.getChildAt(i).setLayoutParams(modifyLayoutParams
                        ((RelativeLayout.LayoutParams)
                                parent.getChildAt(i).getLayoutParams(),
                                (int) (startDeg + degree_between_views * (i - 1))));
            }
        }
    }
}
package com.action_animatorset.sample.evaluator;

/**
 * Created by yang on 2016/9/20.
 * paulyung@outlook.com
 */

public class AnimPoint {
    public float mEndX;
    public float mEndY;

    public float mCtlX1;
    public float mCtlY1;
    public float mCtlX2;
    public float mCtlY2;

    private AnimPoint(float ctlX1, float ctlY1, float ctlX2, float ctlY2, float endX, float endY) {
        mCtlX1 = ctlX1;
        mCtlY1 = ctlY1;
        mCtlX2 = ctlX2;
        mCtlY2 = ctlY2;
        mEndX = endX;
        mEndY = endY;
    }

    public AnimPoint(float x, float y) {
        mEndX = x;
        mEndY = y;
    }

    public static AnimPoint newInstance(float x, float y) {
        return new AnimPoint(x, y);
    }

    public static AnimPoint cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        return new AnimPoint(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public String toString() {
        return "AnimPoint{" +
                "mEndX=" + mEndX +
                ", mEndY=" + mEndY +
                '}';
    }
}

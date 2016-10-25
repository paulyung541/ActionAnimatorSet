package com.paul.actionanimset;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by yang on 2016/10/18.
 * paulyung@outlook.com
 * 触发点
 */
public class TriggerPoint<T> {
    private static final String TAG = "ActionAnimatorSet";
    private int value_int;
    private LinkedList<Float> v1v2;

    public TriggerPoint(int value) {
        value_int = value;
        v1v2 = new LinkedList<>();
    }

    public TriggerPoint() {
    }

    @Override
    public boolean equals(Object o) {
        boolean res = false;
        if (o instanceof Integer || o instanceof Float) {
            float v = (float) o;
            if (v1v2.size() >= 2) {
                v1v2.removeFirst();
                v1v2.addLast(v);
                res = calculate();
                if (BuildConfig.DEBUG && res)
                    Log.d(TAG, "equals: start!!! --- value is " + o);
            } else {
                v1v2.addLast(v);
            }
        } else {
            res = whenToStart((T) o);
        }
        return res;
    }

    /**
     * if the param is not a value-type such as int and float etc, you must
     * overwrite this method.It would be best to use '>' or '<', but not to
     * use a '=',because the value could not be so accurate.
     * */
    public boolean whenToStart(T o) {
        return false;
    }

    //compare the set-value and calculate-value whether effective or not
    private boolean calculate() {
        return Math.abs(v1v2.getFirst() - value_int) < Math.abs(v1v2.getLast() - value_int);
    }

    void clear() {
        if (!v1v2.isEmpty())
            v1v2.clear();
    }
}

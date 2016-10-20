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

    //如果传入的不是普通类型，则需重写此方法
    //在方法内判断时尽量使用 '>'  而不要使用 '='
    //此时这样做，精确度不如普通类型经过计算找的那个点准确
    //但是也不会差远，是距离设定值最近的那两个点的较大一个的值对应的时刻触发动画
    public boolean whenToStart(T o) {
        return false;
    }

    //计算什么时候和设定值最接近
    private boolean calculate() {
        return Math.abs(v1v2.getFirst() - value_int) < Math.abs(v1v2.getLast() - value_int);
    }

    void clear() {
        if (!v1v2.isEmpty())
            v1v2.clear();
    }
}

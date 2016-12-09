package com.paul.actionanimset;

/**
 * Created by yang on 2016/12/9.
 * paulyung@outlook.com
 */

public interface Animator {
    void setDuration(String tag, long duration);

    void start(String tag);

    void cancel(String tag);

    void setStartDelay(String tag, long startDelay);
}

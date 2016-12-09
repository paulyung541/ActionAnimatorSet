package com.paul.actionanimset;

import android.animation.Animator;

/**
 * Created by yang on 2016/12/9.
 * paulyung@outlook.com
 */

public interface AnimatorManager extends com.paul.actionanimset.Animator {
    /**
     * @return return the {@link ActionAnimatorSet} Object to handle a group of Animator
     */
    ActionAnimatorSet createAnimatorSet();

    /**
     * @param anim the Animator will be handled later, if you want to handle Animator more convenient,
     *             should you call this method.
     * @param tag  to find Animator by calling {@link AnimatorManager#getAnimator(String)},
     *             when you want to handle it.
     */
    void addAnimator(Animator anim, String tag);

    /**
     * @param tag get the Animator, if you add it before by {@link AnimatorManager#addAnimator(Animator, String)}
     * @return if you have not add it, return null
     */
    Animator getAnimator(String tag);

    void setOnStartListener(String tag, Action start);

    void setOnEndListener(String tag, Action end);

    void setStartAndEndListener(String tag, Action start, Action end);

    void build();
}

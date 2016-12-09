package com.paul.actionanimset;

import android.animation.Animator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/12/9.
 * paulyung@outlook.com
 */

public class DefaultAnimatorManager implements AnimatorManager {
    Map<String, ActionNode> mAnimators;

    public DefaultAnimatorManager() {
        mAnimators = new HashMap<>();
    }

    @Override
    public ActionAnimatorSet createAnimatorSet() {
        return ActionAnimatorSet.getInstance();
    }

    @Override
    public void addAnimator(Animator anim, String tag) {
        if (!mAnimators.containsKey(anim)) {
            ActionNode node = new ActionNode();
            node.mAnim = anim;
            mAnimators.put(tag, node);
        }
    }

    //最好不要直接操作Animator，尽量用 AnimatorManager 的API进行操作
    @Deprecated
    @Override
    public Animator getAnimator(String tag) {
        return mAnimators.get(tag).mAnim;
    }

    @Override
    public void setOnStartListener(String tag, Action start) {
        mAnimators.get(tag).startAciton = start;
    }

    @Override
    public void setOnEndListener(String tag, Action end) {
        mAnimators.get(tag).endAction = end;
    }

    @Override
    public void setStartAndEndListener(String tag, Action start, Action end) {
        ActionNode node = mAnimators.get(tag);
        node.startAciton = start;
        node.endAction = end;
    }

    @Override
    public void build() {
        Set<String> keys = mAnimators.keySet();
        for (String key : keys) {
            final ActionNode node = mAnimators.get(key);
            node.mAnim.addListener(new Animator.AnimatorListener() {
                private boolean hasCancled;

                @Override
                public void onAnimationStart(Animator animation) {
                    if (!node.isCancel && node.startAciton != null)
                        node.startAciton.doAction();
                    node.isCancel = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!hasCancled && node.startAciton != null)
                        node.endAction.doAction();
                    hasCancled = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    hasCancled = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    public void setDuration(String tag, long duration) {
        mAnimators.get(tag).mAnim.setDuration(duration);
    }

    @Override
    public void start(String tag) {
        mAnimators.get(tag).mAnim.start();
    }

    @Override
    public void cancel(String tag) {
        ActionNode node = mAnimators.get(tag);
        node.isCancel = true;
        node.mAnim.cancel();
    }

    @Override
    public void setStartDelay(String tag, long startDelay) {
        mAnimators.get(tag).mAnim.setStartDelay(startDelay);
    }
}

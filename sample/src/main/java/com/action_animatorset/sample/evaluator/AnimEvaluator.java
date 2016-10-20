package com.action_animatorset.sample.evaluator;

import android.animation.TypeEvaluator;

/**
 * Created by yang on 2016/9/20.
 * paulyung@outlook.com
 * 估值器：在某一瞬时时刻，一个输入对应的一个输出
 */

public class AnimEvaluator implements TypeEvaluator<AnimPoint> {
    /**
     * @param fraction 百分比
     */
    @Override
    public AnimPoint evaluate(float fraction, AnimPoint startValue, AnimPoint endValue) {
        float x = 0f, y = 0f;
                x = calculateCubic(fraction, startValue.mEndX, endValue.mCtlX1, endValue.mCtlX2, endValue.mEndX);
                y = calculateCubic(fraction, startValue.mEndY, endValue.mCtlY1, endValue.mCtlY2, endValue.mEndY);

        return AnimPoint.newInstance(x, y);
    }

    /**
     * 三阶贝塞尔曲线计算公式
     *
     * @param t 就是evaluate函数的fraction参数，百分比 0 < t < 1
     */
    private float calculateCubic(float t, float start, float ctl1, float ctl2, float end) {
        double out = start * Math.pow((1 - t), 3)
                + 3 * ctl1 * t * Math.pow((1 - t), 2)
                + 3 * ctl2 * Math.pow(t, 2) * (1 - t) + end * Math.pow(t, 3);
        return (float) out;
    }
}

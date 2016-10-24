package com.action_animatorset.sample;

import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.action_animatorset.sample.evaluator.AnimEvaluator;
import com.action_animatorset.sample.evaluator.AnimPoint;
import com.paul.actionanimset.ActionAnimatorSet;
import com.paul.actionanimset.TriggerPoint;

public class TriggerPoint2Activity extends AppCompatActivity {
    private static final int ANIM_TIME = 500;

    RelativeLayout rl;
    FloatingActionButton fab;
    ActionAnimatorSet animSet;
    PointF centerP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_point2);
        rl = (RelativeLayout) findViewById(R.id.content_trigger_point2);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        rl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerP = new PointF(rl.getWidth() / 2, rl.getHeight() / 2);//rl的中点
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAnim(v);
            }
        });
    }

    private void initAnim(View v) {
        animSet = new ActionAnimatorSet();
        AnimPoint startPoint = AnimPoint.newInstance(0, 0);
        AnimPoint endPoint = cubicTo(0, 0, 0.86f, 0, -centerP.x, -centerP.y);
        ObjectAnimator anim1 = ObjectAnimator.ofObject(this,
                "Anim", new AnimEvaluator(), startPoint, endPoint);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleX", 1, 20);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(v, "scaleY", 1, 20);

        anim1.setDuration(ANIM_TIME);
        anim2.setDuration(ANIM_TIME);
        anim3.setDuration(ANIM_TIME);

        animSet.playFirst(anim1);
        animSet.addAnimBetween(anim2, anim1, new TriggerPoint<AnimPoint>() {
            @Override
            public boolean whenToStart(AnimPoint pointV) {
                return pointV.mEndX < -centerP.x + 100;//自定义何时开始anim2动画，此方法返回true时，anim2开始执行
            }
        });
        animSet.addAnimWith(anim3, anim2);//anim3和anim2同时执行
        animSet.start();
    }

    /**
     * 此方法便于根据 http://cubic-bezier.com/这个网站 的控制点参数来设置坐标
     */
    private AnimPoint cubicTo(float ctlParamsX1,
                              float ctlParamsY1, float ctlParamsX2, float ctlParamsY2, float endX, float endY) {
        return AnimPoint.cubicTo(ctlParamsX1 * endX, ctlParamsY1 * endY, ctlParamsX2 * endX, ctlParamsY2 * endY, endX, endY);
    }

    void setAnim(AnimPoint animPoint) {
        fab.setTranslationX(animPoint.mEndX);
        fab.setTranslationY(animPoint.mEndY);
    }
}

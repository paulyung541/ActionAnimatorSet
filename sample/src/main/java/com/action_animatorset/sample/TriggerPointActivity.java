package com.action_animatorset.sample;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.paul.actionanimset.ActionAnimatorSet;
import com.paul.actionanimset.AnimatorManager;
import com.paul.actionanimset.DefaultAnimatorManager;
import com.paul.actionanimset.TriggerPoint;

public class TriggerPointActivity extends AppCompatActivity {
    View view1, view2, view3;
    ActionAnimatorSet animSet;
    AnimatorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_point);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        ObjectAnimator view1Anim = ObjectAnimator.ofFloat(view1, "y", 1200);
        ObjectAnimator view2Anim = ObjectAnimator.ofFloat(view2, "y", 1200);
        ObjectAnimator view3Anim = ObjectAnimator.ofFloat(view3, "y", 1200);

        manager = new DefaultAnimatorManager();
        animSet = manager.createAnimatorSet();
        animSet.addAnimBetween(view3Anim, view2Anim, new TriggerPoint(400));//view3在view2的y值为300的时候开始动画
        animSet.addAnimBetween(view2Anim, view1Anim, new TriggerPoint(300));//view2在view1的y值为300的时候开始动画
        animSet.playFirst(view1Anim);
        animSet.setDuration(2000);
        animSet.setStartDelay(1000);
        animSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animSet.cancel();
    }
}

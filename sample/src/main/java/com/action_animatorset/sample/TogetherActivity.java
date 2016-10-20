package com.action_animatorset.sample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.paul.actionanimset.ActionAnimatorSet;

public class TogetherActivity extends AppCompatActivity {
    View view1, view2, view3;
    ActionAnimatorSet animSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_together);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        ObjectAnimator view1Anim = ObjectAnimator.ofFloat(view1, "y", 1200);
        ObjectAnimator view2Anim = ObjectAnimator.ofFloat(view2, "y", 1200);
        ObjectAnimator view3Anim = ObjectAnimator.ofFloat(view3, "y", 1200);
        view1Anim.setDuration(2000);
        view2Anim.setDuration(2000);
        view3Anim.setDuration(2000);

        view1Anim.setStartDelay(1000);
        view2Anim.setStartDelay(1000);
        view3Anim.setStartDelay(1000);

        animSet = new ActionAnimatorSet();
        animSet.playTogether(view1Anim, view2Anim, view3Anim);
        animSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animSet.cancel();
    }
}

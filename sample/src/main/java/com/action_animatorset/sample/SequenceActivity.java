package com.action_animatorset.sample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.paul.actionanimset.Action;
import com.paul.actionanimset.ActionAnimatorSet;
import com.paul.actionanimset.AnimatorManager;
import com.paul.actionanimset.DefaultAnimatorManager;

public class SequenceActivity extends AppCompatActivity {
    View view1, view2, view3;
    AnimatorManager manager;
    ActionAnimatorSet animSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        ObjectAnimator view1Anim = ObjectAnimator.ofFloat(view1, "y", 1200);
        ObjectAnimator view2Anim = ObjectAnimator.ofFloat(view2, "y", 1200);
        ObjectAnimator view3Anim = ObjectAnimator.ofFloat(view3, "y", 1200);

        manager = new DefaultAnimatorManager();

        animSet = manager.createAnimatorSet();
        animSet.playSequence(view1Anim, view2Anim, view3Anim);
        animSet.setDuration(2000);
        animSet.setStartDelay(1000);

        //设置Action，可在动画执行前后做一些处理，这样就不用单独再给动画设置监听了
        animSet.addStartAction(view1Anim, new Action() {
            @Override
            public void doAction() {
                showToast("动画开始");
            }
        });
        animSet.addEndAction(view3Anim, new Action() {
            @Override
            public void doAction() {
                showToast("动画结束了");
            }
        });

        animSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animSet.cancel();
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

package com.action_animatorset.sample;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.paul.actionanimset.Action;
import com.paul.actionanimset.AnimatorManager;
import com.paul.actionanimset.DefaultAnimatorManager;

public class IndependenceActivity extends AppCompatActivity {
    View view1, view2, view3;
    AnimatorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independence);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        ObjectAnimator view1Anim = ObjectAnimator.ofFloat(view1, "y", 1200);
        ObjectAnimator view2Anim = ObjectAnimator.ofFloat(view2, "y", 1200);
        ObjectAnimator view3Anim = ObjectAnimator.ofFloat(view3, "y", 1200);

        manager = new DefaultAnimatorManager();

        manager.addAnimator(view1Anim, "view1");
        manager.addAnimator(view2Anim, "view2");
        manager.addAnimator(view3Anim, "view3");

        manager.setDuration("view1", 1000);
        manager.setDuration("view2", 2000);
        manager.setDuration("view3", 3000);

        manager.setStartAndEndListener("view1", new Action() {
            @Override
            public void doAction() {
                showToast("view1开始了");
            }
        }, new Action() {
            @Override
            public void doAction() {
                showToast("view1结束了");
            }
        });
        manager.setStartAndEndListener("view2", new Action() {
            @Override
            public void doAction() {
                showToast("view2开始了");
            }
        }, new Action() {
            @Override
            public void doAction() {
                showToast("view2结束了");
            }
        });
        manager.setStartAndEndListener("view3", new Action() {
            @Override
            public void doAction() {
                showToast("view3开始了");
            }
        }, new Action() {
            @Override
            public void doAction() {
                showToast("view3结束了");
            }
        });
        manager.build();
    }

    public void click1(View view) {
        manager.start("view1");
    }

    public void click2(View view) {
        manager.start("view2");
    }

    public void click3(View view) {
        manager.start("view3");
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

package com.action_animatorset.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //同时执行
    public void click1(View view) {
        startActivity(new Intent(this, TogetherActivity.class));
    }

    //顺序执行
    public void click2(View view) {
        startActivity(new Intent(this, SequenceActivity.class));
    }

    //在依赖的动画执行到某个float(int)值时执行
    public void click3(View view) {
        startActivity(new Intent(this, TriggerPointActivity.class));
    }

    //在依赖的动画执行到某个Object值时执行
    public void click4(View view) {
        startActivity(new Intent(this, TriggerPoint2Activity.class));
    }
}

package com.yj.carouselview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        findViewById(R.id.btnDefaultStyle).setOnClickListener(this);
        findViewById(R.id.btnSelfStyle).setOnClickListener(this);
        findViewById(R.id.btnCustomerStyle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch(v.getId()){
            case R.id.btnDefaultStyle:
                intent.setClass(this, DefaultStyleActivity.class);
                break;
            case R.id.btnSelfStyle:
                intent.setClass(this, SelfStyleActivity.class);
                break;
            case R.id.btnCustomerStyle:
                intent.setClass(this, CustomerStyleActivity.class);
                break;
        }
        startActivity(intent);
    }
}

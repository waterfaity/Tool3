package com.waterfairy.tool3.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.waterfairy.tool3.R;
import com.waterfairy.widget.SpreadView;

public class SpreadActivity extends AppCompatActivity implements View.OnClickListener, SpreadView.OnSpreadListener {
    private SpreadView spreadView;
    private boolean canJump;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprea);
        spreadView = (SpreadView) findViewById(R.id.spread_view);
        button = (Button) findViewById(R.id.jump);
        button.setOnClickListener(this);
        spreadView.setTimes(50);
        spreadView.start(false, 540, 1500);
        spreadView.setOnSpreadListener(this);
    }

    @Override
    public void onClick(View v) {
        canJump = true;
        spreadView.start(true, (button.getRight() + button.getLeft()) / 2, (button.getBottom() + button.getTop()) / 2);
    }

    @Override
    public void onSpreadOk() {
        if (canJump)
            startActivity(new Intent(this, SpreadActivity.class));
    }
}

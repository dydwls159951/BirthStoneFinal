package com.perples.birthstone.General;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.perples.birthstone.Pregnant.PregnantSelectSeat;
import com.perples.recosample.R;

public class SelectSeat extends AppCompatActivity {

    private ToggleButton mSeat;
    private Button mAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seat);

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.tabbar);

        mSeat = findViewById(R.id.seat);

        mSeat.setText(null);
        mSeat.setTextOn(null);
        mSeat.setTextOff(null);

        mAccept = findViewById(R.id.gen_accept_but);
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectSeat.this, WaitConfirm.class);
                startActivity(intent);
            }
        });

    }
}

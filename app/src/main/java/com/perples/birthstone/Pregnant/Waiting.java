package com.perples.birthstone.Pregnant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.perples.recosample.R;

import static java.lang.Thread.sleep;

public class Waiting extends AppCompatActivity {

    private Button mCancelBut;
    private Button fake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        mCancelBut = findViewById(R.id.btn_cancel);
        mCancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fake = findViewById(R.id.fake);
        fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Waiting.this, PregnantSelectSeat.class);
                startActivity(intent);
            }
        });

    }
}

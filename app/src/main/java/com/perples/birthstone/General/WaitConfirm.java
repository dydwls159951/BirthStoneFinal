package com.perples.birthstone.General;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.perples.birthstone.Pregnant.PregnantSelectSeat;
import com.perples.birthstone.Pregnant.Waiting;
import com.perples.recosample.R;

public class WaitConfirm extends AppCompatActivity {

    private Button fake2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_confirm);

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.tabbar);

        fake2 = findViewById(R.id.fake2);
        fake2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaitConfirm.this, Confirm.class);
                startActivity(intent);
            }
        });

    }
}

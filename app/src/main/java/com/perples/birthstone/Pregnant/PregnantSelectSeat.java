package com.perples.birthstone.Pregnant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.perples.birthstone.Cookie.CookieSystem;
import com.perples.recosample.R;

public class PregnantSelectSeat extends AppCompatActivity {

    private Button mAcceptBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnant_select_seat);

        mAcceptBut = findViewById(R.id.preg_accept_but);
        mAcceptBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}

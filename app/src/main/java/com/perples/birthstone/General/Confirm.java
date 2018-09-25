package com.perples.birthstone.General;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.perples.recosample.R;

public class Confirm extends AppCompatActivity {

    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.tabbar);

        confirm = findViewById(R.id.fake3);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Confirm.this, MainGeneralActivity.class);
                startActivity(intent);
            }
        });

    }
}

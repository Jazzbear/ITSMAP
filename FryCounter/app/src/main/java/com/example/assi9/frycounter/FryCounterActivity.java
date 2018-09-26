package com.example.assi9.frycounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FryCounterActivity extends AppCompatActivity {

    private final static String KEY_CLICK_COUNT = "temporary value";

    Button btnClick;
    TextView txtCounter;
    ImageView imgPickle;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fry_counter);

        imgPickle = findViewById(R.id.imgView);
        imgPickle.setVisibility(View.INVISIBLE);

        btnClick = (Button) findViewById(R.id.btnClicker);
        txtCounter = (TextView) findViewById(R.id.txtOutput);

        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt(KEY_CLICK_COUNT, 0);
        }

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;

                updateUI();
            }
        });

        updateUI();

    }

    private void updateUI() {
        if(counter > 0) {
            imgPickle.setVisibility(View.VISIBLE);
            txtCounter.setText(Integer.toString(counter));
        } else {
            txtCounter.setText("Press the button!");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CLICK_COUNT, counter);

        super.onSaveInstanceState(outState);
    }


}

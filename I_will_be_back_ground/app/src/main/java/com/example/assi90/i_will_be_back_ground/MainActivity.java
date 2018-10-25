package com.example.assi90.i_will_be_back_ground;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button startBtn, stopBtn;
    String helperText = "Press start/stop to initiate or close a service";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Shorthand way of binding ui elements/widgets instead of with the = operator.
        textView.findViewById(R.id.helperTextView);
        textView.setText(helperText);
        
        startBtn.findViewById(R.id.btnStart);
        stopBtn.findViewById(R.id.btnStop);
//        startBtn = findViewById(R.id.btnStart);
        
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "It Works", Toast.LENGTH_SHORT).show();
            }
        });
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This also works!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

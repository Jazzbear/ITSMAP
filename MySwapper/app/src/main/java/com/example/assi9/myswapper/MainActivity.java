package com.example.assi9.myswapper;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //value to set after swapping images.
    boolean swapped;
    //key to save our state in.
    public final static String KEY_SWAP_STATE = "Initialized";

    ImageView leftImg;
    ImageView rightImg;
    Button btnSwap;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate called");

        if(savedInstanceState != null) {
            swapped = savedInstanceState.getBoolean(KEY_SWAP_STATE, false);
            Log.d("MainActivity", "restored with swapped = " + swapped);
        }

        btnSwap = findViewById(R.id.swapBtn);
        btnExit = findViewById(R.id.btnExit);

        leftImg = findViewById(R.id.leftImg);
        rightImg = findViewById(R.id.rightImg);

        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swap();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void swap() {
        swapped = !swapped;
        String toastText = "Swap was pressed";
        updateUI();
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    public void updateUI() {
        if(!swapped) {
            leftImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ricknormal));
            rightImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rickpickle));
        } else {
            leftImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rickpickle));
            rightImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ricknormal));
        }
    }

    public void onSavedInstanceState(Bundle state) {
        state.putBoolean(KEY_SWAP_STATE, swapped);
        super.onSaveInstanceState(state);
    }
}

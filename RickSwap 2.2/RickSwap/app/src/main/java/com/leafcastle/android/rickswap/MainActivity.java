package com.leafcastle.android.rickswap;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //keep track of swapped state
    boolean swapped;

    //key for saving the swapped state between e.g. rotations
    static final String KEY_IS_SWAPPED = "key_is_swapped";

    //declare UI widgets
    Button btnSwap;
    Button btnExit;
    ImageView imgLeft;
    ImageView imgRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate called");

        //check if state was saved before onCreate was called
        //and set the state to the previous if found
        if(savedInstanceState != null){
            swapped = savedInstanceState.getBoolean(KEY_IS_SWAPPED, false); //default to false
            Log.d("MainActivity", "restored with swapped = " + swapped);
        }

        //get UI widgets (note that typecasting is no longer necesarry)
        btnSwap = (Button) findViewById(R.id.btnSwap);
        btnExit = findViewById(R.id.btnExit);


        imgLeft = findViewById(R.id.imgLeft);
        imgRight = findViewById(R.id.imgRight);

        //setup buttons with click listeners
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

        //update UI for first view
        updateUI();
    }

    //swaps and updates UI
    private void swap(){
        swapped = !swapped;
        updateUI();
        Toast.makeText(this, "Swapped was pressed!", Toast.LENGTH_SHORT).show();
    }

    //updates the UI (including updating ImageViews with Drawables based on swapped state)
    private void updateUI(){
        if(swapped){
            imgLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ricknormal));
            imgRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rickpickle));
        } else {
            imgLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rickpickle));
            imgRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ricknormal));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("MainActivity", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SWAPPED, swapped);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy!!!!");
        super.onDestroy();
    }
}

package com.example.assi9.myhelloworldapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText_field);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, "message");
//        startActivity(intent);
    }

    public void changeMessage(View view) {
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText("Hello Andorid!");
    }
}

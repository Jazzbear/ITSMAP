package com.example.assi9.thereandbackagain;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.assi9.thereandbackagain.AnswerActivity.EXTRA_ANSWER;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_ANSWER = 101;
    public static final String EXTRA_QUESTION = "com.thereAndBackAgain.question.input";

    EditText edtQuestion;
    String question;
    Button askButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //calle the super method on onCreate to restore the savedInstance values.
        super.onCreate(savedInstanceState);
        //Initializing the content view and setting it to be activity_main.xml for this context
        setContentView(R.layout.activity_main);

//        if(savedInstanceState != null) {
//            question = savedInstanceState.getString(KEY_QEUSTION, "");
//            edtQuestion.setText(question);
//        }
        //Mapping the question input field from view.
        edtQuestion = findViewById(R.id.edtQuestion);
        //mappingt the Send Question button from view
        askButton = findViewById(R.id.askButton);

        //setting up a listener for when the button is pressed
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askQuestion();
            }
        });
    }

    private void askQuestion() {
        //Get the question from the input field
        question = edtQuestion.getText().toString();
        //Make a new intent with the
        Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
        intent.putExtra(EXTRA_QUESTION, question);
        startActivityForResult(intent, REQ_ANSWER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Make sure we call the super class method so we can utilize the default event based function
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_ANSWER) {

            if (resultCode==RESULT_OK) {
                toast("Ok");
               if (data != null) {
                   String answer = data.getStringExtra(EXTRA_ANSWER);
                   toast(answer);
               }
            } else {
                toast("Canceled");
            }
        }
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }
}

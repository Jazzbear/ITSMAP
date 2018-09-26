package com.leafcastle.android.intentsclassexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.leafcastle.android.intentsclassexample.MainActivity.EXTRA_QUESTION;

public class AnswerActivity extends AppCompatActivity {

    public static final String EXTRA_ANSWER = "extra_answer";

    TextView txtQuestion;
    EditText edtAnswer;
    Button btnCancel;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Intent dataFromMainActivity = getIntent();
        String question = dataFromMainActivity.getStringExtra(EXTRA_QUESTION);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion.setText(question);

        edtAnswer = findViewById(R.id.edtAnswer);
        btnCancel = findViewById(R.id.btnCancel);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelPressed();
            }
        });
    }

    private void cancelPressed(){
        setResult(RESULT_CANCELED);
        finish();
    }

    private void okPressed(){

        String answer = edtAnswer.getText().toString();

        Intent data = new Intent(); //Intent used as data object
        data.putExtra(EXTRA_ANSWER, answer);

        setResult(RESULT_OK, data);
        finish();
    }
}

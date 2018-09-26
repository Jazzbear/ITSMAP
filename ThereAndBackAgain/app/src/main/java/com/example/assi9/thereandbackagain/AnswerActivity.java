package com.example.assi9.thereandbackagain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.assi9.thereandbackagain.MainActivity.EXTRA_QUESTION;

public class AnswerActivity extends AppCompatActivity {

    public final static String EXTRA_ANSWER = "com.TABA.answer.key";
    TextView txtQuestion;
    EditText answerField;
    Button btnCancel;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Intent dataFromMain = getIntent();
        String question = dataFromMain.getStringExtra(EXTRA_QUESTION);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion.setText(question);

        answerField = findViewById(R.id.ansField);
        btnCancel = findViewById(R.id.cancelBtn);
        btnOk = findViewById(R.id.okBtn);

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

    private void cancelPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void okPressed() {
        String answer = answerField.getText().toString();
        Intent answerData = new Intent().putExtra(EXTRA_ANSWER, answer);

        setResult(RESULT_OK, answerData);
        finish();
    }
}

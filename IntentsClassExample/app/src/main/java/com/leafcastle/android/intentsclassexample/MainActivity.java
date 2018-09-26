package com.leafcastle.android.intentsclassexample;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.leafcastle.android.intentsclassexample.AnswerActivity.EXTRA_ANSWER;

public class MainActivity extends AppCompatActivity {

    public static final int REQ_ANSWER = 101;
    public static final String EXTRA_QUESTION = "extra_question";

    EditText edtQuestion;
    Button btnAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtQuestion = findViewById(R.id.edtQuestion);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer();
            }
        });
    }

    private void answer(){

        String question = edtQuestion.getText().toString();
        //Toast.makeText(this, "Your questions is:\n" + question, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
        intent.putExtra(EXTRA_QUESTION, question);
        //no result:
        //startActivity(intent);

        //get result:
        startActivityForResult(intent, REQ_ANSWER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_ANSWER){

            if(resultCode==RESULT_OK){
                toast("Ok");
                if(data!=null){
                    String answer = data.getStringExtra(EXTRA_ANSWER);

                    toast(answer);

                }
            } else {
                toast("Canceled");
            }
        }

        //if many activities use switch - case

    }

    private void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

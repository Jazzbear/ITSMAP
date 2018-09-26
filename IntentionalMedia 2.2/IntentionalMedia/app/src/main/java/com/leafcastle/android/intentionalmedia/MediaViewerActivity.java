package com.leafcastle.android.intentionalmedia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import static com.leafcastle.android.intentionalmedia.MediaBrowseActivity.EXTRA_SELECTED_VIDEO_ID;

public class MediaViewerActivity extends AppCompatActivity {

    public static final String EXTRA_RANDOM_NUMBER = "extra_random_number";

    private String videoId = "undefined";
    private Button btnOk;
    private Button btnCancel;
    private TextView txtVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        //retrieve data from the Intent that was used to start this activity
        Intent dataFromCaller = getIntent();
        if(dataFromCaller!=null) {
            videoId = dataFromCaller.getStringExtra(EXTRA_SELECTED_VIDEO_ID);
        }

        //get UI references
        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtVideo = (TextView) findViewById(R.id.txtShowVideo);

        //update TextView
        txtVideo.setText("Showing video: " + videoId);

        //set OnClickListerners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                Random r = new Random();
                int random = r.nextInt(10) + 1;
                data.putExtra(EXTRA_RANDOM_NUMBER, random);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}

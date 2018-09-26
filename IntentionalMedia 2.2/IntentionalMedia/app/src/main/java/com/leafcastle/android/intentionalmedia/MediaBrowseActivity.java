package com.leafcastle.android.intentionalmedia;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static com.leafcastle.android.intentionalmedia.MediaViewerActivity.EXTRA_RANDOM_NUMBER;

public class MediaBrowseActivity extends AppCompatActivity {

    //key for saving instance state
    public final static String KEY_SELECTED_POSITION = "key_selected_position";
    //request codes for activities
    public final static int REQUEST_CODE_APP_VIEW = 100;
    public final static int REQUEST_CODE_YOUTUBE_APP_VIEW = 101;
    public final static int REQUEST_CODE_BROWSER_VIEW = 102;

    public final static String EXTRA_SELECTED_VIDEO_ID = "extra_selected_video_id";

    private Button btnViewApp;
    private Button btnViewYoutubeApp;
    private Button btnViewBrowser;
    private Spinner spnVideos;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> videoList;

    private int selectedPosition = 0;
    private String selectedVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if state was saved and restore
        if(savedInstanceState!=null){
            selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION, 0); //0 is default
        }

        //get references to widgets
        btnViewApp = (Button) findViewById(R.id.btnViewApp);
        btnViewYoutubeApp = (Button) findViewById(R.id.btnViewYoutube);
        btnViewBrowser = (Button) findViewById(R.id.btnViewWeb);
        spnVideos = (Spinner) findViewById(R.id.spnVideoLinks);

        //set up spinner
        videoList = new ArrayList<String>(Arrays.asList("tZp8sY06Qoc", "LEUGPEVRDmU", "cEeWLDMDqpk"));
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, videoList);
        spnVideos.setAdapter(adapter);
        spnVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedPosition = position;
                selectedVideoId = videoList.get(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nothing happened, no change
            }

        });
        spnVideos.setSelection(selectedPosition);

        //set OnClickListerners for Buttons
        btnViewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewInApp(selectedVideoId);
            }
        });
        btnViewYoutubeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewWithYoutube(selectedVideoId);
            }
        });
        btnViewBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewWithBrowser(selectedVideoId);

            }
        });
    }

    private void viewInApp(String id){
        //explicit intent, because we name the specific class
        Intent activityIntent = new Intent(this, MediaViewerActivity.class);
        activityIntent.putExtra(EXTRA_SELECTED_VIDEO_ID, id); //add the video id string as an extra
        startActivityForResult(activityIntent, REQUEST_CODE_APP_VIEW);

    }

    private void viewWithYoutube(String id){
            //implicit intent
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));

            try {
                startActivityForResult(appIntent, REQUEST_CODE_YOUTUBE_APP_VIEW);
            } catch (ActivityNotFoundException ex) {
                viewWithBrowser(id);
            }
    }

    private void viewWithBrowser(String id) {
        //implicit intent
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        startActivityForResult(browserIntent, REQUEST_CODE_BROWSER_VIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_APP_VIEW:
                if(resultCode == RESULT_OK){
                    int random = -1;
                    if(data!=null){
                        random = data.getIntExtra(EXTRA_RANDOM_NUMBER, -1);
                    }
                    toastThis("Got back from MediaViewerActivity, result was OK :" + random);
                } else {
                    toastThis("Got back from MediaViewerActivity, result was CANCEL");
                }
                break;
            case REQUEST_CODE_YOUTUBE_APP_VIEW:
                toastThis("Got back from the Youtube App, result code was: " + resultCode);
                break;
            case REQUEST_CODE_BROWSER_VIEW:
                toastThis("Got back from the browser, result code was: " + resultCode);
                break;
        }
    }

    private void toastThis(String toast){
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition);
    }
}
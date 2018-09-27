package com.example.jazzbear.assignmentone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.jazzbear.assignmentone.DataContainer.*;

public class OverviewActivity extends AppCompatActivity {

//    TextView overviewHeader;
    TextView overviewStockName;
    TextView stockPurchasePrice;
    Button detailsButton;
//    ArrayList<String> uiState =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        overviewStockName = findViewById(R.id.overviewName);
        stockPurchasePrice = findViewById(R.id.overviewPurchased);
        detailsButton = findViewById(R.id.overviewButton);

//        if (savedInstanceState != null) {
//
//        }

        updateUI();

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
                startActivityForResult(detailsIntent, DETAILS_REQUEST);
            }
        });
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==DETAILS_REQUEST) {
            if (resultCode==RESULT_OK) {
                updateUI();
            } else {
                toast("No changes made");
            }
        }
     }

    private void updateUI() {
        overviewStockName.setText(EXTRA_STOCKNAME);
        overviewStockName.setText(EXTRA_STOCKPRICE);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putStringArrayList();
////        super.onSaveInstanceState()
//    }

}

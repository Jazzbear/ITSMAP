package com.example.jazzbear.assignmentone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.jazzbear.assignmentone.DataContainer.*;

public class DetailsActivity extends AppCompatActivity {

    static final String SAVED_DETAILSVIEW = "overview_is_set";
    TextView detailName;
    TextView detailPrice;
    TextView detailAmount;
    TextView detailSector;
    Button backButton;
    Button editButton;
    Stock detailsStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intentFromOverview = getIntent();
        detailsStock = intentFromOverview.getParcelableExtra(EXTRA_STOCKOBJECT);

        detailName = findViewById(R.id.nameDetails);
        detailPrice = findViewById(R.id.priceDetails);
        detailAmount = findViewById(R.id.stocksDetails);
        detailSector = findViewById(R.id.sectorDetails);
        backButton = findViewById(R.id.backBtn);
        editButton = findViewById(R.id.editBtn);
//        toast(detailsStock.getStockName());
//        detailName.setText("Facebook");

//        if (savedInstanceState != null) {
////            detailsStock = savedInstanceState.getParcelable(SAVED_DETAILSVIEW);
//////            assert detailsStock != null;
////            updateUI(detailsStock);
////        } else {
////            updateUI(detailsStock);
////        }


        
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                backButtonPressed();
//            }
//        });
        
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editButtonPressed();
//            }
//        });
//

    }

    private void updateUI(Stock input) {
//        String stockName = input.getStockName();
        int stockPrice = input.getStockPrice();
        int stockAmount = input.getStockAmount();
        String stockSector = input.getStockSector();
//        detailName.setText(stockName);
        detailPrice.setText(stockPrice);
        detailAmount.setText(stockAmount);
        detailSector.setText(stockSector);
    }

    private void editButtonPressed() {

    }

    private void backButtonPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_DETAILSVIEW, detailsStock);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

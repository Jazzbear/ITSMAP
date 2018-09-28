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
    Stock newDetailsStock; /*= new Stock();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intentFromOverview = getIntent();
        detailsStock = intentFromOverview.getParcelableExtra(STOCKOBJECT_EXTRA);

        detailName = findViewById(R.id.nameDetails);
        detailPrice = findViewById(R.id.priceDetails);
        detailAmount = findViewById(R.id.stocksDetails);
        detailSector = findViewById(R.id.sectorDetails);
        backButton = findViewById(R.id.backBtn);
        editButton = findViewById(R.id.editBtn);

        if (savedInstanceState != null) {
            detailsStock = savedInstanceState.getParcelable(SAVED_DETAILSVIEW);
            assert detailsStock != null;
            updateUI(detailsStock);
        } else {
            updateUI(detailsStock);
        }


        
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressed();
            }
        });
        
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonPressed();
            }
        });

    }

    private void updateUI(Stock input) {
//        detailName.setText(input.getStockName());
//        detailPrice.setText(Double.toString(input.getStockPrice()));
//        detailAmount.setText(Integer.toString(input.getStockAmount()));
//        detailSector.setText(input.getStockSector());

        detailName.setText("Hurr");
        detailPrice.setText("9000");
        detailAmount.setText("55");
        detailSector.setText("Bag");

        String newName = detailName.getText().toString();
        Double newPrice = Double.parseDouble(detailPrice.getText().toString());
        int newAmount = Integer.parseInt(detailAmount.getText().toString());
        String newSector = detailSector.getText().toString();

        newDetailsStock = new Stock();
        newDetailsStock.setStockName(newName);
        newDetailsStock.setStockPrice(newPrice);
        newDetailsStock.setStockAmount(newAmount);
        newDetailsStock.setStockSector(newSector);

        toast("Updated UI");
    }

    private void editButtonPressed() {

    }

    private void backButtonPressed() {
        Intent detailsResult = new Intent().putExtra(STOCKOBJECT_EXTRA, newDetailsStock);
        setResult(RESULT_OK, detailsResult);
//        setResult(RESULT_OK);
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

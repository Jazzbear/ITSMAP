package com.example.jazzbear.assignmentone;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.jazzbear.assignmentone.DataCodes.*;

public class DetailsActivity extends AppCompatActivity {

//    static final String DETAILSVIEW_SAVED = "detailsview_is_set";
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

        if (savedInstanceState != null) {
            detailsStock = savedInstanceState.getParcelable(STOCK_STATE);
//            toast("Refreshed UI"); // For testing
        } else {
            detailsStock = getIntent().getParcelableExtra(STOCKOBJECT_EXTRA);
        }

        detailName = findViewById(R.id.nameDetails);
        detailPrice = findViewById(R.id.priceDetails);
        detailAmount = findViewById(R.id.stocksDetails);
        detailSector = findViewById(R.id.sectorDetails);
        backButton = findViewById(R.id.backBtn);
        editButton = findViewById(R.id.editBtn);

        updateDetailsUI(); // Update the ui with the new information

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

    private void updateDetailsUI() {
        detailName.setText(detailsStock.getStockName());
        detailPrice.setText(Double.toString(detailsStock.getStockPrice()));
        detailAmount.setText(Integer.toString(detailsStock.getStockAmount()));
        detailSector.setText(detailsStock.getStockSector());
    }

    //Send an intent result back to the the overview and destroy details view.
    private void backButtonPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void editButtonPressed() {
        Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
        editIntent.putExtra(STOCKOBJECT_EXTRA, detailsStock);
        startActivityForResult(editIntent, EDIT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Return straight to overviewActivity
                assert data != null;
                setResult(RESULT_OK, data);
                finish();
            } else {
                toast(getResources().getString(R.string.toastCanceled));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STOCK_STATE, detailsStock);
        super.onSaveInstanceState(outState);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

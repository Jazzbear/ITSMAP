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

    static final String DETAILSVIEW_SAVED = "detailsview_is_set";
    TextView detailName;
    TextView detailPrice;
    TextView detailAmount;
    TextView detailSector;
    Button backButton;
    Button editButton;
    Stock detailsStock = new Stock();
//    Stock newDetailsStock; /*= new Stock();*/

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
            detailsStock = savedInstanceState.getParcelable(DETAILSVIEW_SAVED);
            assert detailsStock != null;
            updateDetailsUI(detailsStock);
            toast("Refreshed UI");
        } else {
            updateDetailsUI(detailsStock);
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
                /* TODO: bør ændres til endten tom eller stock, i det at vi burde bare
                * TODO: sende det samme object med. men det kan altid ændres, i alle tilfælde
                * TODO: så bør stock nok initialiseres i toppen så den ikke kan være null*/
                editButtonPressed(detailsStock);
            }
        });
    }

    private void updateDetailsUI(Stock input) {
        detailName.setText(input.getStockName());
        detailPrice.setText(Double.toString(input.getStockPrice()));
        detailAmount.setText(Integer.toString(input.getStockAmount()));
        detailSector.setText(input.getStockSector());

//        detailName.setText("Hurr");
//        detailPrice.setText("9000");
//        detailAmount.setText("55");
//        detailSector.setText("Bag");
//
        setChanges();

        toast("Updated UI");
    }

    // TODO: bør nok ændres til at være tom
    private void editButtonPressed(Stock stock) {
        Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
        editIntent.putExtra(STOCKOBJECT_EXTRA, stock);
        startActivityForResult(editIntent, EDIT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                detailsStock = data.getParcelableExtra(STOCKOBJECT_EXTRA);
                updateDetailsUI(detailsStock);
                //TODO: Needs to send result back to overview - FIXED!
                toast("SAVED");
            } else {
                toast("Canceled");
            }
        }
    }

    private void setChanges() {
        // TODO: Kan muligvis fjenes. Siden detailstock også burde blive sat med response fra editActivity.
        String newName = detailName.getText().toString();
        Double newPrice = Double.parseDouble(detailPrice.getText().toString());
        int newAmount = Integer.parseInt(detailAmount.getText().toString());
        String newSector = detailSector.getText().toString();

        // TODO: Kan muligvis ændres til bare at bruge detailsStock - FIXED
//        detailsStock = new Stock();
        detailsStock.setStockName(newName);
        detailsStock.setStockPrice(newPrice);
        detailsStock.setStockAmount(newAmount);
        detailsStock.setStockSector(newSector);
    }

    private void backButtonPressed() {
        Intent detailsResult = new Intent().putExtra(STOCKOBJECT_EXTRA, detailsStock);
        setResult(RESULT_OK, detailsResult);
//        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAILSVIEW_SAVED, detailsStock);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

package com.example.jazzbear.assignmentone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.jazzbear.assignmentone.DataContainer.*;

public class OverviewActivity extends AppCompatActivity {

    static final String OVERVIEW_SAVED = "overview_is_set";
    TextView overviewStockName;
    TextView stockPurchasePrice;
    ImageView imgView;
    Button detailsButton;
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Init view elements
        overviewStockName = findViewById(R.id.overviewName);
        stockPurchasePrice = findViewById(R.id.overviewPurchased);
        detailsButton = findViewById(R.id.overviewButton);
        imgView = findViewById(R.id.imageView);

        // Recreating after and instance saved state.
        // Otherwise initializes first stock with default values
        if (savedInstanceState != null) {
            stock = savedInstanceState.getParcelable(OVERVIEW_SAVED);
            updateUI(stock);
//            toast("Refreshed UI");
        } else {
            // Getting the resources so we can set the language to the right locale
            // DA=Teknologi and EN=Technology
            Resources res = getResources();
            String sector = res.getString(R.string.sectorTech);
            stock = new Stock("Facebook",
                    1000.00,
                    14,
                    sector);
            updateUI(stock);
        }

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonClicked(stock);
            }
        });
    }

    private void detailsButtonClicked(Stock stock) {
        //Sent an intent to details and parse the stock object.
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(STOCKOBJECT_EXTRA, stock);
        startActivityForResult(detailsIntent, DETAILS_REQUEST);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Update the stock object and update the ui
                stock = data.getParcelableExtra(STOCKOBJECT_EXTRA);
                updateUI(stock);
                toast(getResources().getString(R.string.toastOk));
            } else {
                toast(getResources().getString(R.string.toastNoChanges));
            }
        }
     }

    private void updateUI(Stock input) {
        overviewStockName.setText(input.getStockName());
        String purchaseString = getResources().getString(R.string.stockPurchaseText) + input.getStockPrice();
        stockPurchasePrice.setText(purchaseString);
        setImageView(input);
    }

    private void setImageView(Stock input) {
        // Getting resources so i can check on the string values for sector, for the right locale.
        String sectorValue = input.getStockSector();
        Resources res = getResources();
        String techSector = res.getString(R.string.sectorTech);
        String materialSector = res.getString(R.string.sectorMats);
        String healthSector = res.getString(R.string.sectorHealth);

        //commented out the setImageDrawable since its better to use icons as they scale in pixel density for each device.
        // but left them there to show the alternative.
        //Used this to find out how to set icons instead: https://stackoverflow.com/questions/30800708/how-to-load-images-from-mipmap-folder-programatically
        if (sectorValue != null) {
            if (sectorValue.equalsIgnoreCase(techSector)) {
                imgView.setImageResource(R.mipmap.ic_technology_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.technology));
            } else if (sectorValue.equalsIgnoreCase(materialSector)) {
                imgView.setImageResource(R.mipmap.ic_materials_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.materials));
            } else if (sectorValue.equalsIgnoreCase(healthSector)) {
                imgView.setImageResource(R.mipmap.ic_healthcare_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.healthcare));
            }
        }
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(OVERVIEW_SAVED, stock);
    }

}

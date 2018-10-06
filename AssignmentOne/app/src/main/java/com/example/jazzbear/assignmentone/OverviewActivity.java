package com.example.jazzbear.assignmentone;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.jazzbear.assignmentone.DataCodes.*;

public class OverviewActivity extends AppCompatActivity {

//    static final String OVERVIEW_SAVED = "overview_is_set";
    TextView overviewStockName;
    TextView stockPurchasePrice;
    ImageView imgView;
    Button detailsButton;
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Recreating after and instance saved state.
        // Otherwise initializes first stock with default values
        if (savedInstanceState != null) {
            stock = savedInstanceState.getParcelable(STOCK_STATE);
//            toast("Refreshed UI"); // For debugs
        } else {
            // Getting the resources so we can set the language to the right locale
            // DA=Teknologi and EN=Technology
            String sector = getResources().getString(R.string.sectorTech);
            stock = new Stock("Facebook",
                    1000.00,
                    14,
                    sector);
        }

        // Init view elements
        overviewStockName = findViewById(R.id.overviewName);
        stockPurchasePrice = findViewById(R.id.overviewPurchased);
        detailsButton = findViewById(R.id.overviewButton);
        imgView = findViewById(R.id.imageView);

        updateUI();

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonClicked();
            }
        });
    }

    private void detailsButtonClicked() {
        //Sent an intent to details and parse the stock object.
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(STOCKOBJECT_EXTRA, stock);
        startActivityForResult(detailsIntent, DETAILS_REQUEST);
    }

    private void updateUI() {
        overviewStockName.setText(stock.getStockName());
        String purchaseString = getResources().getString(R.string.stockPurchaseText) + " " + Double.toString(stock.getStockPrice());
        stockPurchasePrice.setText(purchaseString);
        setImageView();
    }

    private void setImageView() {
        // Getting resources so i can check on the string values for sector, for the right locale.
        String sectorValue = stock.getStockSector();
        String techSector = getResources().getString(R.string.sectorTech);
        String materialSector = getResources().getString(R.string.sectorMats);
        String healthSector = getResources().getString(R.string.sectorHealth);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Update the stock object and update the ui
                assert data != null;
                stock = data.getParcelableExtra(STOCKOBJECT_EXTRA);
                updateUI();
//                toast("OK");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STOCK_STATE, stock);
    }

    // Used toasts for debugging
    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

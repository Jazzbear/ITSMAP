package com.example.jazzbear.assignmentone;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.jazzbear.assignmentone.DataContainer.*;

public class OverviewActivity extends AppCompatActivity {

    static final String OVERVIEW_SAVED = "overview_is_set";
    TextView overviewStockName;
    TextView stockPurchasePrice;
    Button detailsButton;
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        overviewStockName = findViewById(R.id.overviewName);
        stockPurchasePrice = findViewById(R.id.overviewPurchased);
        detailsButton = findViewById(R.id.overviewButton);



        if (savedInstanceState != null) {
            stock = savedInstanceState.getParcelable(OVERVIEW_SAVED);
            updateUI(stock);
            toast("Refreshed UI");
        } else {
            Resources res = getResources();
//            String[] sectors = res.getStringArray(R.array.sectors_array);
            String sector = res.getString(R.string.sectorTech);
//            String sector = sectors[0];
            stock = new Stock("Facebook",
                    1000.00,
                    14,
                    sector);
            updateUI(stock);
        }

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Denne bør mulgivis også ændres ligesom i details activity
                detailsButtonClicked(stock);
            }
        });
    }

    private void detailsButtonClicked(Stock stock) {
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(STOCKOBJECT_EXTRA, stock);
        startActivityForResult(detailsIntent, DETAILS_REQUEST);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                // TODO: bør testes om vores andet stock object kan bruges.
                Stock detailsResponse = data.getParcelableExtra(STOCKOBJECT_EXTRA);
                updateUI(detailsResponse);
                toast("OK");
//                stock = data.getParcelableExtra()
//                updateUI();
            } else {
                toast("No changes made");
            }
        }
     }

    private void updateUI(Stock input) {
        overviewStockName.setText(input.getStockName());
        String purchaseString = "Purchased at: " + input.getStockPrice();
        stockPurchasePrice.setText(purchaseString);
//        toast("UI Updated");
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

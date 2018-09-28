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

    static final String SAVED_OVERVIEW = "overview_is_set";
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
            stock = savedInstanceState.getParcelable(SAVED_OVERVIEW);
            toast("Refreshed UI");
            updateUI(stock);
        } else {
            stock = new Stock("Facebook",
                    1000.00,
                    14,
                    "Technology");
            updateUI(stock);
        }

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonClicked();
            }
        });
    }

    private void detailsButtonClicked() {
        Stock stockToSend = stock;

        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(STOCKOBJECT_EXTRA, stockToSend);
        startActivityForResult(detailsIntent, DETAILS_REQUEST);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==DETAILS_REQUEST) {
            if (resultCode==RESULT_OK) {
                Stock responseData = data.getParcelableExtra(STOCKOBJECT_EXTRA);
                updateUI(responseData);
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
        outState.putParcelable(SAVED_OVERVIEW, stock);
    }

}

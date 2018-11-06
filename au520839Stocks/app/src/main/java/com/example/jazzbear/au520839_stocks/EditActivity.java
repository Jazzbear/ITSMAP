package com.example.jazzbear.au520839_stocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;

public class EditActivity extends AppCompatActivity {
    TextView txtStockCompanyName, txtStockSector;
    EditText editStockValue, editStockAmount;
    Button saveButton, cancelButton;
    StockQuote editStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Recovering the instance state.
        if (savedInstanceState != null) {
            editStock = savedInstanceState.getParcelable(Globals.STOCK_STATE);
        } else {
            //Take out from the intent, the stock object via. the parcelable object class.
            editStock = getIntent().getParcelableExtra(Globals.STOCK_OBJECT_EXTRA);
        }

        //Init view elements
        txtStockCompanyName = findViewById(R.id.txtNameField);
        editStockValue = findViewById(R.id.stockPriceField);
        editStockAmount = findViewById(R.id.stockAmountField);
        txtStockSector = findViewById(R.id.txtSectorField);
        updateEditUI();

        saveButton = findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            //Click event listener for save button
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancelButton = findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //Click event listener for cancelButton
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });
    }

    private void updateEditUI() {
        //update the ui elements with the new info.
        txtStockCompanyName.setText(editStock.getCompanyName());
        editStockValue.setText(Double.toString(editStock.getStockPurchasePrice()));
        editStockAmount.setText(Integer.toString(editStock.getAmountOfStocks()));
        txtStockSector.setText(editStock.getSector());
    }


    private void cancelChanges() {
        // No changes, so return to the DetailsActivity view
        setResult(RESULT_CANCELED);
        finish();
    }

    private void saveChanges() {
        if (checkFieldsAreValid()) {
            getChanges();
            Intent editResult = new Intent().putExtra(Globals.STOCK_OBJECT_EXTRA, editStock);
            setResult(RESULT_OK, editResult);
            finish();
        } else {
            toast(getResources().getString(R.string.toastFailedSaving));
        }
    }

    private void getChanges() {
        editStock.setStockPurchasePrice(Double.parseDouble(editStockValue.getText().toString()));
        editStock.setAmountOfStocks(Integer.parseInt(editStockAmount.getText().toString()));
    }

    private boolean checkFieldsAreValid() {
        String priceUserInput = editStockValue.getText().toString();
        String amountUserInput = editStockAmount.getText().toString();

        //Used this stackoverflow example: https://stackoverflow.com/questions/6290531/check-if-edittext-is-empty
        //And this https://stackoverflow.com/questions/6538709/edittext-seterror-with-no-message-just-the-icon
        if (priceUserInput.matches("")) {
            editStockValue.requestFocus();
            editStockValue.setError(getResources().getString(R.string.errorInputPrice));
            return false;
        }
        if (amountUserInput.matches("")) {
            editStockAmount.requestFocus();
            editStockAmount.setError(getResources().getString(R.string.errorInputAmount));
            return false;
        }
        return true;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*Its fine here that we just saved the stock even with empty fields,
         * because it is never parsed back to details on cancel, and saveChanges will
         * never allow empty fields.*/
//        getChanges(); // Get changes before saving so that the editStock object is up to date.
        outState.putParcelable(Globals.STOCK_STATE, editStock);
        super.onSaveInstanceState(outState);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }
}

package com.example.jazzbear.assignment2_stockmonitor;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.jazzbear.assignment2_stockmonitor.Models.Stock;
import com.example.jazzbear.assignment2_stockmonitor.Utils.Globals;

public class EditActivity extends AppCompatActivity {

    //    static final String EDITVIEW_SAVED = "editview_is_set";
    EditText editNameField;
    EditText editPriceField;
    EditText editAmountField;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Button saveButton;
    Button cancelButton;
    Stock editStock;
    String sectorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Recovering the instance state.
        if (savedInstanceState != null) {
            editStock = savedInstanceState.getParcelable(Globals.STOCK_STATE);
        } else {
            //Take out from the intent, the stock object via. the parcelable object class.
            editStock = getIntent().getParcelableExtra(Globals.STOCKOBJECT_EXTRA);
        }

        //Init view elements
        editNameField = findViewById(R.id.stockNameField);
        editPriceField = findViewById(R.id.priceField);
        editAmountField = findViewById(R.id.stockAmountField);
        radioButton1 = findViewById(R.id.editRadio1);
        radioButton2 = findViewById(R.id.editRadio2);
        radioButton3 = findViewById(R.id.editRadio3);
        saveButton = findViewById(R.id.saveBtn);
        cancelButton = findViewById(R.id.cancelBtn);
        sectorValue = editStock.getStockSector();

        updateEditUI();

        //Click event listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        //Click event listener for cancelButton
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });
    }

    private void updateEditUI() {
        //update the ui elements with the new info.
        editNameField.setText(editStock.getStockName());
        editPriceField.setText(Double.toString(editStock.getStockPrice()));
        editAmountField.setText(Integer.toString(editStock.getStockAmount()));
        setRadioButtons();
    }

    private void setRadioButtons() {
        //Get the correct string resources so they fit with the locale.
        String techSector = getResources().getString(R.string.sectorTech);
        String materialSector = getResources().getString(R.string.sectorMats);
        String healthSector = getResources().getString(R.string.sectorHealth);

        //used this as inspiration: https://stackoverflow.com/questions/12832456/radio-buttons-set-checked-state-through-code
        if(sectorValue != null) {
            if (sectorValue.equalsIgnoreCase(techSector)) {
                radioButton1.setChecked(true);
            } else if (sectorValue.equalsIgnoreCase(materialSector)) {
                radioButton2.setChecked(true);
            } else if (sectorValue.equalsIgnoreCase(healthSector)) {
                radioButton3.setChecked(true);
            }
        }
    }

    private void cancelChanges() {
        // No changes, so return to the DetailsActivity view
        setResult(RESULT_CANCELED);
        finish();
    }

    private void saveChanges() {
        if (checkFieldsAreValid()) {
            getChanges();
            Intent editResult = new Intent().putExtra(Globals.STOCKOBJECT_EXTRA, editStock);
            setResult(RESULT_OK, editResult);
            finish();
        } else {
            toast(getResources().getString(R.string.toastFailedSaving));
        }
    }

    private boolean checkFieldsAreValid() {
        String nameUserInput = editNameField.getText().toString();
        String priceUserInput = editPriceField.getText().toString();
        String amountUserInput = editAmountField.getText().toString();

        //Used this stackoverflow example: https://stackoverflow.com/questions/6290531/check-if-edittext-is-empty
        //And this https://stackoverflow.com/questions/6538709/edittext-seterror-with-no-message-just-the-icon
        if (nameUserInput.matches("")) {
            editNameField.requestFocus();
            editNameField.setError(getResources().getString(R.string.errorInputName));
            return false;
        }
        if (nameUserInput.length() < 4) {
            editNameField.requestFocus();
            editNameField.setError(getResources().getString(R.string.errorInputLengh));
            return false;
        }
        if (priceUserInput.matches("")) {
            editPriceField.requestFocus();
            editPriceField.setError(getResources().getString(R.string.errorInputPrice));
            return false;
        }
        if (amountUserInput.matches("")) {
            editAmountField.requestFocus();
            editAmountField.setError(getResources().getString(R.string.errorInputAmount));
            return false;
        }
        return true;
    }

    private void getChanges() {
        editStock.setStockName(editNameField.getText().toString());
        editStock.setStockPrice(Double.parseDouble(editPriceField.getText().toString()));
        editStock.setStockAmount(Integer.parseInt(editAmountField.getText().toString()));
        editStock.setStockSector(sectorValue);
    }

    public void onRadioButtonClicked(View view) {
        //Get the correct string resources so they fit with the locale.
        Resources res = getResources();
        String techSector = res.getString(R.string.sectorTech);
        String materialSector = res.getString(R.string.sectorMats);
        String healthSector = res.getString(R.string.sectorHealth);

        //Used the example from https://developer.android.com/guide/topics/ui/controls/radiobutton#HandlingEvents
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.editRadio1:
                if (checked) {
                    sectorValue = techSector;
                }
                break;
            case R.id.editRadio2:
                if (checked) {
                    sectorValue = materialSector;
                }
                break;
            case R.id.editRadio3:
                if (checked) {
                    sectorValue = healthSector;
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*Its fine here that we just saved the stock even with empty fields,
         * because it is never parsed back to details on cancel, and saveChanges will
         * never allow empty fields.*/
        outState.putParcelable(Globals.STOCK_STATE, editStock);
        getChanges(); // Get changes before saving so that the editStock object is up to date.
        super.onSaveInstanceState(outState);
//        toast("State saved"); // For testing

    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

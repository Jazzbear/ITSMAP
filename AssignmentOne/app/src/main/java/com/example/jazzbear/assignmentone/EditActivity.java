package com.example.jazzbear.assignmentone;


import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static com.example.jazzbear.assignmentone.DataContainer.*;

public class EditActivity extends AppCompatActivity {

    static final String EDITVIEW_SAVED = "editview_is_set";
    EditText editNameField;
    EditText editPriceField;
    EditText editAmountField;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Button saveButton;
    Button cancelButton;
    Stock editStock = new Stock();
    String sectorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Get intent from the details activity
        Intent intentFromDetails = getIntent();
        //Take out the stock object via. the parceable object class
        editStock = intentFromDetails.getParcelableExtra(STOCKOBJECT_EXTRA);

        //Init view elements
        editNameField = findViewById(R.id.stockNameField);
        editPriceField = findViewById(R.id.priceField);
        editAmountField = findViewById(R.id.stockAmountField);
        sectorValue = editStock.getStockSector();
        radioButton1 = findViewById(R.id.editRadio1);
        radioButton2 = findViewById(R.id.editRadio2);
        radioButton3 = findViewById(R.id.editRadio3);
        saveButton = findViewById(R.id.saveBtn);
        cancelButton = findViewById(R.id.cancelBtn);

        //Recovering the instance state.
        if (savedInstanceState != null) {
            editStock = savedInstanceState.getParcelable(EDITVIEW_SAVED);
            assert editStock != null;
            updateEditUI(editStock);
        } else {
            updateEditUI(editStock);
        }
        //Click event listerner for savebutton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        //Click event listerner for cancelButton
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });
    }

    private void cancelChanges() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void saveChanges() {
        if (checkFieldsAreValid() == true) {
            getChanges();
            Intent editResult = new Intent().putExtra(STOCKOBJECT_EXTRA, editStock);
            setResult(RESULT_OK, editResult);
            finish();
        } else {
            toast("Could not save!");
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
            editNameField.setError("You need to input a name");
            return false;
        } else if (nameUserInput.length() < 4) {
            editNameField.requestFocus();
            editNameField.setError("Name needs to be at least 4 characters");
            return false;
        }
        if (priceUserInput.matches("")) {
            editPriceField.requestFocus();
            editPriceField.setError("You need to input a price");
            return false;
        }
        if (amountUserInput.matches("")) {
            editAmountField.requestFocus();
            editAmountField.setError("You need to input an amount of stocks");
            return false;
        }
        return true;
    }

    private void updateEditUI(Stock input) {
        //update the ui elements with the new info.
        editNameField.setText(input.getStockName());
        editPriceField.setText(Double.toString(input.getStockPrice()));
        editAmountField.setText(Integer.toString(input.getStockAmount()));
        setRadioButtons();
    }

    private void getChanges() {
        editStock.setStockName(editNameField.getText().toString());
        editStock.setStockPrice(Double.parseDouble(editPriceField.getText().toString()));
        editStock.setStockAmount(Integer.parseInt(editAmountField.getText().toString()));
        editStock.setStockSector(sectorValue);
    }

    private void setRadioButtons() {
        //Get the correct string resources so they fit with the locale.
        Resources res = getResources();
        String techSector = res.getString(R.string.sectorTech);
        String materialSector = res.getString(R.string.sectorMats);
        String healthSector = res.getString(R.string.sectorHealth);

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
        super.onSaveInstanceState(outState);
        getChanges(); // Get changes before saving so that the editStock object is up to date.
        toast("State saved");
        /*Its fine here that we just saved the stock even with empty fields,
        * because it is never parsed back to details on cancel, and saveChanges will
        * never allow empty fields.*/
        outState.putParcelable(EDITVIEW_SAVED, editStock);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}

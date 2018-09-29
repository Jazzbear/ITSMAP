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

import static com.example.jazzbear.assignmentone.DataContainer.*;

public class EditActivity extends AppCompatActivity {

    static final String EDITVIEW_SAVED = "editview_is_set";
    EditText editNameField;
    EditText editPriceField;
    EditText editAmountField;
//    RadioGroup editSectorRadioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Button saveButton;
    Button cancelButton;
    Stock editStock = new Stock();
    String sectorValue;
//    Resources res = getResources();
//    String techSector = res.getString(R.string.sectorTech);
//    String materialSector = res.getString(R.string.sectorMats);
//    String healthSector = res.getString(R.string.sectorHealth);
//    Stock newEditStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Get intent from the details activity
        Intent intentFromDetails = getIntent();
        editStock = intentFromDetails.getParcelableExtra(STOCKOBJECT_EXTRA);

        editNameField = findViewById(R.id.stockNameField);
        editPriceField = findViewById(R.id.priceField);
        editAmountField = findViewById(R.id.stockAmountField);
        sectorValue = editStock.getStockSector();

        radioButton1 = findViewById(R.id.editRadio1);
        radioButton2 = findViewById(R.id.editRadio2);
        radioButton3 = findViewById(R.id.editRadio3);
        saveButton = findViewById(R.id.saveBtn);
        cancelButton = findViewById(R.id.cancelBtn);

        // TODO: Change this so we have another check if a field is empty
        // TODO: Then add 3 more keys and then i can save the instance state with user changes
        if (savedInstanceState != null) {
            editStock = savedInstanceState.getParcelable(EDITVIEW_SAVED);
            assert editStock != null;
            updateEditUI(editStock);
        } else {
            updateEditUI(editStock);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

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
        Stock changedEditStock = getChanges();

        //TODO: needs field validation
//        if (checkFieldsAreValid() == true) {
//            changedEditStock = getChanges();
//
//        } else {
//            fieldValidation();
//        }

        Intent editResult = new Intent().putExtra(STOCKOBJECT_EXTRA, changedEditStock);
        setResult(RESULT_OK, editResult);
        finish();
    }

    private void fieldValidation() {
    }

    private boolean checkFieldsAreValid() {
        // TODO: implement
        return true;
    }

    private void updateEditUI(Stock input) {
        editNameField.setText(input.getStockName());
        editPriceField.setText(Double.toString(input.getStockPrice()));
        editAmountField.setText(Integer.toString(input.getStockAmount()));
        setRadioButtons(input);
    }

    private Stock getChanges() {
        // TODO: Kan muligvis fjenes. Siden detailstock også burde blive sat med response fra editActivity.
        String newName = editNameField.getText().toString();
        Double newPrice = Double.parseDouble(editPriceField.getText().toString());
        int newAmount = Integer.parseInt(editAmountField.getText().toString());

        //TODO: Needs method implemented to check for sector seclected in view
        String newSector = sectorValue;

        // TODO: Kan muligvis ændres til bare at bruge detailsStock
//        editStock = new Stock();
        editStock.setStockName(newName);
        editStock.setStockPrice(newPrice);
        editStock.setStockAmount(newAmount);
        editStock.setStockSector(newSector);
        return editStock;
    }

    private void setRadioButtons(Stock input) {
        Resources res = getResources();
        String techSector = res.getString(R.string.sectorTech);
        String materialSector = res.getString(R.string.sectorMats);
        String healthSector = res.getString(R.string.sectorHealth);


        // TODO: Skal muligvis have ændres string values sådan at vi sættes
        if(sectorValue != null) {
            if (sectorValue.equalsIgnoreCase(techSector)) {
                radioButton1.setChecked(true);
            } else if (sectorValue.equalsIgnoreCase(materialSector)) {
                radioButton2.setChecked(true);
            } else if (sectorValue.equalsIgnoreCase(healthSector)) {
                radioButton3.setChecked(true);
            }
        }
//        switch (sectorValue) {
//            case "Technology":
//                radioButton1.setChecked(true);
//                break;
//            case "Materials":
//
//        }
    }

    public void onRadioButtonClicked(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch (view.getId()) {
//            case R.id.editRadio1:
//                if (checked) {
//                    sectorValue = techSector;
//                }
//                break;
//            case R.id.editRadio2:
//                if (checked) {
//                    sectorValue = materialSector;
//                }
//                break;
//            case R.id.editRadio3:
//                if (checked) {
//                    sectorValue = healthSector;
//                }
//                break;
//        }
    }

    //TODO: Add on onSaveInstanceState here and add a check on wether a field has been changed
    //TODO: Then if its changed we output the fields to the bundle. Otherwise we output the stock.
}

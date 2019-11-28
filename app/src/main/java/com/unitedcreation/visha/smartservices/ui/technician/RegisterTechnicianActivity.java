package com.unitedcreation.visha.smartservices.ui.technician;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.model.Technician;
import com.unitedcreation.visha.smartservices.utils.Utilities;

import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TYPE;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USERS;

public class RegisterTechnicianActivity extends AppCompatActivity {

    private EditText enterNameEditText;
    private String phoneNumber;
    private String type;
    private Spinner dropDown;
    private Button continueButton;
    private String category;
    private String[] items;
    private ArrayAdapter<String> arrayAdapter;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public void intentToTechnicianUi(){

        Intent intent = new Intent(this, TechnicianMapsActivity.class);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);
        finish();

    }

    public void onContinueClicked(View view){

        if(enterNameEditText.getText().toString().trim().length()!=0){

            if(category.trim().length() != 0) {

                Technician technician = new Technician(enterNameEditText.getText().toString(), phoneNumber, type, category);
                mDatabaseReference.child(USERS).child(userId).setValue(technician);
                intentToTechnicianUi();
            } else {

                Toast.makeText(this, getString(R.string.regtech_select_category_warning), Toast.LENGTH_SHORT).show();

            }

        } else {

            Toast.makeText(this, getString(R.string.regtech_invalid_name_warning), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_technician);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFireBaseAuth = FirebaseAuth.getInstance();
        userId = mFireBaseAuth.getCurrentUser().getUid();

        items = getResources().getStringArray(R.array.services_name);

        dropDown = findViewById(R.id.spinner_registertechnician_choosecategory);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);
        dropDown.setAdapter(arrayAdapter);

        phoneNumber = mFireBaseAuth.getCurrentUser().getPhoneNumber();

        enterNameEditText = findViewById(R.id.edittext_registertechnician_name);
        continueButton = findViewById(R.id.button_registertechnician_continue);
        continueButton.animate().translationYBy(-Utilities.pixels(this)).setDuration(800);

        Intent intent = getIntent();
        type = intent.getStringExtra(TYPE);

        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                category = items[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

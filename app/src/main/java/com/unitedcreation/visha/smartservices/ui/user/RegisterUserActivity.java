package com.unitedcreation.visha.smartservices.ui.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.model.User;
import com.unitedcreation.visha.smartservices.utils.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TYPE;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USERS;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText enterNameEditText;
    private String phoneNumber;
    private String type;
    private Button continueButton;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    public void intentToUserUi(){

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();

    }

    public void onContinueClicked(View view){

        if(enterNameEditText.getText().toString().trim().length()!=0) {

            User user = new User(enterNameEditText.getText().toString(), phoneNumber, type);
            mDatabaseReference.child(USERS).child(userId).setValue(user);
            intentToUserUi();

        }
        else{

            Toast.makeText(this, getString(R.string.regtech_invalid_name_warning), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFireBaseAuth = FirebaseAuth.getInstance();
        userId = mFireBaseAuth.getCurrentUser().getUid();
        phoneNumber = mFireBaseAuth.getCurrentUser().getPhoneNumber();

        Intent intent = getIntent();
        type=intent.getStringExtra(TYPE);

        enterNameEditText = findViewById(R.id.edittext_registeruser_name);
        continueButton = findViewById(R.id.button_registeruser_continue);
        continueButton.animate().translationYBy(-(Utilities.pixels(this))).setDuration(800);

    }
}

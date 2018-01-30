package com.example.visha.smarttechnician;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterName extends AppCompatActivity {

    private EditText enterNameEditText;
    private String phoneNumber;
    private String type;
    private Button continueButton;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    public void continue2UserUI(){

        Intent intent=new Intent(this,CategoryList.class);
        startActivity(intent);
        finish();

    }

    public float pixels(){

        float px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
        return px;

    }

    public void onContinueClicked(View view){

        if(enterNameEditText.getText().toString().trim().length()!=0){

            User user=new User(enterNameEditText.getText().toString(),phoneNumber,type);
            mDatabaseReference.child("Users").child(userId).setValue(user);
            continue2UserUI();

        }
        else{

            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mFireBaseAuth=FirebaseAuth.getInstance();
        userId=mFireBaseAuth.getCurrentUser().getUid();
        phoneNumber=mFireBaseAuth.getCurrentUser().getPhoneNumber();

        Intent intent=getIntent();
        type=intent.getStringExtra("type");

        enterNameEditText= findViewById(R.id.enterNameEditText);
        continueButton= findViewById(R.id.UcontinueButton);
        continueButton.animate().translationYBy(-pixels()).setDuration(800);

    }

}

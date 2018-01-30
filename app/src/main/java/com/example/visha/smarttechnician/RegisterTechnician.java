package com.example.visha.smarttechnician;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterTechnician extends AppCompatActivity {

    private EditText enterNameEditText;
    private String phoneNumber;
    private String type;
    private Spinner dropDown;
    private Button continueButton;
    private String category;
    private final String[] items = new String[]{"Electronics","Plumber","Carpenter","Painter","Portable Devices"};
    private ArrayAdapter arrayAdapter;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public void continue2TechnicianUI(){

        Intent intent=new Intent(this,MapsActivity.class);
        intent.putExtra("category",category);
        startActivity(intent);
        finish();

    }

    public float pixels(){

        float px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
        return px;

    }

    public void onContinueClicked(View view){

        if(enterNameEditText.getText().toString().trim().length()!=0){

            if(category.trim().length()!=0) {

                Technician technician = new Technician(enterNameEditText.getText().toString(), phoneNumber, type, category);
                mDatabaseReference.child("Users").child(userId).setValue(technician);
                continue2TechnicianUI();
            }
            else{

                Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();

            }

        }
        else{

            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_technician);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mFireBaseAuth=FirebaseAuth.getInstance();
        userId=mFireBaseAuth.getCurrentUser().getUid();

        dropDown= findViewById(R.id.spinner);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,items);
        dropDown.setAdapter(arrayAdapter);

        phoneNumber=mFireBaseAuth.getCurrentUser().getPhoneNumber();

        enterNameEditText= findViewById(R.id.technicianEnterNameEditText);
        continueButton= findViewById(R.id.TcontinueButton);
        continueButton.animate().translationYBy(-pixels()).setDuration(800);

        Intent intent=getIntent();
        type=intent.getStringExtra("type");

        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                category=items[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

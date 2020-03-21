package com.unitedcreation.visha.smartservices.ui.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.unitedcreation.visha.smartservices.R;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView shareTextView;
    View paytmButton, bhimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bhimButton = findViewById(R.id.view_payment_bhim);
        bhimButton.setOnClickListener(this);
        paytmButton = findViewById(R.id.view_payment_paytm);
        paytmButton.setOnClickListener(this);
        shareTextView = findViewById(R.id.tv_payment_share);
        shareTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Intent intent=new Intent(PaymentActivity.this, ShareActivity.class);

        if (v.getId() == R.id.tv_payment_share) {
            intent.putExtra("HIDE",true);
        }

        startActivity(intent);
    }
}

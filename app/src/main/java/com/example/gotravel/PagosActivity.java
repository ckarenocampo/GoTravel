package com.example.gotravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PagosActivity extends AppCompatActivity {
    TextView txtid, txtamount,txtstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);
        txtid = findViewById(R.id.txtId);
        txtamount = findViewById(R.id.txtAmount);
        txtstatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),intent.getStringExtra("PaymentAmount"));

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void showDetails(JSONObject response, String paymentAmount) {
        txtid.setText("id");
        txtstatus.setText("state");
        txtamount.setText("$"+paymentAmount);
    }
}

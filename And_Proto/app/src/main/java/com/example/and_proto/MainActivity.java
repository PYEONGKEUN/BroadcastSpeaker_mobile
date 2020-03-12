package com.example.and_proto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    Button Button_transmit, Button_request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button_transmit = (Button)findViewById(R.id.button_transmit);
        Button_request = (Button)findViewById(R.id.button_request);



    }

    public void ButtonTransmitOnClick(View v){
        Intent intent = new Intent(MainActivity.this, TransmitActivity.class);
        startActivity(intent);
    }

    public void ButtonRequestOnClick(View v){
        Intent intent = new Intent(MainActivity.this, ReqeustActivity.class);
        startActivity(intent);
    }





}

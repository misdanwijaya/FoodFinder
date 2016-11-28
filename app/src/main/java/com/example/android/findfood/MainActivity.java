package com.example.android.findfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    static final int ACT2_REQUEST = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void klikButton(View v) {
        //mengambil masukan
        //untuk username
        EditText masukan1 = (EditText) findViewById(R.id.username);
        String inputNama = masukan1.getText().toString();

        if(TextUtils.isEmpty(inputNama)) {
            masukan1.setError("Field Username!");
            return;
        }

        Intent intent2 = new Intent(this, Main2Activity.class);
        startActivityForResult(intent2,ACT2_REQUEST);
        masukan1.setText("");
    }

}

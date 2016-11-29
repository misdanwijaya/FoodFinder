package com.example.android.findfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final int ACT2_REQUEST = 99;
    public static final String EXTRA_MESSAGE1 = "keluar" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //cara 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // cek request code
        if (requestCode == ACT2_REQUEST) {


            //tampilkan toast
            Toast toast = Toast.makeText(getApplicationContext(),"You Have Been Log Out",Toast.LENGTH_LONG);
            toast.show();


        }
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
        intent2.putExtra(MainActivity.EXTRA_MESSAGE1, "You Have Been Log in.");
        startActivityForResult(intent2,ACT2_REQUEST);
        masukan1.setText("");
    }

}

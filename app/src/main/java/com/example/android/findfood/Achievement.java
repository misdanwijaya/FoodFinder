package com.example.android.findfood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Achievement extends AppCompatActivity {

    public static final String EXTRA_MESSAGE2 = "badges";
    public static final String EXTRA_ID = "id" ;

    int hitung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        //untuk toolbar
        Toolbar myToolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar2);
        getSupportActionBar().setTitle("Achievement");

        //notif
        //ambil intent
        Intent intent = getIntent();
        //ambil datanya
        new Achievement.AmbilData().execute("http://makersinstitute.id:5000/users/" + intent.getStringExtra(Main2Activity.EXTRA_ID));;
        String pesan = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE2);
        //tampilkan toast
        Toast t = Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_LONG);
        t.show();

        hitungBadges();
    }


    private class AmbilData extends AsyncTask<String, Integer, String> {
        protected String username;
        protected int score;
        protected String deskripsi;

        protected String doInBackground(String... strUrl) {
            Log.v("yw", "mulai ambil data");
            String hasil="";
            String kind = "";

            //ambil data dari internet
            InputStream inStream = null;
            int len = 500; //buffer
            try {
                URL url = new URL(strUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);


                conn.setRequestMethod("GET");
                conn.connect();
                int response = conn.getResponseCode();
                inStream = conn.getInputStream();  //ambil stream data


                //konversi stream ke string
                /*Reader r = null;
                r = new InputStreamReader(inStream, "UTF-8");
                char[] buffer = new char[len];
                r.read(buffer);
                hasil  =  new String(buffer);*/
                Reader r = null;
                r = new InputStreamReader(inStream, "UTF-8");
                BufferedReader bfr  = new BufferedReader(r);
                String s;
                StringBuilder sb = new StringBuilder();
                s = bfr.readLine();
                while (s != null) {
                    sb.append(s);
                    s = bfr.readLine(); //baca per baris
                }

                hasil  =  sb.toString().trim();



                JSONObject jsonObj = new JSONObject(hasil);
                JSONObject jo  =  jsonObj.getJSONObject("message");
                username = jo.getString("username");
                score = jo.getInt("points");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return username;
        }


        protected void onPostExecute(String result) {
            hitung = score;
        }
    }

    //tollbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mBack:
                klikKembali();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void klikKembali() {
        Intent intent2 = getIntent();
        intent2.putExtra(Main2Activity.EXTRA_MESSAGE2,"");
        setResult(RESULT_OK, intent2);
        finish();


    }


    public void hitungBadges(){
        if (hitung == 2){
            ImageView img = (ImageView) findViewById(R.id.badge1);
            img.setImageResource(R.drawable.eat);
        }
        else if (hitung == 5){
            ImageView img = (ImageView) findViewById(R.id.badge2);
            img.setImageResource(R.drawable.cake);
        }

        else if (hitung == 10){
            ImageView img = (ImageView) findViewById(R.id.badge2);
            img.setImageResource(R.drawable.egg);
        }

        else if (hitung == 15){
            ImageView img = (ImageView) findViewById(R.id.badge2);
            img.setImageResource(R.drawable.chicken);
        }

        else if (hitung == 20){
            ImageView img = (ImageView) findViewById(R.id.badge2);
            img.setImageResource(R.drawable.burger);
        }

        else if (hitung == 25){
            ImageView img = (ImageView) findViewById(R.id.badge2);
            img.setImageResource(R.drawable.pizza);
        }

    }

    public void klikBadges1(View v){
        new AlertDialog.Builder(this)
                .setTitle("First Eat")
                .setMessage(Html.fromHtml("This is Your First Achievemennt! Visit More Places To Get Another Badges."+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }

    public void klikBadges2(View v){
        new AlertDialog.Builder(this)
                .setTitle("Sweet Cupcake")
                .setMessage(Html.fromHtml("You Must Get 5 Points to Unlock This Achievement!"+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }

    public void klikBadges3(View v){
        new AlertDialog.Builder(this)
                .setTitle("Lovely Egg")
                .setMessage(Html.fromHtml("You Must Get 10 Points to Unlock This Achievement!"+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }

    public void klikBadges4(View v){
        new AlertDialog.Builder(this)
                .setTitle("Perfecto Chicken")
                .setMessage(Html.fromHtml("You Must Get 15 Points to Unlock This Achievement!"+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }

    public void klikBadges5(View v){
        new AlertDialog.Builder(this)
                .setTitle("Super Burger")
                .setMessage(Html.fromHtml("You Must Get 20 Points to Unlock This Achievement!"+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }

    public void klikBadges6(View v){
        new AlertDialog.Builder(this)
                .setTitle("Yummy Pizza")
                .setMessage(Html.fromHtml("You Must Get 25 Points to Unlock This Achievement!"+"\n"+"<b>"+"Your Current Point : "+hitung+"</b>"))
                .setNegativeButton("Ok", null)
                .show();
    }


}




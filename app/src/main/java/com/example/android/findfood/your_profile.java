package com.example.android.findfood;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class your_profile extends AppCompatActivity {

    public static final String EXTRA_MESSAGE3 = "profile" ;
    public static final String EXTRA_ID = "id" ;
    public TextView tUsername;
    public TextView tScore;
    public TextView tLocation;

    //foto
    CircleImageView imgProfile;
    static final int REQUEST_CAMERA = 0;
    static final int SELECT_FILE = 1;
    private String userChoosenTask;


    String[] value = new String[]{
            "Choose from Gallery",
            "Take Photo",
            "Remove Profile Photo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);

        //untuk toolbar
        Toolbar myToolbar2 = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar2);
        getSupportActionBar().setTitle("Profile");



        tUsername = (TextView) findViewById(R.id.uName);
        tScore = (TextView) findViewById(R.id.uScore);
        tLocation = (TextView) findViewById(R.id.uLocation);

        //foto
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        imgProfile.setImageResource(R.drawable.user);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPhoto();
            }
        });

        //notif
        //ambil intent
        Intent intent = getIntent();
        //ambil datanya
        String pesan = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE3);
        new AmbilData().execute("http://makersinstitute.id:5000/users/" + intent.getStringExtra(Main2Activity.EXTRA_ID));;
        //tampilkan toast
        Toast t = Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_LONG);
        t.show();
    }

    //foto
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    public void dialogPhoto(){
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(your_profile.this);

        alertdialogbuilder.setTitle("Profile Picture");

        alertdialogbuilder.setItems(value, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(your_profile.this);

                switch (value[item]) {
                    case "Choose from Gallery":
                        Toast.makeText(your_profile.this, "Select Photo", Toast.LENGTH_SHORT).show();
                        userChoosenTask = "Choose from Library";
                        if (result)
                            galleryIntent();
                        break;
                    case "Take Photo":
                        Toast.makeText(your_profile.this, "Take Photo", Toast.LENGTH_SHORT).show();
                        userChoosenTask = "Take Photo";
                        if (result)
                            cameraIntent();
                        break;
                    case "Remove Profile Photo":
                        Toast.makeText(your_profile.this, "Photo Removed", Toast.LENGTH_SHORT).show();
                        imgProfile.setImageResource(R.drawable.user);
                        break;
                }

            }
        });

        AlertDialog dialog = alertdialogbuilder.create();

        dialog.show();
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgProfile.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgProfile.setImageBitmap(thumbnail);
    }

    //tollbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nBack:
                klikKembali();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void klikKembali() {
        Intent intent2 = getIntent();
        intent2.putExtra(Main2Activity.EXTRA_MESSAGE3,"");
        setResult(RESULT_OK, intent2);
        finish();

    }

    private class AmbilData extends AsyncTask<String, Integer, String> {
        protected String username;
        protected String score;
        protected String latitude;
        protected String longitude;

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
                score = jo.getString("points");
                latitude = jo.getString("latitude");
                longitude = jo.getString("longtitude");


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
            tUsername.setText(username);
            tScore.setText(score);
            tLocation.setText("Latitude : " + latitude + " Longitude : " + longitude);
        }
    }
}

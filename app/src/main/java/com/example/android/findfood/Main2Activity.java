package com.example.android.findfood;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.LocationListener;



public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,SensorEventListener {

    //Variabel Global
    private GoogleMap mMap;

    private static final int MY_PERMISSIONS_REQUEST = 99;//int bebas, maks 1 byte

    private Marker mPosSekarang;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private SensorManager sm;
    private Sensor senAccel;

    //untuk rumah makan
    private GoogleMap wdMap;
    private GoogleMap PadangOmuda;
    private GoogleMap kantinT;
    private GoogleMap ssg;
    private GoogleMap martabak;
    private GoogleMap mKontrakan;

    //contoh
    private GoogleMap rumah;
    private LatLng poisisirumah;

    private LatLng Wadoel;
    private LatLng kantintujuh;
    private LatLng PadangO;
    private LatLng posisiSSG;
    private LatLng posisiMartabak;
    private LatLng Kontrakan;
    private LatLng posSekarang;
    private LatLng gedungIlkom;

    //nilai
    int nilaiGIKL = 0;
    int nilaiGIKU = 0;

    int nilaiPadangL = 0;
    int nilaiPadangU = 0;

    int nilaiKantinL = 0;
    int nilaiKantinU = 0;

    int nilaiSSGL = 0;
    int nilaiSSGU = 0;

    int nilaiMartabakL = 0;
    int nilaiMartabakU = 0;

    int nilaiWadulL = 0;
    int nilaiWadulU = 0;

    double ax = 0, ay = 0, az = 0;

    int tanda =0;
    int tanda2 =0;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void ambilLokasi() {
       /* mulai Android 6 (API 23), pemberian persmission
        dilakukan secara dinamik (tdk diawal)
        untuk jenis2 persmisson tertentu, termasuk lokasi
        */


        // cek apakah sudah diijinkan oleh user, jika belum tampilkan dialog
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }
        //set agar setiap update lokasi maka UI bisa diupdate
        //setiap ada update maka onLocationChanged akan dipanggil
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    //muncul dialog & user memberikan reson (allow/deny), method ini akan dipanggil
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ambilLokasi();
            } else {
                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //10 detik sekli minta lokasi (10000ms = 10 detik)
        mLocationRequest.setInterval(10000);
        //tapi tidak boleh cepat dari 5 detik
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        //untuk toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //untuk maps
        //cek apakah sensor tersedia
        sm = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        senAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        if (senAccel != null) {
            // ada sensor accelerometer!
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Sukses, device punya sensor accelerometer!");
            ad.show();
        } else {
            // gagal, tidak ada sensor accelrometer.
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Tidak ada sensor accelerometer!");
            ad.show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        createLocationRequest();

    }

    //tollbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mAchievement:
                Toast.makeText(getApplicationContext(), "Achievement", Toast.LENGTH_LONG).show();
                return true;
            case R.id.mLogout:
                Toast.makeText(getApplicationContext(), "Log Out", Toast.LENGTH_LONG).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void klikAchievement() {
        //Intent intent2 = new Intent(this, Main2Activity.class);
        //startActivity(intent2);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap = googleMap;

        //map satelite mode
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //untuk tempat makan
        wdMap = googleMap;
        PadangOmuda = googleMap;
        mKontrakan = googleMap;
        kantinT = googleMap;
        ssg = googleMap;
        martabak = googleMap;

        //contoh
        rumah = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //batas lokasi UPI
        //urutan harus kiri bawah, kanan atas kotak
        LatLngBounds UPI = new LatLngBounds(
                new LatLng(-6.863273, 107.587212), new LatLng(-6.858025, 107.597839));


        //marker gedung ilkom
        gedungIlkom = new LatLng(-6.860418, 107.589889);
        mMap.addMarker(new MarkerOptions().position(gedungIlkom).title("Marker di GIK").snippet("Like " + nilaiGIKL + "; Dislike " + nilaiGIKU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


        // Set kamera sesuai batas UPI
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(UPI, 0));

        //contoh
        //marker kontrakan
        Kontrakan = new LatLng(-6.8663673, 107.5918008);
        mKontrakan.addMarker(new MarkerOptions().position(Kontrakan).title("Kontrakan").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        poisisirumah = new LatLng(-6.9535871, 107.66612);
        rumah.addMarker(new MarkerOptions().position(poisisirumah).title("Rumah").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //marker lokasi tempat makan
        //marker warung jadoel
        Wadoel = new LatLng(-6.8649716, 107.5936292);
        wdMap.addMarker(new MarkerOptions().position(Wadoel).title("Warung Jadoel Cafe").snippet("Like " + nilaiWadulL + "; Dislike " + nilaiWadulU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //marker Padang Omuda
        PadangO = new LatLng(-6.8658617, 107.5916296);
        PadangOmuda.addMarker(new MarkerOptions().position(PadangO).title("Rumah Makan Padang Omuda").snippet("Like " + nilaiPadangL + "; Dislike " + nilaiPadangU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //marker kantin 77
        kantintujuh = new LatLng(-6.863624, 107.589367);
        kantinT.addMarker(new MarkerOptions().position(kantintujuh).title("Kantin 77").snippet("Like " + nilaiKantinL + "; Dislike " + nilaiKantinU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //marker SSG
        posisiSSG = new LatLng(-6.8637613, 107.5898821);
        ssg.addMarker(new MarkerOptions().position(posisiSSG).title("SSGC").snippet("Like " + nilaiSSGL + "; Dislike " + nilaiSSGU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //marker martabak
        posisiMartabak = new LatLng(-6.864408, 107.5921445);
        martabak.addMarker(new MarkerOptions().position(posisiMartabak).title("Martabak lezat Group Bandung").snippet("Like " + nilaiMartabakL + "; Dislike " + nilaiMartabakU).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


        // Set kamera sesuai batas UPI
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset dari edges

        //posisi sekarang
        posSekarang = new LatLng(-6.8663673, 107.5918008);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.walk);
        mPosSekarang = mMap.addMarker(new MarkerOptions().position(posSekarang).title("I'm Here").flat(true).icon(icon));


        //set kamera sesuai batas di Ilkom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posSekarang, 17));


    }

    @Override
    public void onLocationChanged(Location location) {
        //Mengerakan Kamera sesuai lokasi sekarang
        mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 17));

        //untuk notifikasi getar
        double toKontrakan = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(Kontrakan),getLong(Kontrakan));
        double toPadangO = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(PadangO),getLong(PadangO));
        double toKantin = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(kantintujuh),getLong(kantintujuh));
        double toSSG = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(posisiSSG),getLong(posisiSSG));
        double toMartabak = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(posisiMartabak),getLong(posisiMartabak));
        double toWadoel = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(Wadoel),getLong(Wadoel));



        //contoh
        double toGIK = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(gedungIlkom),getLong(gedungIlkom));
        double toRumah = distFrom((float)location.getLatitude(),(float)location.getLongitude(),getLat(poisisirumah),getLong(poisisirumah));

        System.out.println("Jarak : " + toKontrakan);
        if(toKontrakan >= 0 && toKontrakan <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);

            System.out.println("Jarak : " + toKontrakan);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi kontrakan");
            ad.show();
        }

        if(toPadangO >= 0 && toPadangO <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toPadangO);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi Rumah Makan Padang Omuda");
            ad.show();
        }

        else if (toPadangO >= 0 && toPadangO <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiPadangL = nilaiPadangL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiPadangU = nilaiPadangU + 1;
            }
        }

        if(toKantin >= 0 && toKantin <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toKantin);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi Kantin 77");
            ad.show();
        }

        else if (toKantin >= 0 && toKantin <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiKantinL = nilaiKantinL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiKantinU = nilaiKantinU + 1;
            }
        }

        if(toSSG >= 0 && toSSG <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toSSG);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi SSGC");
            ad.show();
        }

        else if (toSSG >= 0 && toSSG <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiSSGL = nilaiSSGL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiSSGU = nilaiSSGU + 1;
            }
        }



        if(toMartabak >= 0 && toMartabak <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toMartabak);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi Martabak");
            ad.show();
        }

        else if (toMartabak >= 0 && toMartabak <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiMartabakL = nilaiMartabakL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiMartabakU = nilaiMartabakU + 1;
            }
        }

        if(toWadoel >= 0 && toWadoel <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toWadoel);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi Warung Jadoel");
            ad.show();
        }

        else if (toWadoel >= 0 && toWadoel <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiWadulL = nilaiWadulL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiWadulU = nilaiWadulU + 1;
            }
        }

        //contoh
        if(toGIK >= 5 && toGIK <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toGIK);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi GIK");
            ad.show();
        }

        else if (toGIK >= 0 && toGIK <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiGIKL = nilaiGIKL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiGIKU = nilaiGIKU + 1;
            }
        }

        if(toRumah >= 0 && toRumah <=12){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            System.out.println("Jarak : " + toRumah);
            mPosSekarang.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Anda dekat lokasi");
            ad.show();
        }

        else if (toRumah >= 0 && toRumah <= 5){

            if (ay >= 10 && tanda == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Menyukai");
                ad.show();
                tanda = 1;
                nilaiGIKL = nilaiGIKL + 1;
            }

            else if (ax >= 5 && tanda2 == 0){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Anda Tidak Menyukai");
                ad.show();
                tanda2=1;

                nilaiGIKU = nilaiGIKU + 1;
            }
        }
    }

    //fungsi untuk menghitung jarak
    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public float getLat(LatLng latln) {
        String s = latln.toString();
        String[] latLng = s.substring(10, s.length() - 1).split(",");
        String sLat = latLng[0];

        return Float.parseFloat(sLat);
    }

    public float getLong(LatLng latln) {
        String s = latln.toString();
        String[] latLng = s.substring(10, s.length() - 1).split(",");
        String sLong = latLng[1];

        return Float.parseFloat(sLong);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ambilLokasi();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, senAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }


    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}

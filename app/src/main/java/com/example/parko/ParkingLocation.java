package com.example.parko;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParkingLocation extends FragmentActivity implements OnMapReadyCallback {


   private static final String TAG = "MapActivity";
    private GoogleMap mMap;
     double latitude;
     double longtitude;
     LatLng latlong;
     Location currloc;
     String rruga;


@Override
    public void onMapReady(GoogleMap googleMap) {


        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
           // final String Latlong ;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d(TAG, "onMapClick: Pika e klikuar eshte"+latLng);
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    latitude = latLng.latitude;
                    longtitude = latLng.longitude;
                    latlong = latLng;
                    // Setting the position for the marker

                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                    // Clears the previously touched position
                    mMap.clear();

                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);
                    Location currentLocation = new Location(LocationManager.GPS_PROVIDER) ;
                    currentLocation.setLatitude(latitude);
                    currentLocation.setLongitude(longtitude);
                    List<Address> addresses;
                    Geocoder geocoder = new Geocoder(ParkingLocation.this);
                    try {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        rruga = addresses.get(0).getAddressLine(0);
                        //  Log.d(TAG, "onComplete: Adresa eshte vendbanimi:"+adresa);
                        Log.d(TAG, "onComplete: Rruga e vetme : "+rruga);

                    }catch (IOException e){
                        Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
                    }

                }
            });

            register_location_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: latitude"+latitude+"\nlongtitude"+longtitude);
                    //final Intent intent = new Intent(ParkingLocation.this, ParkingInformationActivity.class);



                    AlertDialog.Builder builder = new AlertDialog.Builder(ParkingLocation.this);
                    builder.setTitle("Regjistrimi i lokacionit");
                    builder.setMessage("Adresa e zgjedhur eshte: "+rruga+"\nGjeresia gjeografike: "+latitude+"\nGjatesia gjeografike: "+longtitude)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    /*startActivity(intent);
                                    intent.putExtra("latitude",latitude);
                                    intent.putExtra("longtitude", longtitude);
                                    intent.putExtra("street_adr",rruga);
*/
                                   // intent.putExtra("street")
                                   // finish();

                                    //ParkingLocation.this.finish();
                                    parking_location_txt.setText(rruga);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                  /*  startActivity(intent);
                    finish();*/

                }
            });

            init();
        }
    }


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private EditText mSearchText;
    private Button register_location_btn;
    private EditText parking_location_txt;
    private EditText parking_capacity_txt;
    private EditText parking_name_txt;

    private EditText price_upto_2hr;
    private EditText price_upto_4hr;
    private EditText price_upto_8hr;
    private EditText price_mt8hrs;

    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private FirebaseFirestore firebaseFirestore;


    private Button register_parking_button;
    private ProgressBar register_progress_bar;



    //vars
    private Boolean mLocationPermissionsGranted = false;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_location);
        mSearchText = (EditText) findViewById(R.id.input_search);
        register_location_btn = (Button) findViewById(R.id.register_loc_button);
        register_parking_button= (Button) findViewById(R.id.park_register_btn) ;

        parking_location_txt = (EditText) findViewById(R.id.parking_str_addr_txt);
        parking_capacity_txt = (EditText) findViewById(R.id.parking_capacity_number);
        parking_name_txt = (EditText) findViewById(R.id.parking_name_txt);

        price_upto_2hr = (EditText) findViewById(R.id.price_2hr_value) ;
        price_upto_4hr = (EditText) findViewById(R.id.price_4hr_value) ;
        price_upto_8hr = (EditText) findViewById(R.id.price_8hr_value) ;
        price_mt8hrs = (EditText) findViewById(R.id.price_mt8hr_value) ;

        register_progress_bar = (ProgressBar) findViewById(R.id.parking_registration_pbar);

        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

       // price_upto_2hr = (EditText) findViewById(R.id.price_0)
        getLocationPermission();

    }

    private void init(){
        Log.d(TAG, "init: initializing");


        register_parking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String location_info=rruga;
                Integer capacity_number = Integer.parseInt(parking_capacity_txt.getText().toString());
                String parking_name = parking_name_txt.getText().toString();
                Double latitude1= latitude;
                Double longitude1 = longtitude;
                final Double price2hrs = Double.parseDouble(price_upto_2hr.getText().toString());
                Double price4hrs = Double.parseDouble(price_upto_4hr.getText().toString());
                Double price8hrs = Double.parseDouble(price_upto_8hr.getText().toString());
                Double pricemt8hrs = Double.parseDouble(price_mt8hrs.getText().toString());
                String creation_details_time= FieldValue.serverTimestamp().toString();

                if(!TextUtils.isEmpty(location_info) && !TextUtils.isEmpty(parking_name)
                        && !TextUtils.isEmpty(capacity_number.toString())  && !TextUtils.isEmpty(price2hrs.toString())
                        && !TextUtils.isEmpty(price4hrs.toString()) && !TextUtils.isEmpty(price8hrs.toString())
                        && !TextUtils.isEmpty(pricemt8hrs.toString()))  {

                    register_progress_bar.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("location_info",location_info);
                    postMap.put("capacity_number", capacity_number);
                    postMap.put("owner_id", current_user_id);
                    postMap.put("timestamp", creation_details_time);
                    postMap.put("parking_name",parking_name);
                    postMap.put("latitude",latitude1);
                    postMap.put("longitude",longitude1);
                    postMap.put("price2hrs",price2hrs);
                    postMap.put("price4hrs",price4hrs);
                    postMap.put("price8hrs",price8hrs);
                    postMap.put("pricemt8hrs",pricemt8hrs);



                    firebaseFirestore.collection("Parkings").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful()){

                                register_progress_bar.setVisibility(View.INVISIBLE);

                                Log.d(TAG, "onComplete: Regjistrimi i parkingut u krye me sukses!");

                                parking_location_txt.setText(null);
                                parking_capacity_txt.setText(null);
                                parking_name_txt.setText(null);
                                price_upto_2hr.setText(null);
                                price_upto_4hr.setText(null);
                                price_upto_8hr.setText(null);
                                price_mt8hrs.setText(null);




                                //Toast.makeText(ParkingInformationActivity.this, "Parking Added Successfully", Toast.LENGTH_LONG).show();
                              /*  Intent mainIntent= new Intent(ParkingInformationActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();*/

                            }else {
                               // add_parking_progress.setVisibility(View.INVISIBLE);

                                Log.d(TAG, "onComplete: Regjistrimi nuk mundi te behet!");
                               // Toast.makeText(ParkingInformationActivity.this, "Couldn't add parking!", Toast.LENGTH_LONG).show();


                            }
                        }
                    });

                }
                else {
                    Log.d(TAG, "onClick: Mbushni fushat e zbrazeta!");
                }



            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(ParkingLocation.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                            Location currentLocation = (Location) task.getResult();
                            List<Address> addresses;
                            Geocoder geocoder = new Geocoder(ParkingLocation.this);
                            try {
                                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                                String adresa = addresses.get(0).getAddressLine(0);
                              //  Log.d(TAG, "onComplete: Adresa eshte vendbanimi:"+adresa);
                                Log.d(TAG, "onComplete: found location.Current location is : " +currentLocation+ ", locationlocation : "+location);

                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "Rruga:"+adresa);


                            }catch (IOException e){
                                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
                            }



                          //  addresses = geocoder.getFromLocationName(currentLocation.toString(), 1);






                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(ParkingLocation.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(ParkingLocation.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

}

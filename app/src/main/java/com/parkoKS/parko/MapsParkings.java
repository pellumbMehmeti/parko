package com.parkoKS.parko;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsParkings extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapActivity";
    private FirebaseFirestore firebaseFirestore;
    private String parking_id;
    private int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_parkings);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseFirestore = FirebaseFirestore.getInstance();



        firebaseFirestore.collection("Parkings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // lista = new ArrayList<>();
                final List<String> list = new ArrayList<>();
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        Integer docNr = queryDocumentSnapshots.size();
                        Log.d(TAG, "onEvent: Numri i dokumenteve" + docNr);
                        Double lat = doc.getDocument().getDouble("latitude");
                        Double lng = doc.getDocument().getDouble("longitude");
                         final String name = doc.getDocument().getString("parking_name");

                         parking_id = doc.getDocument().getId();
                        list.add(parking_id);



                        LatLng latLng = new LatLng(lat, lng);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(name)).setTag(parking_id);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                        //mMap
                        Log.d(TAG, "onEvent: Emri i parkingut: "+name+"\nLatituda:"+lat+"\nLongituda: "+lng+"\nParking id:"+parking_id);

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                final String intent_id = marker.getTag().toString();

                                Log.d(TAG, "onMarkerClick: pozicioni"+marker.getPosition()+",\n id e parkingut:"+marker.getTag());

                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsParkings.this);
                                builder.setTitle("Vazhdo me rezervim");
                                builder.setMessage("Parkingu i zgjedhur eshte: "+marker.getTitle()+".\nA deshironi te vazhdoni me rezervim?")
                                        .setCancelable(false)
                                        .setPositiveButton("Po", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(MapsParkings.this, BookingActivity.class);
                                                intent.putExtra("parking_id", intent_id);
                                                startActivity(intent);
                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton("Jo", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();


                                //marker.getPosition();
                                return false;
                            }
                        });




                    }

                }
            }        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(42.667505, 21.180561);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Prishtina"));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(sydney.latitude, sydney.longitude))
                .radius(250)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3")));
    }
}

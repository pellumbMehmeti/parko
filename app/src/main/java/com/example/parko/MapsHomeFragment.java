package com.example.parko;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsHomeFragment extends Fragment implements OnMapReadyCallback {
/*    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };*/
    private GoogleMap mMap;
    private static final String TAG = "MapActivity";
    private FirebaseFirestore firebaseFirestore;
    private String parking_id;
    private int i;
    private SupportMapFragment supportMapFragment;


    private BottomNavigationView top_menu_home;

    private MapsHomeFragment mapsHomeFragment;
    private HomeFragment homeFragment;
    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    // private Object SupportMapFragment;



    public MapsHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //supportMapFragment = container.findViewById(R.id.map);
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_maps_home, container, false);
        return view;
    }
        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       // initMap();
getLocationPermission();

        top_menu_home = view.findViewById(R.id.top_menu_map);

        homeFragment = new HomeFragment();
        mapsHomeFragment = new MapsHomeFragment();


        replaceFragment(this);

        top_menu_home.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.map_frag_call:
                        replaceFragment(mapsHomeFragment);
                        return true;
                    case R.id.home_frag_call:
                        replaceFragment(homeFragment);
                        return true;


                    default:
                        return false;

                }
                //  return false;
            }
        });

        //  firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

if( mLocationPermissionsGranted == true) {

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
                    // mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    //mMap
                    Log.d(TAG, "onEvent: Emri i parkingut: " + name + "\nLatituda:" + lat + "\nLongituda: " + lng + "\nParking id:" + parking_id);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            final String intent_id = marker.getTag().toString();

                            Log.d(TAG, "onMarkerClick: pozicioni" + marker.getPosition() + ",\n id e parkingut:" + marker.getTag());

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Vazhdo me rezervim");
                            builder.setMessage("Parkingu i zgjedhur eshte: " + marker.getTitle() + ".\nA deshironi te vazhdoni me rezervim?")
                                    .setCancelable(false)
                                    .setPositiveButton("Po", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                              /*  Intent intent = new Intent(getContext(), BookingFragment.class);
                                                intent.putExtra("parking_id", intent_id);
                                   */

                                            firebaseFirestore.collection("Parkings").document(intent_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        final Bundle bundle = new Bundle();
                                                        bundle.putString("parking_id", intent_id);
                                                        bundle.putString("role", "notuser");
                                                        String parkingowner = task.getResult().get("owner_id").toString();
                                                        Log.d(TAG, "onComplete: pronariid: " + parkingowner);
                                                        bundle.putString("id_pronarit", parkingowner);

                                                        BookingFragment ufr = new BookingFragment();
                                                        ufr.setArguments(bundle);
                                                        FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                                                        fragmentManager.beginTransaction().replace(R.id.main_container, ufr, ufr.getTag()).commit();

                                                    }
                                                }
                                            });
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
        }
    });
//return view;
}
else {
    Log.d(TAG, "onViewCreated: Leshojeni lokacionin");
}
    }
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();



    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void mapManip(){
        
    }
private void initMap(){
    Log.d(TAG, "initMap: duke inicializuar harten");
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


    mapFragment.getMapAsync(this);
}
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng prishtina = new LatLng(42.667505, 21.180561);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

     //   mMap.getUiSettings().setAllGesturesEnabled(true);
     //   mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                return false;
            }
        });

       // Log.d(TAG, "onMapReady: my location latitude:"+mMap.getMyLocation());



        mMap.addCircle(new CircleOptions()
                .center(new LatLng(prishtina.latitude, prishtina.longitude))
                .radius(250)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(prishtina));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
       // mMap.addMarker(new MarkerOptions().position(prishtina).title("Marker in Prishtina")).setTag("rrfafa");
        // Add a marker in Sydney and move the camera
     /*   LatLng sydney = new LatLng(42.667505, 21.180561);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Home")).setTag("TAGUUU");

        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(sydney.latitude, sydney.longitude))
                .radius(250)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3")));*/

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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

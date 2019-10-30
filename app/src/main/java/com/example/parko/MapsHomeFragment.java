package com.example.parko;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
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
    private GoogleMap mMap;
    private static final String TAG = "MapActivity";
    private FirebaseFirestore firebaseFirestore;
    private String parking_id;
    private int i;
    private SupportMapFragment supportMapFragment;

    private BottomNavigationView top_menu_home;

    private MapsHomeFragment mapsHomeFragment;
    private HomeFragment homeFragment;

    // private Object SupportMapFragment;



    public MapsHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //supportMapFragment = container.findViewById(R.id.map);
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_maps_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
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
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
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
                                                Intent intent = new Intent(getContext(), BookingActivity.class);
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
return view;

    }
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(42.667505, 21.180561);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Home")).setTag("TAGUUU");

        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(sydney.latitude, sydney.longitude))
                .radius(250)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3")));

    }

}

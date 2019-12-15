package com.example.parko;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationListFragment extends Fragment {

    private RecyclerView finished_reservations_recyclerView;
    private List<ReservationPost> reservationList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ReservationRecyclerAdapter reservationRecyclerAdapter;
    private Switch switchMode;
    private TextView nr_rez;

    public ReservationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reservation_list, container, false);

        reservationList = new ArrayList<>();
        finished_reservations_recyclerView = view.findViewById(R.id.finished_reservations_recyclerView);
       // switchMode = view.findViewById(R.id.switch_btn);
        //nr_rez = view.findViewById(R.id.total_reservation_nr);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reservationRecyclerAdapter = new ReservationRecyclerAdapter(reservationList);

        finished_reservations_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        finished_reservations_recyclerView.setAdapter(reservationRecyclerAdapter);
        //switchMode.setOnCheckedChangeListener(reservationRecyclerAdapter.th);
/*
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    Toast.makeText(getContext(), "checked",Toast.LENGTH_LONG).show();
                    firebaseFirestore.collection("Reservations").whereEqualTo("registration_plates","01255").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                                if((doc.getType() == DocumentChange.Type.ADDED )|| (doc.getType() == DocumentChange.Type.MODIFIED)){
                                    ReservationPost reservationPost = doc.getDocument().toObject(ReservationPost.class);
                                   // reservationList.add(reservationPost);

                                    reservationRecyclerAdapter.notifyDataSetChanged();
                                   // nr_rez.setText(reservationRecyclerAdapter.getItemCount());
                                    Log.d(TAG, "onEvent: Numri i reservimve:"+reservationRecyclerAdapter.getItemCount());
                                }
                            }
                        }
                    });
                }

            }

        });*/
firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if(task.isSuccessful()){
            String role = task.getResult().getString("user_type");
            Log.d(TAG, "onComplete: Roli i userit te ReservationList eshte :"+role+"\nNdersa id e userit eshte:"+firebaseAuth.getCurrentUser().getUid());
            if((role.equals("Parking Owner"))){
                firebaseFirestore.collection("Parkings").whereEqualTo("owner_id",firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Po mirren dokumente");
                            ArrayList lista = new ArrayList();
                            HashMap<String,Double> listamap = new HashMap<String,Double>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String emri_parkingut = document.get("parking_name").toString();
                                Double fitimi = document.getDouble("earnings");
                                listamap.put(emri_parkingut, fitimi);
                            //    lista.add(emri_parkingut);
                               // Log.d(TAG, "Dokumentet e marre:"+listamap);
                                Double shuma = 0.0;
                                //shuma = shuma + fitimi;
                               // Log.d(TAG, "onComplete: shuma totale:"+shuma);
//                                Float fitimifloat = fitimi.floatValue();
  //                              Log.d(TAG, "onComplete: NE RREGULL VLERAT FLOAT JANE:"+fitimifloat);


                            }
                           // Log.d(TAG, "onComplete: 'LISTA PAS DALJES NGA LOOPA:"+listamap+"\nMadhesia"+listamap.size());
                          /*  Double shuma =0.0;
                            for(Double dbl: listamap.values()){
                                shuma += dbl;
                                //Log.d(TAG, "onComplete: shuma e hashmapit:"+shuma);
                            }
                            //Log.d(TAG, "onComplete: shuma jasht forit:"+shuma);
                            Log.d(TAG, "onComplete: 'Lista e parkingjeve me fitimet:"+listamap+"\nMadhesia:"+listamap.size()+"\nShuma totale:"+shuma);
*/

                        }
                        else {
                            Log.d(TAG, "onComplete: Error ghate marrjes se dokumenteve");
                        }
                    }
                });

                firebaseFirestore.collection("Reservations").whereEqualTo("parking_owner_id",firebaseAuth.getCurrentUser().getUid()).whereEqualTo("paid",true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                            if((doc.getType() == DocumentChange.Type.ADDED )|| (doc.getType() == DocumentChange.Type.MODIFIED)){
                                ReservationPost reservationPost = doc.getDocument().toObject(ReservationPost.class);


                                reservationList.add(reservationPost);
                                String targa = doc.getDocument().getString("registration_plates");
                                Log.d(TAG, "onEvent: targa :"+targa);

                                reservationRecyclerAdapter.notifyDataSetChanged();
                                reservationRecyclerAdapter.getItemCount();
                                Log.d(TAG, "onEvent: Reservation List Fragment.Numri i elementeve eshte:"+reservationRecyclerAdapter.getItemCount());


                            }
                        }
                    }
                });
            }
            else if((role.equals("Ordinary User"))){

                firebaseFirestore.collection("Reservations").whereEqualTo("paid",true).whereEqualTo("reserved_by",firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                            if((doc.getType() == DocumentChange.Type.ADDED )|| (doc.getType() == DocumentChange.Type.MODIFIED)){
                                ReservationPost reservationPost = doc.getDocument().toObject(ReservationPost.class);
                                reservationList.add(reservationPost);
                                String targa = doc.getDocument().getString("registration_plates");
                                Log.d(TAG, "onEvent: targa :"+targa);

                                reservationRecyclerAdapter.notifyDataSetChanged();
                                //   reservationRecyclerAdapter.get
                                //   nr_rez.setText(reservationRecyclerAdapter.getItemCount());

                            }
                        }
                    }
                });
            }
            else if((role.equals("Parking Worker"))) {
                Boolean isFired=task.getResult().getBoolean("fired");
                String workingPlace = task.getResult().getString("works_at");
                if(isFired.equals(true)){
                    firebaseFirestore.collection("Reservations").whereEqualTo("reserved_by",firebaseAuth.getCurrentUser().getUid()).whereEqualTo("paid",true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                                if((doc.getType() == DocumentChange.Type.ADDED )|| (doc.getType() == DocumentChange.Type.MODIFIED)){
                                    ReservationPost reservationPost = doc.getDocument().toObject(ReservationPost.class);
                                   // reservationList.clear();
                                    reservationList.add(reservationPost);
                                    String targa = doc.getDocument().getString("registration_plates");
                                    Log.d(TAG, "onEvent: targa :"+targa);

                                    reservationRecyclerAdapter.notifyDataSetChanged();
                                    //   reservationRecyclerAdapter.get
                                    //   nr_rez.setText(reservationRecyclerAdapter.getItemCount());

                                }
                            }
                        }
                    });
                }
                else if(isFired.equals(false)){
                    firebaseFirestore.collection("Reservations").whereEqualTo("parkingId",workingPlace).whereEqualTo("paid",true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                                if((doc.getType() == DocumentChange.Type.ADDED )|| (doc.getType() == DocumentChange.Type.MODIFIED)){
                                    ReservationPost reservationPost = doc.getDocument().toObject(ReservationPost.class);
                                    reservationList.add(reservationPost);
                                    String targa = doc.getDocument().getString("registration_plates");
                                    Log.d(TAG, "onEvent: targa :"+targa);

                                    reservationRecyclerAdapter.notifyDataSetChanged();
                                    //   reservationRecyclerAdapter.get
                                    //   nr_rez.setText(reservationRecyclerAdapter.getItemCount());

                                }
                            }
                        }
                    });
                }
            }
        }
    }
});



        return view;
    }

}

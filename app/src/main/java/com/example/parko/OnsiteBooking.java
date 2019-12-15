package com.example.parko;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnsiteBooking extends Fragment {

    private TextInputEditText onsite_licence_plates;
    private TextInputEditText onsite_check_in_time;
    private Button onsite_check_in_time_btn;
    private Button onsite_confirm_reservation;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public String onsite_time;


    public OnsiteBooking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onsite_booking, container, false);


    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        onsite_licence_plates = (TextInputEditText) getView().findViewById(R.id.plates_text_input);
        onsite_check_in_time = (TextInputEditText) getView().findViewById(R.id.chkIn_onsite_txt_input);
        onsite_check_in_time_btn = (Button) getView().findViewById(R.id.onsite_clock_btn);
        onsite_confirm_reservation = (Button) getView().findViewById(R.id.onsite_register_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();





        onsite_check_in_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                Date currentLocalTime = cal.getTime();

                DateFormat date = new SimpleDateFormat("HH:mm a");

                DateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat ddmy = new SimpleDateFormat("MMM d, yyyy");

                date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));


                String localTime = date.format(currentLocalTime);
                String localDate = dmy.format(currentLocalTime);
                String date2 = ddmy.format(currentLocalTime);

                Log.d(TAG, "onClick: koha e tanishme:\n" + date2);

                onsite_time = localTime;
                onsite_check_in_time_btn.setEnabled(false);
                onsite_check_in_time.setText(date2);
            }
        });

        onsite_confirm_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                          final  Boolean dissable_adding = task.getResult().getBoolean("fired");
                            String parkingId=task.getResult().getString("works_at");

                            Log.d(TAG, "onComplete: PUNETORI NUK MUND TE PUNOJE?\t"+dissable_adding);
                            firebaseFirestore.collection("Parkings").document(parkingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Double free_places=task.getResult().getDouble("current_free_places");
                                        if (free_places == 0){
                                            AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                            alertDialog.setTitle("Informacion");
                                            alertDialog.setMessage("Nuk mund te kryhet rezervimi sepse nuk ka vende te lira. Faleminderit per mirekuptim.");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                            startActivity(intent);

                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }
                                        else
                                        {
                                            if(dissable_adding.equals(false)){
                                                decreaseFreePlaces();
                                                onsiteBook();
                                            }
                                            else {
                                                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Informacion");
                                                alertDialog.setMessage("Nuk keni te drejte te beni rezervime sepse me nuk figuroni punetor ne parkingun aktual.Vazhdo ne faqen kryesore.");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);

                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                        }
                                    }
                                    else {
                                        Log.d(TAG, "onComplete: Error:\t"+task.getException());
                                    }
                                }
                            });

                        }

                    }
                });
               /* decreaseFreePlaces();
                onsiteBook();*/
            }
        });


    }

    public void decreaseFreePlaces() {
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String parking_id = documentSnapshot.getString("works_at");
                Log.d(TAG, "onSuccess: PUNETORI AKTUAL PUNON NE:" + parking_id);

                final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document(parking_id);


                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                        double newFreeSpaces = snapshot.getDouble("current_free_places") - 1;
                        transaction.update(docUpdateReference, "current_free_places", newFreeSpaces);


                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "The number of free places is decreased by 1!");


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.NUK MUND TE PERDITESOHEN TE DHENAT", e);


                    }
                });
            }
        });
    }

    public void onsiteBook() {
        if (!TextUtils.isEmpty(onsite_licence_plates.getText()) && !TextUtils.isEmpty(onsite_check_in_time.getText())) {
            Log.d(TAG, "onClick: TE DHENAT PO RUHEN NE DATABAZE");
            firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final String whereWorks = documentSnapshot.getString("works_at");
                    Log.d(TAG, "onSuccess: Punetori punon ne vendin: " + whereWorks);
                    firebaseFirestore.collection("Parkings").document(whereWorks).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String pronariParkingut = documentSnapshot.getString("owner_id");
                            Log.d(TAG, "onSuccess: Parkingu: " + whereWorks + ", i takon pronarit me id: " + pronariParkingut + "\ndata e rezervimit: " + onsite_check_in_time.getText());
                            String datatest = onsite_check_in_time.getText().toString();
                            String timetest = onsite_time;
                            String plates = onsite_licence_plates.getText().toString();
                            Map<String, Object> reservationsMap = new HashMap<>();

                            reservationsMap.put("reservation_date", datatest);
                            reservationsMap.put("reservation_time", timetest);
                            reservationsMap.put("checkin_time", FieldValue.serverTimestamp());
                            reservationsMap.put("timestamp", FieldValue.serverTimestamp());
                            reservationsMap.put("registration_plates", plates);
                            reservationsMap.put("reserved_by", firebaseAuth.getCurrentUser().getUid());
                            reservationsMap.put("paid", false);
                            reservationsMap.put("parkingId", whereWorks);
                            reservationsMap.put("parking_owner_id", pronariParkingut);
                            reservationsMap.put("reservation_type", "ONSITE");

                            firebaseFirestore.collection("Reservations").add(reservationsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    /*                   */

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                    Log.d(TAG, "onSuccess: Me sukses u shtua rezervimi!");

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Nuk munden te procesohen te dhenat: " + e.getMessage());
                        }
                    });

                }
            });


        }
    }
}

package com.example.parko;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment {

    private static final String TAG = "MapActivity";
    private Button datePicker;
    private Button timePicker;
    private TextInputEditText date_picked_txt;
    private TextInputEditText  time_picked_txt;
    private TextInputEditText car_plates_edit_txt;

    private Button check_in;
    private Button check_out;

    private Button finish_button;

    private TextView calculatedPrice;

    public  Boolean alreadyexecuted = false;



    private String testid;
    private Button reserve_button;
    public Double test00;
    public Double sendToDb;
    public String pronari_parkingut;
    private Double free_places;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference firebaseFirestore1;
    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    public BookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = getArguments();

        String parkingId = bundle.getString("parking_id");
String pronarit_id=bundle.getString("id_pronarit");

        Log.d(TAG, "onCreate: Id e pranuar:"+parkingId+"\n,kurse Numri Unik Identifikues i Pronarit(NUIP) eshte:"+pronarit_id);
        return inflater.inflate(R.layout.activity_booking, container, false);


    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        testid = bundle.getString("parking_id");
        pronari_parkingut =bundle.getString("id_pronarit");
        free_places = bundle.getDouble("current_free_places");
        datePicker = (Button)getView().findViewById(R.id.date_picker_btn);
        timePicker = (Button)getView().findViewById(R.id.time_picker_btn);

        car_plates_edit_txt = (TextInputEditText)getView().findViewById(R.id.plate_number_edit);



        check_in = (Button) getView().findViewById(R.id.button_checkin_time);
        check_out = (Button)getView().findViewById(R.id.button_checkout_time);
        //    finish_button = (Button) findViewById(R.id.finish_btn);

        //   calculatedPrice = (TextView) findViewById(R.id.calculated_price_text);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore1 = FirebaseFirestore.getInstance().collection("Reservations");






        reserve_button = (Button)getView().findViewById(R.id.reserve_parking_btn);


        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


freePlacesTransaction();




            }
        });
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Map<String, Object> checkoutMap = new HashMap<>();
                checkoutMap.put("checkout_time", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Parkings/"+testid+"/Reservations").document("siqhidvx3XQ0VnUo5fxVrAHZ4Ji2").update(checkoutMap);

                final String TAG = "MyActivity";



                final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document("FT0eoF2LjVgzRIh05CsA");
                // final Integer decrement = FieldValue

                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                        double newFreeSpaces = snapshot.getDouble("free_places")+1;
                        transaction.update(docUpdateReference, "free_places", newFreeSpaces);

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "The number of free spaces is increased by 1!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);

                        //Toast.makeText(BookingActivity.this, "Transaction failure: "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });




        //   manipulation_test = (TextView) findViewById(R.id.car_plates_txt);




        date_picked_txt = (TextInputEditText ) getView().findViewById(R.id.book_date_text);
        time_picked_txt = (TextInputEditText )  getView().findViewById(R.id.book_time_text);

        reserve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer platesLength= car_plates_edit_txt.getText().length();
                Integer dateLength = date_picked_txt.getText().length();
                Integer timeLength = time_picked_txt.getText().length();
                if((!(dateLength == 0) && !(timeLength ==0) && !(platesLength ==0)))
                { handleBooking();}
/*
Boolean testbool= car_plates_edit_txt.getText().equals(null);
String vlerabooleane = car_plates_edit_txt.getText().toString();
//Boolean bool1 = ;
                Boolean testbool1= car_plates_edit_txt.getText().equals("");
*/


          //      Log.d(TAG, "onClick: VLERA ESHE: "+testbool+"\nKurse v;era e test bool1 eshet:"+testbool1+"\nKurse vlera e textit eshte:"+vlerabooleane+"\nGjatesia:"+);

              if((dateLength == 0) && (timeLength == 0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Zgjedh datën dhe orën për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if((dateLength == 0) && (platesLength == 0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Zgjedh datën dhe shkruaj targat për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if((timeLength == 0) && (platesLength ==0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Zgjedh kohën dhe shkruaj targat për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if((dateLength == 0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Zgjedh datën për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if((timeLength == 0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Zgjedh kohën për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else if((platesLength == 0)){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Verejtje");
                    alertDialog.setMessage("Shkruaj targat për të vazhduar me rezervim!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if(((dateLength == 0) && (timeLength ==0) && (platesLength ==0))){
                  AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                  alertDialog.setTitle("Verejtje");
                  alertDialog.setMessage("Zgjedh datën, orën dhe shkruaj targat për të vazhduar me rezervim!");
                  alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                          new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {

                                  dialog.dismiss();
                              }
                          });
                  alertDialog.show();

              }


            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton();
            }
        });


    }

    private void handleBooking() {
       /* if((!date_picked_txt.equals(null) && !(time_picked_txt.equals(null)) && !(car_plates_edit_txt.equals(null))))
        {*/
            firebaseFirestore.collection("Parkings").document(testid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Double free_places = task.getResult().getDouble("current_free_places");
                        if (free_places == 0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
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

                        } else {
                            freePlacesManip();
                            bookParking();

                        }
                    } else {
                        Log.d(TAG, "onComplete: Error:" + task.getException());
                    }
                }
            });
        }





public void freePlacesManip(){
    final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document(testid);
    firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
        @Override
        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
            DocumentSnapshot snapshot = transaction.get(docUpdateReference);

            double newFreeSpaces = snapshot.getDouble("current_free_places")-1;
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
            Log.w(TAG, "Transaction failure.", e);



        }
    });


}
    public void bookParking(){ 
        String date_selected = date_picked_txt.getText().toString();
        String time_selected = time_picked_txt.getText().toString();
        String stringToCompare =  "Sat Jan 01 00:00:00 GMT+01:00 2000";
        String str_date="Sat, 01 Jan 2000 00:00:00 GMT ";
        Date localTime = null;
        //   try {
        try {

            localTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).parse(str_date);
            Log.d(TAG, "onEvent: koha lokale:   "+localTime   );
            System.out.println("TimeStamp is " +localTime.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        //final String testtest= documentSnapshot.getString("owner_id");
        String currentUserId = firebaseAuth.getUid();
        Log.d(TAG, "bookParking: current user id is: ");
        String car_plates=car_plates_edit_txt.getText().toString();

        final Map <String, Object> reservationsMap = new HashMap<>();
        reservationsMap.put("reservation_date", date_selected);
        reservationsMap.put("reservation_time", time_selected);
        reservationsMap.put("timestamp", FieldValue.serverTimestamp());
        reservationsMap.put("checkin_time",       localTime);
        reservationsMap.put("registration_plates", car_plates);
        reservationsMap.put("reserved_by", currentUserId);
        reservationsMap.put("paid",false);
        //reservationsMap.put("checkin_time",FieldValue.serverTimestamp());

        reservationsMap.put("parkingId", testid);
        reservationsMap.put("parking_owner_id",pronari_parkingut );
        reservationsMap.put("reservation_type","REMOTE");//NDRYSHIM

        firebaseFirestore.collection("Reservations").add(reservationsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: Rezervimi u shtua me sukses.");
               /* Intent intent = new Intent(getContext(),MainActivity.class);
                startActivity(intent);*/
                AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                alertDialog.setTitle("Informacion");
                alertDialog.setMessage("Rezervimi i parkingut u krye me sukses. Faleminderit.");
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
        });

    }
    public void freePlacesTransaction(){
        final String TAG = "MyActivity";
        final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document(testid);

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                double newFreeSpaces = snapshot.getDouble("current_free_places")-1;
                transaction.update(docUpdateReference, "current_free_places", newFreeSpaces);
                //    transaction.set(docUpdateReference1,reservationsMap);


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
                Log.w(TAG, "Transaction failure.", e);

                //Toast.makeText(BookingActivity.this, "Transaction failure: "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });





    }

    private void handleTimeButton() {

        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24hourformat = DateFormat.is24HourFormat(getContext());

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                //String timeString = "hour:" +hour + " minute " +minute;
                //time_picked_txt.setText(timeString);

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);

                CharSequence charSequence = DateFormat.format("hh:mm a", calendar1);
                time_picked_txt.setText(charSequence);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR, hour+1);
                calendar2.set(Calendar.MINUTE, minute);

                CharSequence charSequence1= DateFormat.format("hh:mm a", calendar2);

                //  manipulation_test.setVisibility(View.VISIBLE);
                //  manipulation_test.setText(charSequence1);






            }
        },HOUR,MINUTE,is24hourformat);



        long now = System.currentTimeMillis() - 1000;
        long for1hour= HOUR+1;


        timePickerDialog.show();
        // timePickerDialog
        // Toast.makeText(BookingActivity.this, "1 HOUR FROM NOW: "+for1hour+ " MINUTES: " +MINUTE, Toast.LENGTH_LONG).show();


    }

    private void handleDateButton() {

        final Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);



        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String dateString = date + " " + month + " " + year;
                date_picked_txt.setText(dateString);//january llogariutet month 0; december month11

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);

                CharSequence dateCharSequence = DateFormat.format("MMM d, yyyy", calendar1);
                date_picked_txt.setText(dateCharSequence);




            }
        },YEAR,MONTH,DATE);

        long now = System.currentTimeMillis() - 1000;

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(now);//nuk len me pickirat data ma tvjetra se today
        datePickerDialog.getDatePicker().setMaxDate(now+(1000*60*60*24*7));//data maksimale e rezervimit 1 jave prej dates aktuale
    }



    }

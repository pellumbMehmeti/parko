package com.example.parko;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "MapActivity";
    private Button datePicker;
    private Button timePicker;
    private TextView date_picked_txt;
    private TextView time_picked_txt;
    private EditText car_plates_edit_txt;

    private Button check_in;
    private Button check_out;

    private Button finish_button;

    private TextView calculatedPrice;



    private String testid;
    private Button reserve_button;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        datePicker = (Button) findViewById(R.id.date_picker_btn);
        timePicker = (Button) findViewById(R.id.time_picker_btn);

       car_plates_edit_txt = (EditText) findViewById(R.id.plate_number_edit);


        check_in = (Button) findViewById(R.id.button_checkin_time);
        check_out = (Button) findViewById(R.id.button_checkout_time);
        finish_button = (Button) findViewById(R.id.finish_btn);

        calculatedPrice = (TextView) findViewById(R.id.calculated_price_text);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();




        reserve_button = (Button) findViewById(R.id.reserve_parking_btn);

        testid = getIntent().getExtras().getString("parking_id");

        Log.d(TAG, "onCreate: Id e pranuar:"+testid);

        //Toast.makeText(BookingActivity.this, "parking id: " +testid, Toast.LENGTH_LONG).show();

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> testMap = new HashMap<>();
                testMap.put("number_of_reservations", 0);

                firebaseFirestore.collection("Parkings").document("FT0eoF2LjVgzRIh05CsA").update(testMap);

            }
        });

        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentUserId = firebaseAuth.getUid();

                Map<String, Object> checkInMap = new HashMap<>();
                checkInMap.put("checkin_time", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Parkings/"+testid+"/Reservations").document("siqhidvx3XQ0VnUo5fxVrAHZ4Ji2").update(checkInMap);


                final String TAG = "MyActivity";

                final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document("FT0eoF2LjVgzRIh05CsA");
                // final Integer decrement = FieldValue

                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                        double newFreeSpaces = snapshot.getDouble("free_places")-1;
                        transaction.update(docUpdateReference, "free_places", newFreeSpaces);

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


                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                        double testnr = snapshot.getDouble("number_of_reservations")-1;
                        transaction.update(docUpdateReference, "number_of_reservations", testnr);

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "The number of successfull reservations is decreased by 1!");
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




        date_picked_txt = (TextView) findViewById(R.id.book_date_text);
        time_picked_txt = (TextView) findViewById(R.id.book_time_text);

        reserve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBooking();
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

        final String TAG = "MyActivity";

        //  firebaseFirestore.collection("Parkings").document(testid).a
    //    final DocumentReference referenca = firebaseFirestore.collection("Parkings").document(testid).get;

       // String parkingOwnerId= referenca.

   //     Query getParkingOwner= firebaseFirestore.collection("Parkings").document(testid).
        firebaseFirestore.collection("Parkings").document(testid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
           // public String testtest;
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String testtest= documentSnapshot.getString("owner_id");
                String currentUserId = firebaseAuth.getUid();

                String car_plates=car_plates_edit_txt.getText().toString();

                Map <String, Object> reservationsMap = new HashMap<>();
                reservationsMap.put("reservation_date", date_picked_txt.getText());
                reservationsMap.put("reservation_time", time_picked_txt.getText());
                reservationsMap.put("timestamp", FieldValue.serverTimestamp());
                reservationsMap.put("registration_plates", car_plates);
                reservationsMap.put("reserved_by", currentUserId);
                reservationsMap.put("paid",false);
                reservationsMap.put("parkingId", testid);
                reservationsMap.put("parking_owner_id",testtest );

                firebaseFirestore.collection("Reservations").add(reservationsMap);

                Log.w(TAG, "reservation_date:"+date_picked_txt.getText()+", reservation_time"+time_picked_txt.getText()+", timestamp"+FieldValue.serverTimestamp()+", registration_plates"+car_plates+", reserved_by"+currentUserId+", parkingId"+testtest);






            }
        });
        String currentUserId = firebaseAuth.getUid();

        String car_plates=car_plates_edit_txt.getText().toString();
/*
        Map <String, Object> reservationsMap = new HashMap<>();
        reservationsMap.put("reservation_date", date_picked_txt.getText());
        reservationsMap.put("reservation_time", time_picked_txt.getText());
        reservationsMap.put("timestamp", FieldValue.serverTimestamp());
        reservationsMap.put("registration_plates", car_plates);
        reservationsMap.put("reserved_by", currentUserId);
        reservationsMap.put("paid",false);
        reservationsMap.put("parkingId", testid);*/

        // reservationsMap.put("parking_owner_id",testtest );





       // firebaseFirestore.collection("Reservations").add(reservationsMap);

       /* String currentUserId = firebaseAuth.getUid();

        String car_plates=car_plates_edit_txt.getText().toString();

        Map <String, Object> reservationsMap = new HashMap<>();
        reservationsMap.put("reservation_date", date_picked_txt.getText());
        reservationsMap.put("reservation_time", time_picked_txt.getText());
        reservationsMap.put("timestamp", FieldValue.serverTimestamp());
        reservationsMap.put("registration_plates", car_plates);
        reservationsMap.put("reserved_by", currentUserId);
        reservationsMap.put("paid",false);
        reservationsMap.put("parkingId", testid);
      //  reservationsMap.put("parking_owner_id", )

*/



//        firebaseFirestore.collection("Reservations").add(reservationsMap);

       /* final String TAG = "MyActivity";

        final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document("FT0eoF2LjVgzRIh05CsA");
        // final Integer decrement = FieldValue

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                double testnr = snapshot.getDouble("number_of_reservations")+1;
                transaction.update(docUpdateReference, "number_of_reservations", testnr);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BookingActivity.this, "Transaksioni u krye me sukses!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);

                //Toast.makeText(BookingActivity.this, "Transaction failure: "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });*/




    }

    private void handleTimeButton() {

        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24hourformat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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



        DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
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

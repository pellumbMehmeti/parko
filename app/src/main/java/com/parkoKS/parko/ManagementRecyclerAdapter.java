package com.parkoKS.parko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.Transaction;

import java.sql.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ManagementRecyclerAdapter extends RecyclerView.Adapter<ManagementRecyclerAdapter.ViewHolder> {

    public List<ManagementPost> managementList;
    private FirebaseFirestore firebaseFirestore;
    public Integer itemselected;
    public boolean isset;
    private FirebaseAuth firebaseAuth;
    public static int saveState;

    public ManagementRecyclerAdapter(List<ManagementPost> managementList) {
        this.managementList = managementList;

    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reservation_item, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mView.setTag(position);

        String managementPostId = managementList.get(position).ManagementPostId;

        String plates_data = managementList.get(position).getRegistration_plates();
        String arrive_time_data = managementList.get(position).getReservation_time();
        Date check_in_time = managementList.get(position).getCheck_in_time();
        Timestamp check_out_time = managementList.get(position).getCheck_out_time();




    /*    String time01 = check_in_time.toString();

        if (time01 != null){
            Log.d(TAG, "onBindViewHolder: NUK ESHTE NULL");
        }*/
        if (check_in_time != null) {
            String stringcheckin = check_in_time.toString();
            Log.d(TAG, "onBindViewHolder: KOHA E HR:" + stringcheckin);

            holder.setRegistrationNumber(plates_data);
            //  holder.setCHKintime(check_in_time);
            holder.getAdapterPosition();
            Log.d(TAG, "onBindViewHolder:TIME 1 CHECK IN: " + check_in_time);
            Log.w(TAG, "ID CURRDORC:" + managementPostId);
            //   holder.setPrice(check_in_time, check_out_time);
            holder.setArrivalTime(arrive_time_data);
            holder.setCheckIn(stringcheckin);
        }
        /*else {
            holder.setRegistrationNumber(plates_data);
            //  holder.setCHKintime(check_in_time);
            holder.getAdapterPosition();
            Log.d(TAG, "onBindViewHolder:TIME 1 CHECK IN: "+check_in_time);
            Log.w(TAG,"ID CURRDORC:"+managementPostId);
            //   holder.setPrice(check_in_time, check_out_time);
            holder.setArrivalTime(arrive_time_data);
        }*/
        //}


        holder.setRegistrationNumber(plates_data);
        //  holder.setCHKintime(check_in_time);
        holder.getAdapterPosition();
        Log.d(TAG, "onBindViewHolder:TIME 1 CHECK IN: " + check_in_time);
        Log.w(TAG, "ID CURRDORC:" + managementPostId);
        //   holder.setPrice(check_in_time, check_out_time);
        holder.setArrivalTime(arrive_time_data);
        //.    holder.setCheckIn(stringcheckin);
    }

    @Override
    public int getItemCount() {
        return managementList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView plates_number_text;
        private TextView starting_time_text;
        String id_parking;
        private TextView calculated_price;

        private Button check_in_btn;
        private Button check_out_btn;
        private CheckBox paid_chk_box;
        private Date currentLocalTime0;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;

            check_in_btn = mView.findViewById(R.id.arrive_time_btn);
            check_out_btn = mView.findViewById(R.id.departure_time_btn);
            paid_chk_box = mView.findViewById(R.id.payment_chk_box);

        //    paid_chk_box.setEnabled(false);
//firebaseFirestore.collection("Users")

            firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        String usertype=task.getResult().getString("user_type");
                        Log.d(TAG, "onComplete: USER TYPE ESHTE:"+usertype);
                        if(usertype.equals("Parking Owner")){
                          // check_in_btn.setEnabled(false);
                            //check_out_btn.setEnabled(false);
                            //paid_chk_box.setEnabled(false);

                        }
                        else if(usertype.equals("Ordinary User")){
                            check_out_btn.setEnabled(false);
                            check_in_btn.setEnabled(false);
                            paid_chk_box.setEnabled(false);
                        }
                        else if(usertype.equals("Parking Worker")){
                            Boolean fired = task.getResult().getBoolean("fired");
                            if(fired.equals(true)){
                                check_out_btn.setEnabled(false);
                                check_in_btn.setEnabled(false);
                                paid_chk_box.setEnabled(false);
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "onComplete: ERROR:"+task.getException());
                    }
                }
            });

            paid_chk_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String[] myStringArray = new String[3];

                    myStringArray = new String[]{"Vetura me targën përkatëse nuk u shfaq. Anulo rezervimin!", "Rezervimi përfundoi dhe vetura u largua. Largo nga lista!"};
                        final ArrayList selectedItems = new ArrayList();  // Where we track the selected items
                        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());

                        builder.setTitle("Zgjidhni njërin nga opsionet").setSingleChoiceItems(myStringArray, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which ==0){
                                    Log.d(TAG, "onClick: itemi i klikuar eshte 0");
                                    isset=true;
                                    itemselected =0;

                                }
                                else if (which == 1) {
                                    isset=true;
                                    Log.d(TAG, "onClick: itemi i klikuar eshte 1");

                                    itemselected = 1;
                                }
                                isset=false;


                            }
                        })

                                .setPositiveButton("vazhdo", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        Log.d(TAG, "onClick: e selektuar eshte :"+itemselected+"isset??"+isset);
                                       // if (isset == true){
                                        if(itemselected.equals(0)){
                                            String dontallow1 = check_in_btn.getText().toString();
                                            String dontallow2 = check_out_btn.getText().toString();
                                            Log.d(TAG, "onClick: dontallow1"+dontallow1+"\ndont allow2:"+dontallow2);
                                            if(!(dontallow1.equals("HYRJA"))){
                                                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Rezervimi i zgjedhur nuk mund te anulohet sepse eshte aktiv.");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                            else{
                                                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("A jeni i sigurt se doni ta largoni rezervimin nga lista");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "PO",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                handleFailedReservation();
                                                                increaseFreePlaces();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                        }
                                        else if (itemselected.equals(1)){
                                            String dontallow1 = check_in_btn.getText().toString();
                                            String dontallow2 = check_out_btn.getText().toString();

                                           Log.d(TAG, "onClick: check in text is :"+dontallow1);
                                            Log.d(TAG, "onClick: check out text is :"+dontallow2);
                                            if((dontallow1.equals("HYRJA"))&&(dontallow2.equals("DALJA") )){
                                                Log.d(TAG, "onClick: brravo");
                                               AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Kliko mbi butonat per hyrje dhe dalje");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                            else if((dontallow1.equals("HYRJA"))&&!(dontallow2.equals("DALJA") )){
                                                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Kliko mbi butonin per hyrje.");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                            else if(!(dontallow1.equals("HYRJA"))&&(dontallow2.equals("DALJA") )){
                                                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Kliko mbi butonin per dalje.");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                            else if (!(dontallow1.equals("HYRJA"))&&!(dontallow2.equals("DALJA") )){
                                                increaseEarnings();
                                                increaseFreePlaces();
                                                handleCheckOut();

                                              //  managementList.remove(getAdapterPosition());
                                            }

                                        }
                /*            else if(isset==false){
                              dialog.dismiss();
                                            Log.d(TAG, "onClick: po mshelet");
                                        }*/



                                    }
                                })
                                .setNegativeButton("mbyll", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
                        ;
                    builder.show();


                }
            });


            check_in_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkInManagement();
                   // decreaseFreePlaces();
                }
            });


            check_out_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String document = managementList.get(getAdapterPosition()).ManagementPostId;

                    Log.d(TAG, "onClick: koha e check init: " + check_in_btn.getText());

                    String testtest = "WV49nNKf17fLWEKvcbSe";
                    Map<String, Object> checkOutMap = new HashMap<>();
                    checkOutMap.put("checkout_time", FieldValue.serverTimestamp());
                    final String TAG = "MyActivity";


                    firebaseFirestore.collection("Reservations").document(document).update(checkOutMap).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w(TAG, "Failure:" + e.getMessage());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully updated!");
                            paid_chk_box.setEnabled(true);
                        }
                    });
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                    Date currentLocalTime = cal.getTime();
                    DateFormat date = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
// you can get seconds by adding  "...:ss" to it
                    date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

                    String localTime = date.format(currentLocalTime);


                    check_out_btn.setText(localTime);
                    check_out_btn.setTextColor(Color.WHITE);
                    check_out_btn.setTextSize(12);
                //    check_out_btn.setBackgroundColor(Color.TRANSPARENT);
                    //check_out_btn.setBackground(R.drawable.textview_border);


                    String check_in_time = check_in_btn.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                    try {
                        Date time1 = format.parse(check_in_time);
                        Log.d(TAG, "Koha e arritjes: " + check_in_time + "\nKoha e largimit: " + currentLocalTime);
                        long difference = (currentLocalTime.getTime() - time1.getTime()) / 1000;
                        Log.d(TAG, "diferenca:" + difference + "\nNumri i dokumentit jasht koleksionit: " + id_parking);
                        Integer days = (int) (difference / (1000 * 60 * 60 * 24));
                        final Integer hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        Integer min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);


                        //if (hours >= 0 && hours<=1){
                        firebaseFirestore.collection("Reservations").document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        id_parking = document.get("parkingId").toString();
                                        Log.d(TAG, "DocumentSnapshot data.Nmr i parkingut: " + id_parking);

                                        firebaseFirestore.collection("Parkings").document(id_parking).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {

                                                        if (hours >= 0 && hours <= 2) {

                                                            Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price2hrs"));
                                                            calculated_price.setText(document.getDouble("price2hrs").toString());
                                                        } else if (hours > 2 && hours <= 4) {
                                                            Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price4hrs"));
                                                            calculated_price.setText(document.getDouble("price4hrs").toString());
                                                            //Log.d(TAG, "onComplete: gabimgabimgaibm");
                                                        } else if (hours > 4 && hours <= 8) {
                                                            Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price8hrs"));
                                                            calculated_price.setText(document.getDouble("price8hrs").toString());
                                                        } else if (hours > 8) {
                                                            Double cmimi8hr = document.getDouble("price8hrs");
                                                            Double cmimimt8hr = document.getDouble("pricemt8hrs");
                                                            Double totali = cmimi8hr + cmimimt8hr * hours;
                                                            calculated_price.setText(totali.toString());

                                                        } else {
                                                            Log.d(TAG, "onComplete: Gabim gjate futjes se te dhenave.");
                                                        }


                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }

                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                        Log.d(TAG, "Numri i dokumentit jasht koleksionit: " + document);

                        // }

                        Log.d(TAG, "Diferenca e shprehur ne sekonda:" + difference + "\nDite:" + days + "\nOre:" + hours + "\nMinuta:" + min);
                    } catch (Exception e) {
                        Log.w(TAG, "Failure:" + e.getMessage());
                    }


                    calculated_price = mView.findViewById(R.id.price_text);

                    //  calculated_price.setText(diff.toString());


                }
            });

           /* paid_chk_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //   holder.getAdapterPosition();
                        //remove(mView.getTag());
                        Integer position = Integer.parseInt(mView.getTag().toString());
                        Log.d(TAG,"item to be removed:" +mView.getTag());
                        managementList.remove(position);
                        //notifyDataSetChanged(position);
                }
            });*/


        }

        public void setRegistrationNumber(String platesText) {
            plates_number_text = mView.findViewById(R.id.registration_plates_txt);
            plates_number_text.setText(platesText);


        }
/*        public void setCHKintime(Date koha_hyrjes)

        {


            check_in_btn = mView.findViewById(R.id.arrive_time_btn);
            check_in_btn.setText(koha_hyrjes.toString());
            check_in_btn.setEnabled(false);


        }*/
      /*  public void setPrice(Timestamp time1, Timestamp time2){
            String time= time1.toString();
            calculated_price=mView.findViewById(R.id.price_text);
            calculated_price.setText(time);*/


        //    }
        public void setArrivalTime(String arriveTime) {


            starting_time_text = mView.findViewById(R.id.expected_arrival);
            starting_time_text.setText(arriveTime);
        }

        public void setCheckIn(String kohaHyrjes) {
            check_in_btn = mView.findViewById(R.id.arrive_time_btn);
            String kohalokale = kohaHyrjes;
            Log.d(TAG, "setCheckIn: KOHA QE PO DON ME NDRRU" + kohalokale);
            String stringToCompare = "Sat Jan 01 01:00:00 GMT+01:00 2000";
            String str_date = "Fri, 07 Aug 2015 08:08:31 GMT";
            String outputPattern = "dd-MMM-yyyy hh:mm";
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date localTime = null;
            String str = null;
            if (!(kohaHyrjes.equals(stringToCompare))) {
                try {
                    localTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault()).parse(kohalokale);
                    Log.d(TAG, "setCheckIn: koha osht:" + localTime);
                    System.out.println("TimeStamp is " + localTime.getTime());
                    str = outputFormat.format(localTime);
                    Log.d(TAG, "setCheckIn: koha aktuale do te jete:" + str);
                    check_in_btn.setText(str);
                  //  check_in_btn.setBackgroundColor(Color.P);
                    check_in_btn.setTextColor(Color.WHITE);
                    check_in_btn.setTextSize(12);
                    check_in_btn.setEnabled(false);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }


            }
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

        public void checkInManagement() {

            final String document = managementList.get(getAdapterPosition()).ManagementPostId;

            String testtest = "WV49nNKf17fLWEKvcbSe";
            Map<String, Object> checkInMap = new HashMap<>();
            checkInMap.put("checkin_time", FieldValue.serverTimestamp());
            final String TAG = "MyActivity";


            firebaseFirestore.collection("Reservations").document(document).update(checkInMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.w(TAG, "Failure:" + e.getMessage());


                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Successfully updated!");
                }
            });
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
            currentLocalTime0 = cal.getTime();
            DateFormat date = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
// you can get seconds by adding  "...:ss" to it
            date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

            String localTime = date.format(currentLocalTime0);


            check_in_btn.setText(localTime);
            check_in_btn.setEnabled(false);
            check_in_btn.setBackgroundColor(Color.TRANSPARENT);
            check_in_btn.setTextColor(Color.BLACK);
            //    check_in_btn.setBackground(view.setBackground(R.drawable.textview_border););


            saveState = 1;


        }
        public void increaseEarnings(){
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
                            Double price = Double.parseDouble(calculated_price.getText().toString());
                            double newprice = snapshot.getDouble("earnings") + price;
                            transaction.update(docUpdateReference, "earnings", newprice);


                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Earning is bigger!:");


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
        public void increaseFreePlaces(){


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

                            double newFreeSpaces = snapshot.getDouble("current_free_places") + 1;
                            transaction.update(docUpdateReference, "current_free_places", newFreeSpaces);


                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "The number of free places is increased by 1!");


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
        public void handleFailedReservation(){
            final String document = managementList.get(getAdapterPosition()).ManagementPostId;
            String str_date="Sat, 01 Jan 2001 00:00:00 GMT ";
            Date localTime = null;

            //   try {
            try {

                localTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).parse(str_date);
                Log.d(TAG, "onEvent: koha lokale:   "+localTime   );
                System.out.println("TimeStamp is " +localTime.getTime());
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("paid", true);
            paymentMap.put("price",0);
            paymentMap.put("checkin_time",localTime);
            paymentMap.put("checkout_time",localTime);
           // paymentMap.put("fa")

            firebaseFirestore.collection("Reservations/").document(document).update(paymentMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.w(TAG, "Failure:" + e.getMessage());

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Successfully updated document: " + document);

                }
            });


        }

        public void handleCheckOut()
        {
            final String document = managementList.get(getAdapterPosition()).ManagementPostId;


            firebaseFirestore = FirebaseFirestore.getInstance();
            Double price = Double.parseDouble(calculated_price.getText().toString());

            managementList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("paid", true);
            paymentMap.put("price",price);
            final String TAG = "MyActivity";
            String testtest = "WV49nNKf17fLWEKvcbSe";

                    /*firebaseFirestore.collectionGroup("Reservations").whereEqualTo(FieldPath.documentId(),document).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        }
                    });
                    final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings//Reservations").document(document);
                    firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(docUpdateReference);
                            //double testnr = snapshot.getDouble("number_of_reservations")+1;
                            transaction.update(docUpdateReference, "paid", true);
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully updated document: "+document);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error: "+e.getMessage());
                        }
                    });*/


            firebaseFirestore.collection("Reservations/").document(document).update(paymentMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.w(TAG, "Failure:" + e.getMessage());

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Successfully updated document: " + document);

                }
            });



        }

    }
}
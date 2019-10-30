package com.example.parko;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.Transaction;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ManagementRecyclerAdapter extends RecyclerView.Adapter<ManagementRecyclerAdapter.ViewHolder> {

    public List<ManagementPost> managementList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public ManagementRecyclerAdapter(List<ManagementPost> managementList){
        this.managementList = managementList;

    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reservation_item, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mView.setTag(position);

        String managementPostId = managementList.get(position).ManagementPostId;

        String plates_data = managementList.get(position).getRegistration_plates();
        String arrive_time_data = managementList.get(position).getReservation_time();
        Timestamp check_in_time = managementList.get(position).getCheck_in_time();
        Timestamp check_out_time = managementList.get(position).getCheck_out_time();

        holder.setRegistrationNumber(plates_data);
        holder.getAdapterPosition();

        Log.w(TAG,"ID CURRDORC:"+managementPostId);
     //   holder.setPrice(check_in_time, check_out_time);
     //   holder.setArrivalTime(arrive_time_data);

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
        private  Date currentLocalTime0;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;

            check_in_btn = mView.findViewById(R.id.arrive_time_btn);
            check_out_btn = mView.findViewById(R.id.departure_time_btn);
            paid_chk_box =  mView.findViewById(R.id.payment_chk_box);




            firebaseFirestore = FirebaseFirestore.getInstance();

            paid_chk_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String document = managementList.get(getAdapterPosition()).ManagementPostId;


                    firebaseFirestore = FirebaseFirestore.getInstance();

                    managementList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    Map<String, Object> paymentMap = new HashMap<>();
                    paymentMap.put("paid", true);
                    final String TAG = "MyActivity";
                    String testtest= "WV49nNKf17fLWEKvcbSe";

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

                            Log.w(TAG, "Failure:"+e.getMessage());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully updated document: "+document);

                        }
                    });

                    AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Selected reservation will be archived");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();




                }
            });


            check_in_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String document = managementList.get(getAdapterPosition()).ManagementPostId;

                    String testtest= "WV49nNKf17fLWEKvcbSe";
                    Map<String, Object> checkInMap = new HashMap<>();
                    checkInMap.put("checkin_time", FieldValue.serverTimestamp());
                    final String TAG = "MyActivity";




                    firebaseFirestore.collection("Reservations").document(document).update(checkInMap).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w(TAG, "Failure:"+e.getMessage());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully updated!");
                        }
                    });
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                    currentLocalTime0 = cal.getTime();
                    DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
                    date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

                    String localTime = date.format(currentLocalTime0);


                    check_in_btn.setText(localTime);
                    check_in_btn.setEnabled(false);




                }
            });

//            final String document = managementList.get(getAdapterPosition()).ManagementPostId;




        //    final DocumentReference collref0 = firebaseFirestore.collection("Reservations").document(document);
       //     final Query collref1 = firebaseFirestore.collection("Parkings").whereArrayContains(FieldPath.documentId(),document).whereEqualTo("ownerid", FirebaseAuth.getInstance().getUid());

          //  final DocumentReference docRef = collref.getParent();
           // final CollectionG




     //       final DocumentReference docParentTest= firebaseFirestore.collection("Parkings/documentid/Reservations").document("gfACn7d2BrVsdrHYeUuq");





            //docParentTest.getParent();

          //  Log.w(TAG, "Parent Id:"+docParentTest.getParent().getParent().collection("Parkings").getId());
         /*   Log.w(TAG, "Parent Id:"+docParentTest.getParent().getParent().getId());*/
         //   Log.w(TAG, "collection Id:"+collref.getId());
          //  Log.w(TAG, "doc ref Id:"+docRef);





          /*  firebaseFirestore.collectionGroup("Bookings").whereEqualTo(FieldPath.documentId(), "gfACn7d2BrVsdrHYeUuq").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                task.getResult().getPare
                            }
                        }
                    });
*/


            check_out_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String document = managementList.get(getAdapterPosition()).ManagementPostId;

                    String testtest= "WV49nNKf17fLWEKvcbSe";
                    Map<String, Object> checkOutMap = new HashMap<>();
                    checkOutMap.put("checkout_time", FieldValue.serverTimestamp());
                    final String TAG = "MyActivity";


                    firebaseFirestore.collection("Reservations").document(document).update(checkOutMap).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w(TAG, "Failure:"+e.getMessage());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully updated!");
                        }
                    });
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                    Date currentLocalTime = cal.getTime();
                    DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
                    date.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));

                    String localTime = date.format(currentLocalTime);


                    check_out_btn.setText(localTime);



                    String check_in_time = check_in_btn.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
                    try {
                        Date time1 = format.parse(currentLocalTime0.toString());
                        Log.d(TAG, "Koha e arritjes: "+currentLocalTime0+"\nKoha e largimit: "+currentLocalTime);
                        long difference = (currentLocalTime.getTime()-time1.getTime())/1000;
                        Log.d(TAG, "diferenca:"+difference+"\nNumri i dokumentit jasht koleksionit: "+id_parking);
                        Integer days = (int) (difference / (1000*60*60*24));
                        final Integer hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                        Integer min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);



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
                                                            
                                                            if(hours>=0 && hours<=2){

                                                                Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price2hrs"));
                                                                calculated_price.setText(document.getDouble("price2hrs").toString());
                                                            }
                                                            else if (hours>2 && hours<=4){
                                                                Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price4hrs"));
                                                                calculated_price.setText(document.getDouble("price4hrs").toString());
                                                                //Log.d(TAG, "onComplete: gabimgabimgaibm");
                                                            }
                                                            else if(hours>4 && hours<=8){
                                                                Log.d(TAG, "DocumentSnapshot data: Cmimi eshte" + document.getDouble("price8hrs"));
                                                                calculated_price.setText(document.getDouble("price8hrs").toString());
                                                            }
                                                            else if(hours>8){
                                                                Double cmimi8hr=document.getDouble("price8hrs");
                                                                Double cmimimt8hr=document.getDouble("pricemt8hrs");
                                                                Double totali = cmimi8hr + cmimimt8hr*hours;
                                                                calculated_price.setText(totali.toString());

                                                            }
                                                            else {
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
                        Log.d(TAG, "Numri i dokumentit jasht koleksionit: "+document);

                        // }

                        Log.d(TAG, "Diferenca e shprehur ne sekonda:"+difference+ "\nDite:"+days+"\nOre:"+hours+"\nMinuta:"+min);
                    } catch (Exception e) {
                        Log.w(TAG, "Failure:"+e.getMessage());
                    }







                    calculated_price=mView.findViewById(R.id.price_text);

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
        public void setRegistrationNumber(String platesText){
            plates_number_text = mView.findViewById(R.id.registration_plates_txt);
            plates_number_text.setText(platesText);


        }

      /*  public void setPrice(Timestamp time1, Timestamp time2){
            String time= time1.toString();
            calculated_price=mView.findViewById(R.id.price_text);
            calculated_price.setText(time);*/




    //    }
       /* public void setArrivalTime(String arriveTime){
            starting_time_text = mView.findViewById(R.id.expected_arrival);
            starting_time_text.setText(arriveTime);
        }*/
    }
}

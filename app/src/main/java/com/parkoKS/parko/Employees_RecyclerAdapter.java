package com.parkoKS.parko;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Employees_RecyclerAdapter extends RecyclerView.Adapter<Employees_RecyclerAdapter.Viewholder> {
    public List<EmployeePost> employeePosts;
    public Context context;

    public String userID;





    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
   // private Button addEmpButton;

    public Employees_RecyclerAdapter(List<EmployeePost> employeePosts){

        this.employeePosts = employeePosts;
}
    @NonNull
    @Override
    public Employees_RecyclerAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_employee_item, parent, false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
mAuth = FirebaseAuth.getInstance();
      //  addEmpButton =


        return new Employees_RecyclerAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Employees_RecyclerAdapter.Viewholder holder, int position) {

        String empl_user_name = employeePosts.get(position).getUser_name();


        String profile_owner_image_url= employeePosts.get(position).getProfile_image_URI();

        final String user_id= employeePosts.get(position).getUser_id();
        holder.setProfileImage(profile_owner_image_url);

        holder.getAdapterPosition();

      //  String user_id= employeePosts.get(position).get();
        //final String user_id;
        //String user_id= employeePosts.get(position)


        // firebaseFirestore.collection("Users").whereEqualTo("")

   //     firebaseFirestore.collection("Users").document().get().addS

/*firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
            if(doc.getType() == DocumentChange.Type.ADDED){

                String userName = doc.getDocument().getString("user_name");

                String userImage = doc.getDocument().getString("user_profile_image");
                String documentId = doc.getDocument().getId();

               // String userid = task.getResult().getId();

                Log.d(TAG, "onComplete: emri userit:"+userName+", ku id eshte:");

                holder.setOwnerData(userName, userImage);

                Integer docNr = queryDocumentSnapshots.size();

                String blla = docNr.toString();


            }
            else {

                Log.d(TAG, "onComplete: GABIM GJATE PROCESIMIT TE TE DHENAVE!");

            }

        }
    }
});*/

       firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("user_name");



                    String userImage = task.getResult().getString("user_profile_image");
                    String userid = task.getResult().getId();
//                    String punonNe= task.getResult().getString("works_at");

                    userID = user_id;

                    Log.d(TAG, "onComplete: emri userit"+userName+",ku user id eshteL:"+userid);
  //                  Log.d(TAG, "onComplete: parkimgu i zgkedhur"+ParkingsGridRecyclerAdapter.stringtoPass+"\npunon ne: "+punonNe);


                    holder.setOwnerData(userName, userImage);

                }else {

                    Log.d(TAG, "onComplete: GABIM GJATE PROCESIMIT TE TE DHENAVE!");

                }
            }
        });








    }
    @Override
    public int getItemCount() {
        return employeePosts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private View mView;


        private TextView employee_name;
        private CircleImageView empl_image;



        private Button add_emp_btn;
        private Button delete_emp_btn;



        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            add_emp_btn = mView.findViewById(R.id.add_empl_btn);
            delete_emp_btn = mView.findViewById(R.id.delete_empl_btn);



            add_emp_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Parkingu i klikuar"+ParkingsGridRecyclerAdapter.stringtoPass);

                    final String document = employeePosts.get(getAdapterPosition()).EmployeeId;



                    firebaseFirestore = FirebaseFirestore.getInstance();



                    Log.d(TAG, "onClick: ITEMI I KLIKAUR:-->>>"+document);


firebaseFirestore.collection("Users/").document(document).get().addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Failure:"+e.getMessage());
    }
}).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        String works_at=documentSnapshot.getString("works_at");
        Log.d(TAG, "onSuccess Punetori punobn: "+documentSnapshot.getString("works_at")); ;

        if (works_at.equals(ParkingsGridRecyclerAdapter.stringtoPass)){
            Toast.makeText(context, "PUNETORI ESHTE I REGJISTRUAR NE PARKINGUN TUAJ",Toast.LENGTH_LONG).show();
            AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
            alertDialog.setTitle("Informacion");
            alertDialog.setMessage("Punetori eshte i regjistruar ne parkingun tuaj!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
           // add_emp_btn.setText("EKZISTON");
            add_emp_btn.setBackgroundColor(Color.RED);

           // add_emp_btn.setEnabled(false);
        }
        else if(!works_at.equals("0")){
            AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
            alertDialog.setTitle("Informacion");
            alertDialog.setMessage("Nuk mund ta regjistroni.Punetori eshte i regjistruar ne nje parking tjeter!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else{
           // if (works_at.equals(0)) {
                Map<String, Object> paymentMap = new HashMap<>();
                paymentMap.put("works_at", ParkingsGridRecyclerAdapter.stringtoPass);
                paymentMap.put("fired",false);
                firebaseFirestore.collection("Users/").document(document).update(paymentMap).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Failure:" + e.getMessage());

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully updated document: " + document);
                   //     add_emp_btn.setEnabled(false);
                        AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                        alertDialog.setTitle("Informacion");
                        alertDialog.setMessage("Punetori u regjistrua me sukses!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }
                });

            //}
     /*       else {
                AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                alertDialog.setTitle("Informacion");
                alertDialog.setMessage("Nuk mund te regjistrohet punetori sepse eshte i regjistruar si punetor ne nje parking tjeter.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }*/

        }
    }
});

                    //    Log.d(TAG, "onClick: USERI I KLIKUAR: "+employee_name);
                }
            });
delete_emp_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final String document = employeePosts.get(getAdapterPosition()).EmployeeId;
        firebaseFirestore.collection("Users").document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String works_at= task.getResult().getString("works_at");
                    Log.d(TAG, "onComplete.Delete Emp btn.User works at: "+works_at);
                    String parkingu = ParkingsGridRecyclerAdapter.stringtoPass;
                    Log.d(TAG, "onComplete: Delete Emp btn.ParkingsGridRecyclerAdapter.stringtoPass: "+parkingu );

                    if (works_at.equals(parkingu)){
                        AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                        alertDialog.setTitle("Informacion");
                        alertDialog.setMessage("A jeni te sigurt qe doni ta fshini punetorin nga parkingu aktual.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map<String, Object> deleteMap = new HashMap<>();
                                        deleteMap.put("works_at", "0");
                                        deleteMap.put("fired",true);
                                        firebaseFirestore.collection("Users").document(document).update(deleteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                                                    alertDialog.setTitle("Informacion");
                                                    alertDialog.setMessage("Punetori u fshi nga parkingu.");
                                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    alertDialog.show();
                                                }
                                                else{
                                                    AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                                                    alertDialog.setTitle("Informacion");
                                                    alertDialog.setMessage("Nuk mundi te fshihet punetori.");
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
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();


                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(mView.getRootView().getContext()).create();
                        alertDialog.setTitle("Informacion");
                        alertDialog.setMessage("Punetori nuk mund te fshihet sepse nuk punon ne kete parking.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else {
                    Log.d(TAG, "onComplete: Delete Emp btn.Error: "+task.getException());
                }
            }
        });
    }
});


/*

            add_emp_btn = mView.findViewById(R.id.add_employees_btn);
            add_emp_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Blla blla");
                }
            });

*/




        }

        public void setProfileImage(String downloadURI){

            empl_image = mView.findViewById(R.id.emp_img);
            Glide.with(context).load(downloadURI).into(empl_image);



        }

        public void setOwnerData(String name, String image){
            empl_image = mView.findViewById(R.id.emp_img);
            employee_name = mView.findViewById(R.id.empl_name);


            employee_name.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.defaultimage);


            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(empl_image);
        }




    }}

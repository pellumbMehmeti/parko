package com.example.parko;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
                    String punonNe= task.getResult().getString("works_at");

                    userID = user_id;

                    Log.d(TAG, "onComplete: emri userit"+userName+",ku user id eshteL:"+userid);
                    Log.d(TAG, "onComplete: parkimgu i zgkedhur"+ParkingsGridRecyclerAdapter.stringtoPass+"\npunon ne: "+punonNe);


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
        Log.d(TAG, "onSuccess: "+documentSnapshot.getString("works_at")); ;

        if (works_at.equals(ParkingsGridRecyclerAdapter.stringtoPass)){
            Toast.makeText(context, "PUNETORI ESHTE I REGJISTRUAR NE PARKINGUN TUAJ",Toast.LENGTH_LONG).show();
            add_emp_btn.setText("EKZISTON");
            add_emp_btn.setBackgroundColor(Color.CYAN);
            add_emp_btn.setEnabled(false);
        }
        else{
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("works_at", ParkingsGridRecyclerAdapter.stringtoPass);
            firebaseFirestore.collection("Users/").document(document).update(paymentMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.w(TAG, "Failure:"+e.getMessage());

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Successfully updated document: "+document);
                    add_emp_btn.setEnabled(false);

                }
            });


        }
    }
});

                    //    Log.d(TAG, "onClick: USERI I KLIKUAR: "+employee_name);
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

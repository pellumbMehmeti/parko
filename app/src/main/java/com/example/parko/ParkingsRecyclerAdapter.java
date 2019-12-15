package com.example.parko;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ParkingsRecyclerAdapter extends RecyclerView.Adapter<ParkingsRecyclerAdapter.Viewholder> {

    public List<ParkingPost> parkingsList;
    public Context context;
    public static String passingOwner;
    private FirebaseFirestore firebaseFirestore;

    public ParkingsRecyclerAdapter(List<ParkingPost> parkingsList){

        this.parkingsList = parkingsList;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_list_item, parent, false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        String parkingPostId = parkingsList.get(position).ParkingPostId;
        Integer total_parking_places = parkingsList.get(position).getCapacity_number();
        String parking_location_text = parkingsList.get(position).getLocation_info();

        String profile_owner_image_url= parkingsList.get(position).getProfile_image_URI();
        String parking_image = parkingsList.get(position).getParking_image_URI();

        Double nrFreePlaces = parkingsList.get(position).getCurrent_free_places();

        String parkingName = parkingsList.get(position).getParking_name();


        String user_id= parkingsList.get(position).getOwner_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("user_name");
                    String userImage = task.getResult().getString("user_profile_image");

                    holder.setOwnerData(userImage);
                    //holder.setProfileImage(userImage);
                }else {

                }
            }
        });
/*        firebaseFirestore.collection("Parkings").document(parkingPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String parking_photo = task.getResult().getString("parking_image_URI");
                    Log.d(TAG, "onComplete: uri e downloduar:\t"+parking_photo);
                   // Glide.with(context).load(parking_photo).into(R.id.parking_photo);
                    holder.setProfileImage(parking_photo);
                }
            }
        });*/

       /* firebaseFirestore.collection("Parkings").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Double freeplaces = task.getResult().getDouble("free_places");

                    holder.setFreePlaces(freeplaces);


                }

            }
        });*/





        holder.setParkAddress(parking_location_text);
        holder.setFreePlaces(total_parking_places);
      holder.setProfileImage(parking_image);
      holder.setCurrentFreePlaces(nrFreePlaces);
      holder.setParkingName(parkingName);

    }

    @Override
    public int getItemCount() {
        return parkingsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView parking_address_view;//mi kqyr prap!!!!!!!!!!!
        private ImageView parking_owner_img;
        private TextView freeplaces_home_nr;

        private TextView parking_owner_name;
        private CircleImageView parking_owner_image;
        private TextView parking_total_places;


        private Button booking_details_button;



        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            parking_total_places = mView.findViewById(R.id.book_parking_btn);
            freeplaces_home_nr = mView.findViewById(R.id.home_free_places);

            parking_total_places.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

               //     firebaseFirestore.collection("P")

                   String curdocid = parkingsList.get(getAdapterPosition()).ParkingPostId;

                    firebaseFirestore.collection("Parkings").document(curdocid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String stringtoPass = parkingsList.get(getAdapterPosition()).ParkingPostId;
                            Log.d(TAG, "onClick: vlera e dergiar esjte :"+stringtoPass);
                            passingOwner = documentSnapshot.getString("owner_id");
                            Log.d(TAG, "onClick: passing owner:"+passingOwner);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("parking_id", stringtoPass);
                            bundle.putSerializable("id_pronarit",passingOwner);

                            BookingFragment bookingFragment = new BookingFragment();
                            bookingFragment.setArguments(bundle);
                            FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.main_container, bookingFragment, bookingFragment.getTag()).commit();
                        }
                    });

                 }
                 /*   FragmentManager fragmentManager = new */
                  //  Intent bookingIntent= new Intent(context, BookingFragment.class);
                  //  bookingIntent.putExtra("parking_id", parkingsList.get(getAdapterPosition()).ParkingPostId);

                  /*  Bundle bundle = new Bundle();
                    bundle.putSerializable("parking_id", getAdapterPosition());
                    bookingIntent.putExtras(bundle);*/
                //    context.startActivity(bookingIntent);



            });



        }
        public void setParkAddress(String addressText){
            parking_address_view = mView.findViewById(R.id.parking_address);
            parking_address_view.setText(addressText);
        }

        public void setProfileImage(String downloadURI){

            parking_owner_img = mView.findViewById(R.id.parking_photo);
            try {
                Glide.with(context).load(downloadURI).into(parking_owner_img);

                Log.d(TAG, "setProfileImage: fotooo:\t"+downloadURI);

            }
           catch (Exception e){
               Log.d(TAG, "setProfileImage: asfokafo"+e.getMessage());
           }


        }
        public void setCurrentFreePlaces(Double nrfreePlaces){
            freeplaces_home_nr = mView.findViewById(R.id.home_free_places);
            freeplaces_home_nr.setText(nrfreePlaces.toString());

        }

        public void setOwnerData(String image){
            parking_owner_image = mView.findViewById(R.id.parking_owner_image);
           // parking_owner_name = mView.findViewById(R.id.parking_name);

            Log.d(TAG, "setOwnerData: foto te setowner data:"+image);


        //    parking_owner_name.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.defaultimage);


            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(parking_owner_image);
        }
        public void setParkingName(String parkname){
            parking_owner_name = mView.findViewById(R.id.parking_name);
            parking_owner_name.setText(parkname);
        }
        public void setFreePlaces(Integer total){
            parking_total_places = mView.findViewById(R.id.parking_total_places);

            String totalString = Double.toString(total);


           parking_total_places.setText(totalString);


        }



    }
}

package com.example.parko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParkingsRecyclerAdapter extends RecyclerView.Adapter<ParkingsRecyclerAdapter.Viewholder> {

    public List<ParkingPost> parkingsList;
    public Context context;

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
        holder.setProfileImage(profile_owner_image_url);

        String user_id= parkingsList.get(position).getOwner_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("user_name");
                    String userImage = task.getResult().getString("user_profile_image");

                    holder.setOwnerData(userName, userImage);
                }else {

                }
            }
        });

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

    }

    @Override
    public int getItemCount() {
        return parkingsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView parking_address_view;//mi kqyr prap!!!!!!!!!!!
        private ImageView parking_owner_img;

        private TextView parking_owner_name;
        private CircleImageView parking_owner_image;
        private TextView parking_total_places;


        private Button booking_details_button;



        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            parking_total_places = mView.findViewById(R.id.book_parking_btn);

            parking_total_places.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent bookingIntent= new Intent(context, BookingActivity.class);
                    bookingIntent.putExtra("parking_id", parkingsList.get(getAdapterPosition()).ParkingPostId);

                  /*  Bundle bundle = new Bundle();
                    bundle.putSerializable("parking_id", getAdapterPosition());
                    bookingIntent.putExtras(bundle);*/
                    context.startActivity(bookingIntent);


                }
            });



        }
        public void setParkAddress(String addressText){
            parking_address_view = mView.findViewById(R.id.parking_address);
            parking_address_view.setText(addressText);
        }

        public void setProfileImage(String downloadURI){

            parking_owner_img = mView.findViewById(R.id.parking_photo);
            Glide.with(context).load(downloadURI).into(parking_owner_img);



        }

        public void setOwnerData(String name, String image){
            parking_owner_image = mView.findViewById(R.id.parking_owner_image);
            parking_owner_name = mView.findViewById(R.id.parking_name);


            parking_owner_name.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.defaultimage);


            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(parking_owner_image);
        }
        public void setFreePlaces(Integer total){
            parking_total_places = mView.findViewById(R.id.parking_total_places);

            String totalString = Double.toString(total);


           parking_total_places.setText(totalString);


        }


    }
}

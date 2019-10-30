package com.example.parko;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class ParkingsGridRecyclerAdapter extends RecyclerView.Adapter<ParkingsGridRecyclerAdapter.Viewholder>  {

    public List<ParkingGridPost> parkingsListGrid;
    public Context context;

    public static String stringtoPass;
    public static Integer stringuI;

    private FirebaseFirestore firebaseFirestore;
    private OnParkingListener mOnParkingListener;
    private int whichActivity;

    private RecyclerView parkingslist;




    public ParkingsGridRecyclerAdapter(List<ParkingGridPost> parkingsListGrid, OnParkingListener onParkingListener, int activity){



        this.mOnParkingListener = onParkingListener;
        whichActivity = activity;
        this.parkingsListGrid = parkingsListGrid;
    }

    @NonNull
    @Override
    public ParkingsGridRecyclerAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.single_parking_grid_item, parent, false);

        context = parent.getContext();

       // parkingslist = view.findViewById(R.id.park_list);

        firebaseFirestore = FirebaseFirestore.getInstance();



        return new ParkingsGridRecyclerAdapter.Viewholder(view,mOnParkingListener);

      /*  return new OnParkingListener(mOnParkingListener);*/
      }

    @Override
    public void onBindViewHolder(@NonNull final ParkingsGridRecyclerAdapter.Viewholder holder, int position) {


        Log.d(TAG, "onBindViewHolder: which activity???"+whichActivity);
        String parkingGridPostId = parkingsListGrid.get(position).ParkingGridPostId;
        Integer total_parking_places = parkingsListGrid.get(position).getCapacity_number();
        String parking_location_text = parkingsListGrid.get(position).getLocation_info();



        String profile_owner_image_url= parkingsListGrid.get(position).getProfile_image_URI();


        String user_id= parkingsListGrid.get(position).getOwner_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("user_name");


                    holder.setOwnerData(userName);
                }else {

                }
            }
        });









        holder.setParkAddress(parking_location_text);
        holder.setFreePlaces(total_parking_places);


    }

    @Override
    public int getItemCount() {
        return parkingsListGrid.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnParkingListener onParkingListener;

        private View mView;
        private TextView parking_address_view;//mi kqyr prap!!!!!!!!!!!
        private ImageView parking_owner_img;



        private TextView parking_owner_name;
        private CircleImageView parking_owner_image;
        private TextView parking_total_places;


        private Button booking_details_button;



        public Viewholder(@NonNull View itemView, final OnParkingListener mOnParkingListener) {
            super(itemView);
            mView=itemView;

            mView.setOnClickListener(this);
          //  itemView.setOnClickListener(this);



            this.onParkingListener= onParkingListener;

     /*       itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    stringtoPass =   parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
                    Log.d(TAG, "onClick: Stringu qe po pasohet eshte :"+stringtoPass);
                //    onParkingListener.
                  //  mOnParkingListener.onParkingClick(parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId);


                   // itemView.setOnClickListener(this);

                }
            });*/



        }
        public void setParkAddress(String addressText){
            parking_address_view = mView.findViewById(R.id.street_adr);
            parking_address_view.setText(addressText);
        }



        public void setOwnerData(String name){

            parking_owner_name = mView.findViewById(R.id.parking_grid_name);


            parking_owner_name.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
           // placeholderOption.placeholder(R.drawable.defaultimage);


           // Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(parking_owner_image);
        }
        public void setFreePlaces(Integer total){
            parking_total_places = mView.findViewById(R.id.nr_places_grid);

            String totalString = Double.toString(total);


            parking_total_places.setText(totalString);


        }


        @Override
        public  void onClick(View v) {
            Log.d(TAG, "onClick: Item i klikuar:"+getAdapterPosition()+",\n item id:"+     parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId);
            stringuI = (this.getAdapterPosition());
            stringtoPass = parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;

            if (whichActivity == 1) {

                Intent intent = new Intent(context,employees_select.class);
                intent.putExtra("parking_id",stringtoPass);
                // context.startActivity(intent);
                v.getContext().startActivity(intent);

            }

        else{
            Bundle bundle = new Bundle();
            bundle.putString("parking_id",stringtoPass);

              NotificationFragment notificationFragment = new NotificationFragment();
                 notificationFragment.setArguments(bundle);
                 FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                 fragmentManager.beginTransaction().replace(R.id.main_container, notificationFragment, notificationFragment.getTag()).commit();}

                 /*     Fragment fragment = new Fragment();
            FragmentManager fragmentManager =
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();*/

/*
       FragmentTransaction ft= getFragmentManager
*/

/*
            Fragment fragment = new Fragment();
*/
                  //  mView.setVisibility(View.INVISIBLE);

               //  onParkingListener.onParkingClick(getItemCount());
        }
    }
    public interface OnParkingListener{
        void onParkingClick(int position);


    }
}

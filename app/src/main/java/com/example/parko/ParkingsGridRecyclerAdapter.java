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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class ParkingsGridRecyclerAdapter extends RecyclerView.Adapter<ParkingsGridRecyclerAdapter.Viewholder> {

    public List<ParkingGridPost> parkingsListGrid;
    public Context context;

    public static String stringtoPass;
    public static Integer stringuI;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private OnParkingListener mOnParkingListener;
    private int whichActivity;

    private int upd_parking = 0;
    private int mngemps = 0;

    private RecyclerView parkingslist;


    public ParkingsGridRecyclerAdapter(List<ParkingGridPost> parkingsListGrid, OnParkingListener onParkingListener, int activity) {


        this.mOnParkingListener = onParkingListener;
        whichActivity = activity;
        this.parkingsListGrid = parkingsListGrid;
    }

    @NonNull
    @Override
    public ParkingsGridRecyclerAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_parking_grid_item, parent, false);

        context = parent.getContext();

        // parkingslist = view.findViewById(R.id.park_list);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();


        return new ParkingsGridRecyclerAdapter.Viewholder(view, mOnParkingListener);

        /*  return new OnParkingListener(mOnParkingListener);*/
    }

    @Override
    public void onBindViewHolder(@NonNull final ParkingsGridRecyclerAdapter.Viewholder holder, int position) {

        upd_parking = 1;
        Log.d(TAG, "onBindViewHolder: which activity???" + whichActivity + "\nUPD PARKING:" + upd_parking);
        String parkingGridPostId = parkingsListGrid.get(position).ParkingGridPostId;
        Integer total_parking_places = parkingsListGrid.get(position).getCapacity_number();
        Integer current_free_places = parkingsListGrid.get(position).getCurrent_free_places();
        String parking_location_text = parkingsListGrid.get(position).getLocation_info();


        String profile_owner_image_url = parkingsListGrid.get(position).getProfile_image_URI();


        String user_id = parkingsListGrid.get(position).getOwner_id();

        holder.setOwnerData(parkingGridPostId);

      /*  firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userName = task.getResult().getString("user_name");


                    holder.setOwnerData(userName);
                } else {

                }
            }
        });*/


        holder.setParkAddress(parking_location_text);
        holder.setFreePlaces(total_parking_places,current_free_places);


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
        private TextView parking_total_places, paarking_capacity_number;
        private Button updateDataBtn;
        private Button manageReservations;
        private Button manageEmployees;


        private Button booking_details_button;


        public Viewholder(@NonNull View itemView, final OnParkingListener mOnParkingListener) {
            super(itemView);
            mView = itemView;

            mView.setOnClickListener(this);
            //  itemView.setOnClickListener(this);


            this.onParkingListener = onParkingListener;

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
            updateDataBtn = mView.findViewById(R.id.update_parking_btn);
        manageEmployees = mView.findViewById(R.id.btn_mg_empls);
            manageReservations = mView.findViewById(R.id.btn_mngs_res);
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String role = task.getResult().getString("user_type");
                if(role.equals("Parking Worker")||role.equals("Ordinary User")){
                    manageEmployees.setVisibility(View.INVISIBLE);
                    updateDataBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        manageEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringtoPass = parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
                //String prk_toGO=parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
              //  Intent intent = new Intent(context, employees_select.class);
                //intent.putExtra("parking_id", prk_toGO);
                // context.startActivity(intent);
                //v.getContext().startActivity(intent);*/
                String prk_toGO=parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
                Bundle bundle = new Bundle();
                bundle.putString("parking_id", prk_toGO);
                bundle.putString("role","notuser");

                UnemployedWorkersFragment ufr = new UnemployedWorkersFragment();
                ufr.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_container, ufr, ufr.getTag()).commit();
            }
        });

        manageReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prk_toGO=parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
                Bundle bundle = new Bundle();
                bundle.putString("parking_id", prk_toGO);
                bundle.putString("role","notuser");

                NotificationFragment notificationFragment = new NotificationFragment();
                notificationFragment.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_container, notificationFragment, notificationFragment.getTag()).commit();
            }
        });

            updateDataBtn = mView.findViewById(R.id.update_parking_btn);
            if (whichActivity == 1) {
                updateDataBtn.setVisibility(View.INVISIBLE);
            }

            updateDataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String prk_id_toUpdate=parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;
                    Bundle bundle = new Bundle();
                    bundle.putString("parking_id", prk_id_toUpdate);
                    UpdateParkingFragment updateParkingFragment = new UpdateParkingFragment();
                    updateParkingFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.main_container, updateParkingFragment, updateParkingFragment.getTag()).commit();
                    Log.d(TAG, "onClick: KLIK UPDATED");
                }
            });


        }

        public void setParkAddress(String addressText) {
            parking_address_view = mView.findViewById(R.id.street_adr);
            parking_address_view.setText(addressText);
        }


        public void setOwnerData(String id) {

            parking_owner_name = mView.findViewById(R.id.parking_grid_name);

            firebaseFirestore.collection("Parkings").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        String parking_name = task.getResult().getString("parking_name");
                        parking_owner_name.setText(parking_name);
                    }
                    else {
                        Log.d(TAG, "onComplete: Error querying for parking name.Error:\t"+task.getException());
                    }
                }
            });


            RequestOptions placeholderOption = new RequestOptions();
            // placeholderOption.placeholder(R.drawable.defaultimage);


            // Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(parking_owner_image);
        }

        public void setFreePlaces(Integer totalCapacity, Integer currentFree) {
            parking_total_places = mView.findViewById(R.id.nr_places_grid);
            paarking_capacity_number = mView.findViewById(R.id.total_places_new);

            String stringToShow = Double.toString(totalCapacity) + "nga" + Double.toString(currentFree);
          //  String totalString = Double.toString(total);


            parking_total_places.setText(currentFree.toString());
            paarking_capacity_number.setText(totalCapacity.toString());


        }


        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: Item i klikuar:" + getAdapterPosition() + ",\n item id:" + parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId);
            stringuI = (this.getAdapterPosition());
            stringtoPass = parkingsListGrid.get(getAdapterPosition()).ParkingGridPostId;

            if (whichActivity == 1) {

                Intent intent = new Intent(context, employees_select.class);
                intent.putExtra("parking_id", stringtoPass);
                // context.startActivity(intent);
                v.getContext().startActivity(intent);

            } else {

                Bundle bundle = new Bundle();
                bundle.putString("parking_id", stringtoPass);
                bundle.putString("role","notuser");

                    NotificationFragment notificationFragment = new NotificationFragment();
                    notificationFragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_container, notificationFragment, notificationFragment.getTag()).commit();

                }


            }



    }

    public interface OnParkingListener {
        void onParkingClick(int position);


    }
}

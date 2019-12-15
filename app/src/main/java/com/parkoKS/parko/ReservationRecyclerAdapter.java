package com.parkoKS.parko;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.parkoKS.parko.employees_select.TAG;

public class ReservationRecyclerAdapter extends RecyclerView.Adapter<ReservationRecyclerAdapter.ViewHolder> {
    public List<ReservationPost> reservationList;
    //public List<ReservationPost> reservationList1;

    public ReservationRecyclerAdapter(List<ReservationPost> reservationList) {
        this.reservationList = reservationList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String parkingID = reservationList.get(position).getParkingId();
        String dateParking = reservationList.get(position).getReservation_date();
        String carPlates = reservationList.get(position).getRegistration_plates();
        Double parkingPrice = reservationList.get(position).getPrice().doubleValue();

        Log.d(TAG, "onBindViewHolder: PARKING PRICE  type IS: "+reservationList.get(position).getPrice().doubleValue());
      //  String stringPrice = parkingPrice.toString();

//reservationList.clear();

        if(parkingPrice.equals(null)){
            holder.setDate(dateParking);
            holder.setParkingName(parkingID);
            holder.setRegistrationPlates(carPlates);


        }
else { holder.setDate(dateParking);
            holder.setParkingName(parkingID);

            holder.setPrice(parkingPrice);
            holder.setRegistrationPlates(carPlates);
        }


    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private FirebaseFirestore firebaseFirestore;
        private TextView parkingName_text;
        private TextView reservationDate_text;
        private TextView price_text;
        private TextView registrationPlates_text;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        public void setParkingName(String parkingId) {
            parkingName_text = mView.findViewById(R.id.park_name_row);
            firebaseFirestore.collection("Parkings").document(parkingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String parkingName = task.getResult().getString("parking_name");
                        parkingName_text.setText(parkingName);
                    } else {
                        parkingName_text.setText("NUK U GJET");
                    }
                }
            });
        }

        public void setPrice(Double priceString ) {
            price_text = mView.findViewById(R.id.parking_price_row);
            price_text.setText(priceString.toString());

        }

        public void setDate(String date) {
            reservationDate_text = mView.findViewById(R.id.park_date_row);
            reservationDate_text.setText(date);
        }

        public void setRegistrationPlates(String regPlates) {
            registrationPlates_text = mView.findViewById(R.id.registration_plates_row);
            registrationPlates_text.setText(regPlates);
        }
        public void setPriceForNull(){
            price_text = mView.findViewById(R.id.parking_price_row);
            price_text.setText("--");
        }
    }
}

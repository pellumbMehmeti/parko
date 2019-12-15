package com.example.parko;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements ParkingsGridRecyclerAdapter.OnParkingListener {



    private RecyclerView parking_grid_list_view;
    private List<ParkingGridPost> parkingsListGrid;

    private FirebaseFirestore firebaseFirestore;
    private ParkingsGridRecyclerAdapter parkingsGridRecyclerAdapter;

    private GridLayoutManager gridLayoutManager ;

   // private FirebaseAuth fire




    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_account, container, false);
        setHasOptionsMenu(false);




        parkingsListGrid = new ArrayList<>();
        parking_grid_list_view = view.findViewById(R.id.parkings_grid_view);

        gridLayoutManager = new GridLayoutManager(getContext(),1);

        parkingsGridRecyclerAdapter = new ParkingsGridRecyclerAdapter(parkingsListGrid, this,0);
        parking_grid_list_view.setLayoutManager(gridLayoutManager);
        parking_grid_list_view.setAdapter(parkingsGridRecyclerAdapter);




        final String TAG = "MyActivity";

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String works_at = documentSnapshot.getString("works_at");
                String roli = documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: shfrytezuesi i tanishem punon ne:"+works_at+"| roli:" +roli);
                if ((roli.equals("Parking Worker"))){
                  firebaseFirestore.collection("Parkings").document(works_at).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                            String parking_id = documentSnapshot.getId();
                            ParkingGridPost parkingGridPost = documentSnapshot.toObject(ParkingGridPost.class).withId(parking_id);
                            parkingsListGrid.add(parkingGridPost);


                            parkingsGridRecyclerAdapter.notifyDataSetChanged();

                        }
                    });


                }else if(((roli.equals("Parking Owner")))){
                    firebaseFirestore.collection("Parkings").whereEqualTo("owner_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                                if(doc.getType() == DocumentChange.Type.ADDED){



                                    String parkingGridPostId = doc.getDocument().getId();

                                    ParkingGridPost parkingGridPost = doc.getDocument().toObject(ParkingGridPost.class).withId(parkingGridPostId);
                                    parkingsListGrid.add(parkingGridPost);


                                    parkingsGridRecyclerAdapter.notifyDataSetChanged();

                                }

                            }
                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
                public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: nuk mund te shfaqen te dhenat");
            }
        });

/*

        firebaseFirestore.collection("Parkings").whereEqualTo("owner_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                    if(doc.getType() == DocumentChange.Type.ADDED){



                        String parkingGridPostId = doc.getDocument().getId();

                        ParkingGridPost parkingGridPost = doc.getDocument().toObject(ParkingGridPost.class).withId(parkingGridPostId);
                        parkingsListGrid.add(parkingGridPost);


                        parkingsGridRecyclerAdapter.notifyDataSetChanged();

                    }

                }
            }
        });


*/


        return view;
    }

    @Override
    public void onParkingClick(int position) {



      //parking_grid_list_view.setVisibility(View.INVISIBLE);
       /* parkingsListGrid.get(position);
        Log.d(TAG, "onParkingClick: GOODF"+position);
        Toast.makeText(getContext(),"testest",Toast.LENGTH_LONG).show();*/

    }
}

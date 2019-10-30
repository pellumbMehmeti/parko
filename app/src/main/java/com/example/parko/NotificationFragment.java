package com.example.parko;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    //test
    private RecyclerView manage_reservations_view;
    private List<ManagementPost> managementList;


    private ManagementRecyclerAdapter managementRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;



    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        //e merr vleren e parkingid

        Bundle bundle = getArguments();

        String parkingGridId = bundle.getString("parking_id");


        Log.d(TAG, "onCreateView.Vlera e marre nga ParkingsGrid eshte: "+parkingGridId);


        managementList = new ArrayList<>();
        manage_reservations_view = view.findViewById(R.id.reservations_list_view);
     //   manage_reservations_view.setHasFixedSize(false);

        managementRecyclerAdapter = new ManagementRecyclerAdapter(managementList);
        manage_reservations_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        manage_reservations_view.setAdapter(managementRecyclerAdapter);

        firebaseFirestore= FirebaseFirestore.getInstance();
        final String TAG = "MyActivity";

       /*firebaseFirestore.collection("Parkings").whereEqualTo("owner_id",FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());

                }
                else {
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){




                        if(doc.getType() == DocumentChange.Type.ADDED){*/
                            firebaseFirestore.collectionGroup("Reservations").whereEqualTo("parking_owner_id",FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("parkingId",parkingGridId).whereEqualTo("paid",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            if (e!=null){
                                                Log.d(TAG,"Error:"+e.getMessage());
                                            }
                                            else{
                                                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                                                    if(doc.getType() == DocumentChange.Type.ADDED){



                                                        Integer docNr = queryDocumentSnapshots.size();

                                                        String managementPostId = doc.getDocument().getId();
                                                        ManagementPost managementPost = doc.getDocument().toObject(ManagementPost.class).withId(managementPostId);

                                                        String blla = docNr.toString();

                                                        Log.w(TAG, "Numri i dokumenteve:" +blla);
                                                        Log.w(TAG, "Dokumentet e marra:" +queryDocumentSnapshots.getDocuments());


                                                        managementList.add(managementPost);

                                                        managementRecyclerAdapter.notifyDataSetChanged();



                                                    }

                                                }
                                            }}
                                    });




                          //  Integer docNr = queryDocumentSnapshots.size();


                          //  Log.w(TAG, "Numri i dokumenteve ku uid="+FirebaseAuth.getInstance().getCurrentUser().getUid()+" eshte :" +docNr);

                       // }

                 //   }
         //       }
         //  }
      //  });
     /*   firebaseFirestore.collectionGroup("Reservations")



                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());
                }
                else{
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){






                    if(doc.getType() == DocumentChange.Type.ADDED){


                        Integer docNr = queryDocumentSnapshots.size();

                        String managementPostId = doc.getDocument().getId();
                        ManagementPost managementPost = doc.getDocument().toObject(ManagementPost.class).withId(managementPostId);

                        String blla = docNr.toString();

                        Log.w(TAG, "Numri i dokumenteve:" +blla);
                        Log.w(TAG, "Dokumentet e marra:" +queryDocumentSnapshots.getDocuments());


                        managementList.add(managementPost);

                        managementRecyclerAdapter.notifyDataSetChanged();

                    }

                }
            }}
        });*/
        return view;
    }

}

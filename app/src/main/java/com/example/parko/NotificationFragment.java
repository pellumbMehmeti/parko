package com.example.parko;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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
    private FirebaseAuth firebaseAuth;

    private TextView tv11;
    private ImageButton imgbtn;
//private ConstraintLayout cl4;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        //e merr vleren e parkingid
//String role = geta

        Bundle bundle = getArguments();
        String role = bundle.getString("role");
/*
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Informacion");
        alertDialog.setMessage("ROLI JUAJ ESHTE: "+role);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); }
                });
        alertDialog.show();*/
       // bundle.putString("parking_id", stringtoPass);

//        Log.d(TAG, "onCreateView.Vlera e marre nga ParkingsGrid eshte: "+parkingGridId);


        managementList = new ArrayList<>();
        manage_reservations_view = view.findViewById(R.id.reservations_list_view);
     //   manage_reservations_view.setHasFixedSize(false);
        tv11 = view.findViewById(R.id.textView11);
        imgbtn = view.findViewById(R.id.infoBtn);
   //     cl4 = view.findViewById(R.id.constraintLayout4);

        managementRecyclerAdapter = new ManagementRecyclerAdapter(managementList);
        manage_reservations_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        manage_reservations_view.setAdapter(managementRecyclerAdapter);

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final String TAG = "MyActivity";

       // view.findViewById(R.id.textView11).setTex

       /*firebaseFirestore.collection("Parkings").whereEqualTo("owner_id",FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());

                }
                else {
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){




                        if(doc.getType() == DocumentChange.Type.ADDED){*/
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        //firebaseFirestore.setFirestoreSettings(settings);
       // firebaseFirestore.collection("Users").document(mA)

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String role = task.getResult().getString("user_type");
                    if(role.equals("Ordinary User")){
                        firebaseFirestore.collection("Reservations").whereEqualTo("reserved_by", firebaseAuth.getCurrentUser().getUid()).whereEqualTo("paid",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                            Log.d(TAG, "onEvent: SAVEDSTATE :"+ManagementRecyclerAdapter.saveState);
                                            managementList.add(managementPost);

                                            managementRecyclerAdapter.notifyDataSetChanged();



                                        }

                                    }
                                }}
                            });
                        }
                    else if(role.equals("Parking Worker")){
                        Boolean isFired = task.getResult().getBoolean("fired");
                        Log.d(TAG, "onComplete: WORKER IS FIRED?:"+isFired);
                        if(isFired.equals(false)){
                            Bundle bundle = getArguments();
                            //  String parkingid=  bundle.getString("parking_id");

                            final String parkingGridId = bundle.getString("parking_id");
                            Log.d(TAG, "onComplete: ID E PARKINGUT ESHTE : "+parkingGridId);
                            firebaseFirestore.collection("Reservations").whereEqualTo("parkingId",parkingGridId).whereEqualTo("paid",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                Log.d(TAG, "onEvent: SAVEDSTATE :"+ManagementRecyclerAdapter.saveState);
                                                managementList.add(managementPost);

                                                managementRecyclerAdapter.notifyDataSetChanged();



                                            }

                                        }
                                    }}
                            });
                        }
                        else if(isFired.equals(true)){
                            firebaseFirestore.collection("Reservations").whereEqualTo("reserved_by", firebaseAuth.getCurrentUser().getUid()).whereEqualTo("paid",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                Log.d(TAG, "onEvent: SAVEDSTATE :"+ManagementRecyclerAdapter.saveState);
                                                managementList.add(managementPost);

                                                managementRecyclerAdapter.notifyDataSetChanged();



                                            }

                                        }
                                    }}
                            });
                        }


                    }
                else {
               
                        Bundle bundle = getArguments();

                        final String parkingGridId = bundle.getString("parking_id");
                        Log.d(TAG, "onComplete: ID E PARKINGUT ESHTE : "+parkingGridId);
                        
                        firebaseFirestore.collection("Reservations").whereEqualTo("parking_owner_id",FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("parkingId",parkingGridId).whereEqualTo("paid",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                        Log.d(TAG, "onEvent: SAVEDSTATE :"+ManagementRecyclerAdapter.saveState);
                                        managementList.add(managementPost);

                                        managementRecyclerAdapter.notifyDataSetChanged();



                                    }

                                }
                            }}
                    });
                }
                }
                    }
                });
          //  };
       // })





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
     imgbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
             alertDialog.setTitle("Mënyra e përdorimit");
             alertDialog.setMessage("1. Kliko butonin HYRJA kur vetura vjen në parking, nëse rezervimi është bërë nga largësia.\n" +
                     "2. Kliko butonin DALJA kur makina me targën përkatëse don të dalë nga parkingu.\n" +
                     "3. Pasi bëhet kalkulimi i çmimit, kërko pagesën nga pronari i makinës.\n" +
                     "4. Pasi pronari i makinës kryen pagesën vendos shenjën ☑  në katrorin përkatës. \n" +
                     "5. Pas kësaj do të shfaqet një dialog ku duhet të klikoni mbi opsionin që ju përshtatet.");
             alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss(); }
                     });
             alertDialog.show();

         }
     });
     if ((role.equals("ordinaryUser") || (role.equals("workerFired")))){
         tv11.setText("LISTA E REZERVIMEVE TE ARDHSHME");
         //cl4.setVisibility(View.INVISIBLE);



      //   view.findViewById(R.id.textView12).setTex
     }

        return view;
    }


}

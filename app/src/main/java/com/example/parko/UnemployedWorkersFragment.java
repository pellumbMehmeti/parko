package com.example.parko;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnemployedWorkersFragment extends Fragment {
    private RecyclerView empl_list_view;
    private List<EmployeePost> employeePosts;

    private FirebaseFirestore firebaseFirestore;
    //private ParkingsRecyclerAdapter parkingsRecyclerAdapter;
    private Employees_RecyclerAdapter employees_recyclerAdapter;

    private FirebaseAuth mAuth;
    private String current_user_id;
    private Button updateDataBtn;

    public static String TAG="MyActivity";

    public UnemployedWorkersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        String parkingId = bundle.getString("parking_id");
        View view = inflater.inflate(R.layout.activity_employees_select, container, false);
        employeePosts = new ArrayList<>();
        empl_list_view = view.findViewById(R.id.list_of_employees);
        // updateDataBtn = findViewById(R.id.update_parking_btn);
//updateDataBtn.setVisibility(View.VISIBLE);

        mAuth=FirebaseAuth.getInstance();


        employees_recyclerAdapter = new Employees_RecyclerAdapter(employeePosts);
        empl_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        empl_list_view.setAdapter(employees_recyclerAdapter);

    //    Intent intent = view.getIntent();
     //   String id = intent.getStringExtra("parking_id");

        //Log.d(TAG, "onCreate: PARKINGU I ZGJEDHUR ESHTE"+id);

        Log.d(TAG, "onCreate: dokumenti"+mAuth.getCurrentUser().getUid());

        String curUser = mAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();



/*        Query query1 = firebaseFirestore.collection("Users").whereGreaterThan(FieldPath.documentId(), mAuth.getCurrentUser().getUid());
        final Query query2 = firebaseFirestore.collection("Users").whereLessThan(FieldPath.documentId(),mAuth.getCurrentUser().getUid());

        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {

                        String employeeId = document.getId();

                        EmployeePost employeePost = document.toObject(EmployeePost.class).withId(employeeId);
                        // EmployeePost employeePost1 = doc.getDocument().toObject(EmployeePost.class);
                        employeePosts.add(employeePost);

                        employees_recyclerAdapter.notifyDataSetChanged();
                    }
                    query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {

                                    String employeeId = document.getId();

                                    EmployeePost employeePost = document.toObject(EmployeePost.class).withId(employeeId);
                                    // EmployeePost employeePost1 = doc.getDocument().toObject(EmployeePost.class);
                                    employeePosts.add(employeePost);

                                    employees_recyclerAdapter.notifyDataSetChanged();
                                }

                            }

                        }
                    });

                }

            }
        });*/

        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                    if(doc.getType() == DocumentChange.Type.ADDED) {

                        String documentID = doc.getDocument().getId().toString();
                        String userType= doc.getDocument().getString("user_type");
                        String works_at=doc.getDocument().getString("works_at");
                        Log.d(TAG, "onEvent: USERI I DOCUMENTIT"+documentID);
                        Boolean userTypeTrueFalse = userType.equals("Parking Worker");
                        Boolean documentIdTrueFalse = !(documentID.equals(mAuth.getCurrentUser().getUid()));
//                        Boolean user_Unemployed = (works_at.equals(0));



                        if (userTypeTrueFalse && documentIdTrueFalse) {
                            Integer docNr = queryDocumentSnapshots.size();

                            String blla = docNr.toString();
                            // String parking_owner_id = doc.getDocument().getString("owner_id");


                            String employeeId = doc.getDocument().getId();
                            Log.d(TAG, "onEvent: emloyee ID: " + employeeId + ",NR DOKUMENTEVE:" + docNr);

                            EmployeePost employeePost = doc.getDocument().toObject(EmployeePost.class).withId(employeeId);
                            // EmployeePost employeePost1 = doc.getDocument().toObject(EmployeePost.class);
                            employeePosts.add(employeePost);

                            employees_recyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });


        return view;
    }

}

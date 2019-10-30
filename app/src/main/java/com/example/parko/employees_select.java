package com.example.parko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class employees_select extends AppCompatActivity {

    private RecyclerView empl_list_view;
    private List<EmployeePost> employeePosts;

    private FirebaseFirestore firebaseFirestore;
    //private ParkingsRecyclerAdapter parkingsRecyclerAdapter;
    private Employees_RecyclerAdapter employees_recyclerAdapter;

    private FirebaseAuth mAuth;
    private String current_user_id;

    public static String TAG="MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_select);

        employeePosts = new ArrayList<>();
        empl_list_view = findViewById(R.id.list_of_employees);





        employees_recyclerAdapter = new Employees_RecyclerAdapter(employeePosts);
        empl_list_view.setLayoutManager(new LinearLayoutManager(employees_select.this));
        empl_list_view.setAdapter(employees_recyclerAdapter);

        Intent intent = getIntent();
        String id = intent.getStringExtra("parking_id");

        Log.d(TAG, "onCreate: PARKINGU I ZGJEDHUR ESHTE"+id);

        firebaseFirestore = FirebaseFirestore.getInstance();

       firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                    if(doc.getType() == DocumentChange.Type.ADDED){


                        Integer docNr = queryDocumentSnapshots.size();

                        String blla = docNr.toString();
                        // String parking_owner_id = doc.getDocument().getString("owner_id");




                        String employeeId = doc.getDocument().getId();
                        Log.d(TAG, "onEvent: emloyee ID: "+employeeId+",NR DOKUMENTEVE:"+docNr);

                        EmployeePost employeePost = doc.getDocument().toObject(EmployeePost.class).withId(employeeId);
                        // EmployeePost employeePost1 = doc.getDocument().toObject(EmployeePost.class);
                        employeePosts.add(employeePost);

                        employees_recyclerAdapter.notifyDataSetChanged();

                    }

                }
            }
        });

    }
}

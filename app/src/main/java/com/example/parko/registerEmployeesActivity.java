package com.example.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class registerEmployeesActivity extends AppCompatActivity {



    private RecyclerView parkings_list_view;
    private List<ManagementPost> parkings_posts;

    private RecyclerView parking_grid_list_view;
    private List<ParkingGridPost> parkingsListGrid;


    private ManagementRecyclerAdapter managementRecyclerAdapter;
    private ParkingsGridRecyclerAdapter parkingsGridRecyclerAdapter;

    public static String vleraParkingut;

    private FirebaseFirestore firebaseFirestore;
    private TextView help_text;
    private GridLayoutManager gridLayoutManager;

    private FirebaseAuth mAuth;
    private String current_user_id;
    private static final String DEBUG_TAG = "Gestures";
    protected GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register_employees);



        help_text = (TextView) findViewById(R.id.add_emp_help);



        parkingsListGrid = new ArrayList<>();
        parking_grid_list_view = findViewById(R.id.park_list);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        parkingsGridRecyclerAdapter = new ParkingsGridRecyclerAdapter(parkingsListGrid, null,1 );
        parking_grid_list_view.setLayoutManager(gridLayoutManager);
        parking_grid_list_view.setAdapter(parkingsGridRecyclerAdapter);

       /* parking_grid_list_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {


            //mDetector = new GestureDetectorCompat(this,this);
            // Set the gesture detector as the double tap
            // listener.
      //  mDetector.setOnDoubleTapListener(this);
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                //parking_grid_list_view.setVisibility(View.GONE);
                //int position = rv.getChildAdapterPosition(this.View);

                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    final String TAG = "MyActivity";


                    Log.d(TAG, "onInterceptTouchEvent: Vlera e parametrit te pranuar" + ParkingsGridRecyclerAdapter.stringtoPass);
//                    Log.d(TAG, "onInterceptTouchEvent: Vlera e parametrit te klikuar" + rv.getChildAdapterPosition(findViewById(R.id.nr_places_grid)));

                    // parkingsGridRecyclerAdapter.getItemCount();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                final String TAG = "MyActivity";

                Log.d(TAG, "onInterceptTouchEvent: Vlera e parametrit string to pass;  "+ParkingsGridRecyclerAdapter.stringtoPass);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });*/

        //parkingsGridRecyclerAdapter = new ParkingsGridRecyclerAdapter(parkingsListGrid, this);
       // parkingsGridRecyclerAdapter = new ParkingsGridRecyclerAdapter(parkingsListGrid,this);
       // parking_grid_list_view.setLayoutManager(new LinearLayoutManager(registerEmployeesActivity.this));
        //parking_grid_list_view.setAdapter(managementRecyclerAdapter);

        final String TAG = "MyActivity";

        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Parkings").whereEqualTo("owner_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                    if(doc.getType() == DocumentChange.Type.ADDED){


                        Integer docNr = queryDocumentSnapshots.size();

                        String parkingGridPostId = doc.getDocument().getId();
                        Log.d(TAG, "onEvent: emloyee ID parkingsgrid: "+parkingGridPostId+",NR DOKUMENTEVE:"+docNr);

                        ParkingGridPost parkingGridPost = doc.getDocument().toObject(ParkingGridPost.class).withId(parkingGridPostId);
                        parkingsListGrid.add(parkingGridPost);


                        parkingsGridRecyclerAdapter.notifyDataSetChanged();

                    }

                }
            }
        });




    }
    public void onParkingClick(int position) {



    }

}






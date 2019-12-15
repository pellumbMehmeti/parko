package com.parkoKS.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ParkingInformationActivity extends AppCompatActivity {

    private Button location_picker_btn;
    private EditText location_lang_txt;
    private EditText parking_capacity;
    private Button register_parking_btn;
    private ProgressBar add_parking_progress;

    private Button decrement_button, mapsBtn;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private FirebaseFirestore firebaseFirestore;



    private String rrugaStreet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_information);

        decrement_button=(Button)findViewById(R.id.btnIDecrementTest);
        location_picker_btn=(Button)findViewById(R.id.location_btn);
        location_lang_txt = (EditText) findViewById(R.id.location_txt);
        parking_capacity=(EditText)findViewById(R.id.numberOfPlaces);
        register_parking_btn=(Button)findViewById(R.id.save_parking_btn);
        add_parking_progress=(ProgressBar)findViewById(R.id.add_parking_progressbar);
        mapsBtn = (Button) findViewById(R.id.mapsActivitybtn);

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParkingInformationActivity.this, MapsParkings.class);
                startActivity(intent);
                finish();
            }
        });

        final String TAG = "MyActivity";


        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

     /*  Bundle bundle = getIntent().getExtras();


        if (bundle.getString("street_adr")!=null)
        {
            Log.d(TAG, "onCreate: RRUGA NGA INTENTI"+bundle.getString("street_adr"));
           // location_lang_txt.setText(bundle.getString("street_adr"));
        }
        else {
            Log.d(TAG, "onCreate: Nuk ka RRUGA NGA INTENTI");
        }
*/
        decrement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String TAG = "MyActivity";
               final DocumentReference docUpdateReference = firebaseFirestore.collection("Parkings").document("FT0eoF2LjVgzRIh05CsA");
               docUpdateReference.getParent();

               // final Integer decrement = FieldValue

               firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                  @Override
                   public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                      DocumentSnapshot snapshot = transaction.get(docUpdateReference);

                      double newFreeSpaces = snapshot.getDouble("free_places")-1;
                      transaction.update(docUpdateReference, "free_places", newFreeSpaces);

                      return null;
                  }
               }).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d(TAG, "Transaction success!");

                    //   Toast.makeText(ParkingInformationActivity.this, "Transaction success", Toast.LENGTH_LONG).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w(TAG, "Transaction failure.", e);
                      // Toast.makeText(ParkingInformationActivity.this, "Transaction failure: "+e.getMessage(), Toast.LENGTH_LONG).show();

                   }
               });

               // docUpdate.update("Free_places", );
                //docUpdate.set()
            }
        });
        location_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ParkingInformationActivity.this, ParkingLocation.class);
                startActivity(intent);
                finish();
            }
        });

        register_parking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String location_details=location_lang_txt.getText().toString();
                String freePlacesTxt=parking_capacity.getText().toString();
                Integer freePlaces=Integer.parseInt(freePlacesTxt);
                //Integer freePlaces=parking_capacity.getText(

                String creation_details_time= FieldValue.serverTimestamp().toString();


                if(!TextUtils.isEmpty(location_details) && !TextUtils.isEmpty(freePlacesTxt)){

                    add_parking_progress.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("location_details",location_details);
                    postMap.put("free_places", freePlaces);
                    postMap.put("owner_id", current_user_id);
                    postMap.put("timestamp", creation_details_time);


                    firebaseFirestore.collection("Parkings").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful()){

                                add_parking_progress.setVisibility(View.INVISIBLE);


                                Toast.makeText(ParkingInformationActivity.this, "Parking Added Successfully", Toast.LENGTH_LONG).show();
                                Intent mainIntent= new Intent(ParkingInformationActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();

                            }else {
                                add_parking_progress.setVisibility(View.INVISIBLE);


                                Toast.makeText(ParkingInformationActivity.this, "Couldn't add parking!", Toast.LENGTH_LONG).show();


                            }
                        }
                    });

                }


            }
        });

    }
}

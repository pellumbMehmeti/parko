package com.example.parko;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class AccountSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private Spinner spinner_userType ;
    private EditText account_name;
    private Button setup_button;
    private ProgressBar setup_progressbar;
    private ImageButton buttonHome;

    private Button add_parking_button;
    private Button add_employee_button;

    private String user_id;
    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private BottomNavigationView mainBottomNavig;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;
    private MapsHomeFragment mapsHomeFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Boolean role_not_set=getIntent().getExtras().getBoolean("role_not_set");
        final String TAG = "MapActivity";
        Log.d(TAG, "onCreate: ROLE not SET? "+role_not_set);

        setupImage = (CircleImageView) findViewById(R.id.profile_picture);
        spinner_userType = (Spinner) findViewById(R.id.spinner_user_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AccountSettingsActivity.this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_userType.setAdapter(adapter);
        spinner_userType.setOnItemSelectedListener(this);
        buttonHome = (ImageButton) findViewById(R.id.imageButtonhome1);
        buttonHome.setVisibility(View.INVISIBLE);
        //add_employee_button = (Button) findViewById(R.id.add_employees_btn);
        //add_parking_button=(Button) findViewById(R.id.add_parking_btn);

        //mainBottomNavig= (BottomNavigationView) findViewById(R.id.bottom_nav_bar_acs);


        account_name = (EditText) findViewById(R.id.setup_name);
        setup_button = (Button) findViewById(R.id.setup_btn);



        setup_progressbar = (ProgressBar) findViewById(R.id.setup_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

       // setup_progressbar.setVisibility(View.VISIBLE);
    //    setup_button.setEnabled(false);


        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        mapsHomeFragment = new MapsHomeFragment();

      // replaceFragment(homeFragment);

/*        mainBottomNavig.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.bottomNav_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottomNav_account:
                        replaceFragment(accountFragment);
                        return true;
                    case R.id.bottomNav_notifications:
                        replaceFragment(notificationFragment);
                        return true;

                    default:
                        return false;

                }

            }
        });*/

        /*add_parking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        if(role_not_set.equals(false)){
            buttonHome.setVisibility(View.VISIBLE);
            buttonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccountSettingsActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        if(task.getResult().exists()){

                            String name=task.getResult().getString("user_name");
                            String image=task.getResult().getString("user_profile_image");
                            String testUserType= task.getResult().getString("user_type");




                            if(testUserType.equals("Parking Owner"))
                            {
                                /***/
                                /*add_employee_button.setVisibility(View.VISIBLE);
                                add_employee_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent newEmployeeIntent = new Intent(AccountSettingsActivity.this, registerEmployeesActivity.class);
                                        startActivity(newEmployeeIntent);
                                        finish();
                                    }
                                });*/
                                //add_parking_button.setVisibility(View.VISIBLE);
                                /*add_parking_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent newParkingIntent= new Intent(AccountSettingsActivity.this, ParkingInformationActivity.class);
                                        startActivity(newParkingIntent);
                                        finish();


                                    }
                                });*/

                            }





                            mainImageURI = Uri.parse(image);

                            account_name.setText(name);


                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.defaultimage);
                            Glide.with(AccountSettingsActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);




                            // spinner_userType.setVisibility(View.INVISIBLE);

                            Toast.makeText(AccountSettingsActivity.this, "Data exists!", Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(AccountSettingsActivity.this, "Data doesn't exist!", Toast.LENGTH_LONG).show();

                        }
                    }else {
                        String retrieveError = task.getException().getMessage();
                        Toast.makeText(AccountSettingsActivity.this, "Firestore Retrieve Error : "+retrieveError, Toast.LENGTH_LONG).show();

                    }

                    setup_progressbar.setVisibility(View.INVISIBLE);
                    setup_button.setEnabled(true);
                }
            });
        }






        setup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = account_name.getText().toString();
                final String user_type = spinner_userType.getSelectedItem().toString();
            //    setup_progressbar.setVisibility(View.VISIBLE);
                if(isChanged){





                if(!TextUtils.isEmpty(user_name) && mainImageURI != null)
                {
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    final StorageReference imagePath = storageReference.child("profile_images").child(user_id + ".jpg");

                    setup_progressbar.setVisibility(View.VISIBLE);

                    //imagePath.putFile(mainImageURI);

                    Task<Uri> urlTask =imagePath.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                String errorA=task.getException().getMessage();
                                Toast.makeText(AccountSettingsActivity.this, "Error : " +errorA, Toast.LENGTH_SHORT).show();
                                //throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imagePath.getDownloadUrl();
                        }

                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                storeToFirestore(task, user_name, user_type);
                                AlertDialog alertDialog = new AlertDialog.Builder(AccountSettingsActivity.this).create();
                                alertDialog.setTitle("Njoftim");






                            }
                            else{
                                String errorImage= task.getException().getMessage();
                                Toast.makeText(AccountSettingsActivity.this, "Image Error: " +errorImage, Toast.LENGTH_LONG).show();

                            }
                        }
                    });




                }
                }
                else {
                    storeToFirestore(null, user_name,null );
                }




            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(AccountSettingsActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {
                       BringImagePicker();
                    }
                }else {
                    BringImagePicker();
                }

            }
        });
    }

    private void storeToFirestore(@NonNull Task<Uri> task, String user_name, String user_type) {
        Uri downloadUri;
        if(task != null)
        {
             downloadUri=task.getResult();
        }
        else {
             downloadUri=mainImageURI;
        }

        Log.d(TAG, "storeToFirestore: User roli:"+user_type);
        Map<String, Object> userMap = new HashMap<>();

        if(user_type.equals("Parking Worker")){
            Boolean fired = false;
           // Map<String, Object> userMap = new HashMap<>();
            userMap.put("user_name", user_name);
            userMap.put("user_profile_image", downloadUri.toString());
            userMap.put("user_type", user_type);
            userMap.put("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
            userMap.put("works_at","0");
            userMap.put("fired",false);
        }
        else {
            userMap.put("user_name", user_name);
            userMap.put("user_profile_image", downloadUri.toString());
            userMap.put("user_type", user_type);
            userMap.put("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        }


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AccountSettingsActivity.this, "Të dhënat u përditësuan", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(AccountSettingsActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettingsActivity.this, "Firestore Error : "+error, Toast.LENGTH_LONG).show();
                }
            }
        });

        setup_progressbar.setVisibility(View.INVISIBLE);
    }

    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSettingsActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//me shkru per qka osht. e kqyr nese request code eshte CROPIMAGE dhe nese eshte e merr rezultatin nga URI dhe na i kthen rezultatet si URI,
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final String TAG = "MapActivity";

                mainImageURI = result.getUri();
                Log.d(TAG, "onActivityResult.MAIN IMAGE URI: "+mainImageURI);
                setupImage.setImageURI(mainImageURI);

                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choosen_type = adapterView.getItemAtPosition(i).toString();
       // Toast.makeText(AccountSettingsActivity.this, "Selected item"+choosen_type,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
/*
    private void replaceFragment(Fragment fragment){
        final String TAG = "MapActivity";

*/
/*       firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                user_type = documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: USER TYPI 2"+user_type);
                if ((user_type.equals("Parking Owner"))){
                    Log.d(TAG, "onSuccess: Congratulations on 3!");

                    //   menu.removeItem(R.id.bottomNav_notifications);
                    //    menu.findItem(R.id.bottomNav_notifications).setVisible(false);
                }

            }
        });*//*


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
*/



    }


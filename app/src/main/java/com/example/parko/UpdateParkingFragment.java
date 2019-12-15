package com.example.parko;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.firebase.firestore.FieldValue.serverTimestamp;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateParkingFragment extends Fragment {

    private static final String TAG = "MapActivity";
    private ImageView image_parking_upd;
    private EditText parking_name_upd;
    private EditText parking_capacity_upd;
    private EditText price2hr_updated,price4hr_updated,price8hr_updated,pricemt8hr_updated;
    private Button update_parking_btn;
    private Button changephotoBtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private boolean isChanged = false;

    private Uri mainImageUri = null;
    private StorageReference storageReference;

    public UpdateParkingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     //   fillFields();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_parking, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        image_parking_upd = (ImageView)view.findViewById(R.id.parking_img_upd);
        parking_name_upd = (EditText)view.findViewById(R.id.park_name_updt);
        parking_capacity_upd = (EditText)view.findViewById(R.id.park_capacity_updt);
        price2hr_updated = (EditText) view.findViewById(R.id.price2hr_upd);
        price4hr_updated = (EditText) view.findViewById(R.id.price4hr_upd);
        price8hr_updated = (EditText) view.findViewById(R.id.price8hr_upd);
        pricemt8hr_updated = (EditText) view.findViewById(R.id.pricemt8hr_upd);
        update_parking_btn = (Button) view.findViewById(R.id.confirm_upd_btn);
        changephotoBtn = (Button) view.findViewById(R.id.change_img_btn) ;
        Bundle bundle = getArguments();
        Log.d(TAG, "onViewCreated: UPDATE PARKING FRAGMENT.ID E MARRE ESHTE:\t"+bundle.getString("parking_id"));
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        current_user_id = firebaseAuth.getCurrentUser().getUid();


        fillFields();
        changephotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        //startActivityForResult(,1);
                    } else {
                        BringImagePicker();
                        //        Intent intent = new Intent();
                        //      startActivityForResult(intent ,1);
                    }
                } else {
                    BringImagePicker();
                }
            }
        });

        image_parking_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                  //startActivityForResult(,1);
                    } else {
                        BringImagePicker();
                //        Intent intent = new Intent();
                  //      startActivityForResult(intent ,1);
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
        update_parking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(isChanged){
                String update_time = FieldValue.serverTimestamp().toString();
                final StorageReference imagePath = storageReference.child("parking_images").child(current_user_id + update_time + ".jpg");
                Task<Uri> urlTask = imagePath.putFile(mainImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {

                            String errorA = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error : " + errorA, Toast.LENGTH_SHORT).show();
                            //throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imagePath.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            storeToFirestore(task);
                        } else {
                            String errorImage = task.getException().getMessage();
                            Toast.makeText(getContext(), "Image Error: " + errorImage, Toast.LENGTH_LONG).show();

                        }
                    }
                });}
            }
        });

    }
    public void fillFields(){
        Bundle bundle = getArguments();
        String documentId=bundle.getString("parking_id");
        firebaseFirestore.collection("Parkings").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String parking_name = task.getResult().getString("parking_name");
                    Double parking_capacity = task.getResult().getDouble("capacity_number");
                    String parking_image_URI= task.getResult().getString("parking_image_URI");
                    Double pricefor2hrs = task.getResult().getDouble("price2hrs");
                    Double pricefor4hrs = task.getResult().getDouble("price4hrs");
                    Double pricefor8hrs = task.getResult().getDouble("price8hrs");
                    Double priceforMt8hrs = task.getResult().getDouble("pricemt8hrs");


               //     String image=task.getResult().getString("user_profile_image");


                    if (!(parking_image_URI.equals(null))){
                        try {
                            mainImageUri = Uri.parse(parking_image_URI);


                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.defaultimage);
                            Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(parking_image_URI).into(image_parking_upd);

                            // Glide.with(getContext()).load(parking_image_URI).into(image_parking_upd);

                            Log.d(TAG, "Update Parking Frament.Foto URI:\t"+parking_image_URI);
                            parking_name_upd.setText(parking_name);
                            parking_capacity_upd.setText(parking_capacity.toString());
                            price2hr_updated.setText(pricefor2hrs.toString());
                            price4hr_updated.setText(pricefor4hrs.toString());
                            price8hr_updated.setText(pricefor8hrs.toString());
                            pricemt8hr_updated.setText(priceforMt8hrs.toString());

                        }
                        catch (Exception e){
                            Log.d(TAG, "Update Parking Frament.Foto Error."+e.getMessage());
                        }

                    }



                }
                else {
                    Log.d(TAG, "onComplete: Update Parking Fragment. Error:"+task.getException().getMessage());
                }
            }
        });
    }

    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
//Intent intent = new Intent(getContext(), getActivity());
     /*   Intent intent = new Intent(getContext(), CropImageActivity.class);
        startActivityForResult(intent, RESULT_OK);*/
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getContext(), this);

       // getActivity().start

    }
 //   public void onFragmentResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//me shkru per qka osht. e kqyr nese request code eshte CROPIMAGE dhe nese eshte e merr rezultatin nga URI dhe na i kthen rezultatet si URI,
        super.onActivityResult(requestCode, resultCode, data);
       // for (Fragment fragment : getFragmentManager().getFragments()) {
         //   fragment.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d(TAG, "onActivityResult: CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE");
                if (resultCode == RESULT_OK) {
                    mainImageUri = result.getUri();
                    image_parking_upd.setImageURI(mainImageUri);
                    Log.d(TAG, "onActivityResult: uri e zgjedhur:" + mainImageUri);



                    isChanged = true;
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d(TAG, "onActivityResult: Error. KA TE BEJE ME URL:"+error);
                } }
            else {
                Log.d(TAG, "onActivityResult: problem problem");
            }
     //   Log.d(TAG, "onActivityResult: NUK PO HIN NE IFA");}


    }
    private void storeToFirestore(@NonNull Task<Uri> task) {
        Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mainImageUri;
        }

        // Task<Uri> download_Uri=imagePath.getDownloadUrl();
        Log.d(TAG, "onComplete: FOTO E PARKINGU U RUAJT ME SUKSES.URI:\t" + downloadUri);
        Double capacity_number = Double.parseDouble(parking_capacity_upd.getText().toString());
        String parking_name = parking_name_upd.getText().toString();
        final Double price2hrs = Double.parseDouble(price2hr_updated.getText().toString());
        Double price4hrs = Double.parseDouble(price4hr_updated.getText().toString());
        Double price8hrs = Double.parseDouble(price8hr_updated.getText().toString());
        Double pricemt8hrs = Double.parseDouble(pricemt8hr_updated.getText().toString());
        String creation_details_time = serverTimestamp().toString();

        if (!TextUtils.isEmpty(parking_name)
                && !TextUtils.isEmpty(capacity_number.toString()) && !TextUtils.isEmpty(price2hrs.toString())
                && !TextUtils.isEmpty(price4hrs.toString()) && !TextUtils.isEmpty(price8hrs.toString())
                && !TextUtils.isEmpty(pricemt8hrs.toString())) {




            Map<String, Object> postMap = new HashMap<>();

            postMap.put("capacity_number", capacity_number);
            postMap.put("current_free_places",capacity_number);
            postMap.put("owner_id", current_user_id);
            postMap.put("timestamp", creation_details_time);
            postMap.put("parking_name", parking_name);
            postMap.put("price2hrs", price2hrs);
            postMap.put("price4hrs", price4hrs);
            postMap.put("price8hrs", price8hrs);
            postMap.put("pricemt8hrs", pricemt8hrs);
            postMap.put("parking_image_URI", downloadUri.toString());

            Bundle bundle = getArguments();
            String documentId=bundle.getString("parking_id");
            firebaseFirestore.collection("Parkings").document(documentId).update(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("Perditesimi u krye me sukses.Vazhdo ne faqen kryesore");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(),MainActivity.class);
                                    startActivity(intent);

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();}
                    else{
                        Log.d(TAG, "onComplete: Regjistrimi nuk mundi te behet!\nErrori:"+task.getException());
                    }

                }
            });


        }
        else {
            Log.d(TAG, "onClick: Mbushni fushat e zbrazeta!");
        }

    }
}

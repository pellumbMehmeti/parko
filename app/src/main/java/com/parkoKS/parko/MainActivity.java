package com.parkoKS.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    public static String user_type;
    private FirebaseFirestore firebaseFirestore;

    private BottomNavigationView mainBottomNavig;

    private BottomNavigationView topMenuNavig;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;
    private MapsHomeFragment mapsHomeFragment;
    private OnsiteBooking onsiteBooking;
    private UpdateParkingFragment updateParkingFragment;
    private ReservationListFragment reservationListFragment;
    private UnemployedWorkersFragment unemployedWorkersFragment;
    private StatisticsFragment statisticsFragment;
 /*   public static final String SHARED_PREF ="Shared_pref";
    public static final String USER_TYPE= "user_type";*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final String TAG = "MapActivity";
//        Log.d(TAG, "onCreate: useri eshte" + mAuth.getCurrentUser().getUid());
//        String user = mAuth.getCurrentUser().getUid();
  /*      firebaseFirestore.collection("Users").document(user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean fired = documentSnapshot.getBoolean("fired");
                Log.d(TAG, "onSuccess: FIRED????\t" + fired);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: NUK MUND TE GJENDET FIRED:" + e.getMessage());
            }
        });
  */      //  String currUsr = mAuth.getCurrentUser().getUid();
        //Log.d(TAG, "onCreate: Useri i tanishem:"+currUsr);


  /*      firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                user_type = documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: USER TYPI"+user_type);
                if (user_type.equals("Parking Owner")){
                    Log.d(TAG, "onSuccess: Congratulations on !");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: NUK MUND TE NXEJRRET roli.Gabimi"+e.getMessage());
            }
        });
*/
        //  Log.d(TAG, "onCreate: LLOJI I USERIT ESHTE:"+user_type);


        mainBottomNavig = (BottomNavigationView) findViewById(R.id.mainBottomNav);
//        topMenuNavig = (BottomNavigationView) findViewById(R.id.top_menu_bar);

        //fragments

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        mapsHomeFragment = new MapsHomeFragment();
        onsiteBooking = new OnsiteBooking();
        updateParkingFragment = new UpdateParkingFragment();
        reservationListFragment = new ReservationListFragment();
        unemployedWorkersFragment = new UnemployedWorkersFragment();
        statisticsFragment = new StatisticsFragment();



        replaceFragment(homeFragment);
/*
        topMenuNavig.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.map_frag_call:
                        replaceFragment(mapsHomeFragment);
                        return true;
                    case R.id.home_frag_call:
                        replaceFragment(homeFragment);
                        return true;


                    default:
                        return false;

                }
              //  return false;
            }
        });*/
        //  Toast.makeText(MainActivity.this,"User tpipi eh ashtrq:"+user_type,Toast.LENGTH_LONG).show();
/*if (user_type.equals("Parking Owner")){
    MenuItem menuItem = (MenuItem) findViewById(R.id.parking_addFs_btn);
    menuItem.setVisible(false);
}
   */
        mainBottomNavig.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.bottomNav_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottomNav_account:
                        compareRoles();

                        return true;
                    case R.id.bottomNav_notifications:
                        replaceFragment(reservationListFragment);
                        return true;

                    default:
                        return false;

                }

            }
        });



       /* mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);//ndoshta me ndrru min SDK ne 21 sepse nuk e perkrahin krejt versionet toolbarin!
        setActionBar(mainToolbar);

       getActionBar().setTitle("Parko");//me kqyr prap*/

    }
public void compareRoles(){
    firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {

                String role = task.getResult().getString("user_type");

                if (role.equals("Ordinary User")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("role", "ordinaryUser");
                    notificationFragment.setArguments(bundle);

                    replaceFragment(notificationFragment);

                } else if (role.equals("Parking Worker")) {
                    Boolean isWorkerFired = task.getResult().getBoolean("fired");
                    String workingPlace = task.getResult().getString("works_at");
                    if (isWorkerFired.equals(true) || workingPlace.equals("0")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("Informacion");
                        alertDialog.setMessage("Nuk keni qasje ne menaxhimin e parkingjeve.Duhet te jeni i punesuar ne njerin nga parkingjet qe te keni qasje.A doni te shikoni listen e rezervimeve tuaja te ardhshme?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "JO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PO",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("role", "workerFired");
                                        notificationFragment.setArguments(bundle);
                                        replaceFragment(notificationFragment);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else if (isWorkerFired.equals(false)) {
                        replaceFragment(accountFragment);
                    }


                } else if (role.equals("Parking Owner")) {
                    replaceFragment(accountFragment);
                }
            }
        }
    });
}
    @Override
    protected void onStart() {/*qitu duhet me shti me ta qel activity ne baze te rolit, me 1 if*/
        super.onStart();
        final String TAG = "MapActivity";
//        String usernow = mAuth.getCurrentUser().getUid();
  //      Log.d(TAG, "onStart: USERI AKTUAL" + usernow);
        //firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
            /**/


        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        final String TAG = "MapActivity";
menu.findItem(R.id.add_dlt_emps).setVisible(false);
 /*       final MenuItem item1 = (MenuItem) findViewById( R.id.parking_addFs_btn);
        final MenuItem item2 = (MenuItem )findViewById(R.id.onsite_reservation_item);
        MenuItem item3 = (MenuItem) findViewById(R.id.add_dlt_emps);*/
//       men
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String role = task.getResult().getString("user_type");
                    if(role.equals("Parking Worker")){
                        //||(role.equals("Ordinary User"))
                        menu.findItem(R.id.parking_addFs_btn).setVisible(false);
                        String works_at = task.getResult().getString("works_at");
                        if (works_at.equals("0")){
                            menu.findItem(R.id.onsite_reservation_item).setVisible(false);
                        }
                    }
                    else if(role.equals("Ordinary User")){
                        menu.findItem(R.id.onsite_reservation_item).setVisible(false);
                        menu.findItem(R.id.parking_addFs_btn).setVisible(false);
                    }
                   // else if(role.equals("Ordinary User")||(role.equals("Parking Owner"))){
                       // menu.findItem(R.id.onsite_reservation_item).setVisible(false);
                  //  }
                }
            }
        });
 /*       firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                user_type = documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: USER TYPI 2" + user_type);
               *//* if ((user_type.equals("Parking Owner"))){
                    Log.d(TAG, "onSuccess: Congratulations on 3!");
                    menu.findItem(R.id.parking_addFs_btn).setVisible(false);
                    menu.findItem(R.id.add_dlt_emps).setVisible(false);
                    mainBottomNavig.getMenu().removeItem(R.id.bottomNav_notifications);
                 //   menu.removeItem(R.id.bottomNav_notifications);
                //    menu.findItem(R.id.bottomNav_notifications).setVisible(false);
                }
*//*
            }
        });*/
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.stats_frag:
                replaceFragment(statisticsFragment);
                return true;
            case R.id.action_logout_btn:

                logOut();

                return true;
            case R.id.parking_addFs_btn:

                addParking();
                return true;
            case R.id.add_dlt_emps:
                addRemoveEmployees();
                return true;
            case R.id.onsite_reservation_item:
                replaceFragment(onsiteBooking);
                return true;

            case R.id.action_acc_settings_btn:

                accountSettings();
            default:
                return false;
        }


    }

    private void addParking() {
        Intent addParkingIntent = new Intent(MainActivity.this, ParkingLocation.class);
        startActivity(addParkingIntent);
        finish();
    }

    private void addRemoveEmployees() {
        Intent addRemoveEmps = new Intent(MainActivity.this, registerEmployeesActivity.class);
        startActivity(addRemoveEmps);
        finish();
    }

    private void accountSettings() {


        Intent accountSettingsIntent = new Intent(MainActivity.this, AccountSettingsActivity.class);
        accountSettingsIntent.putExtra("role_not_set", false);
        startActivity(accountSettingsIntent);
        finish();

    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();//nuk e len userin mu kthy sepse intenti fshihet
    }

    private void replaceFragment(Fragment fragment) {
        final String TAG = "MapActivity";

        /*firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        });*/

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();


    }

}

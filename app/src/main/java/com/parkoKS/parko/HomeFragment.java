package com.parkoKS.parko;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView parking_list_view;
    private List <ParkingPost> parkingsList;

    private FirebaseFirestore firebaseFirestore;
    private ParkingsRecyclerAdapter parkingsRecyclerAdapter;

    private BottomNavigationView top_menu_home;

    private MapsHomeFragment mapsHomeFragment;
    private HomeFragment homeFragment;





    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);



        parkingsList = new ArrayList<>();
        parking_list_view = view.findViewById(R.id.parking_list_view);

        parkingsRecyclerAdapter = new ParkingsRecyclerAdapter(parkingsList);
        parking_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        parking_list_view.setAdapter(parkingsRecyclerAdapter);

        top_menu_home = view.findViewById(R.id.menu_item_top);

        homeFragment = new HomeFragment();
        mapsHomeFragment = new MapsHomeFragment();

        replaceFragment(this);

        top_menu_home.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        });

        final String TAG = "MyActivity";

        firebaseFirestore = FirebaseFirestore.getInstance();

       /*  parking_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               boolean isRearchedBottom = !recyclerView.canScrollVertically(1);

                if(isRearchedBottom){
                    String desc =
                    Toast.makeText(container.getContext(), "Reached", );
                }

            }
        });*/

       // Query numri_dokumenteve = firebaseFirestore.collection("Parkings").whereEqualTo()
      /*  firebaseFirestore.collection("Parkings").whereEqualTo("owner_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());

                }
                else {
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){




                        if(doc.getType() == DocumentChange.Type.ADDED){




                            Integer docNr = queryDocumentSnapshots.size();


                            Log.w(TAG, "Numri i dokumenteve ku uid="+FirebaseAuth.getInstance().getCurrentUser().getUid()+" eshte :" +docNr);

                        }

                    }
                }
            }
        });*/
       // firebaseAuth = FirebaseAuth.getInstance();

      //  String currusr= firebaseAuth.getUid();
       // Log.d(TAG, "ViewHolder: curruser "+FirebaseAuth.getInstance().getCurrentUser().getUid());

       // firebaseFirestore.collection("Users").whereEqualTo("")


        firebaseFirestore.collection("Parkings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges() ){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Integer docNr = queryDocumentSnapshots.size();

                        String blla = docNr.toString();
                        String parking_owner_id = doc.getDocument().getString("owner_id");
              /*         String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        String krahasimi;

                        if(parking_owner_id.equals(currentUser)){
                            krahasimi = "Stringjet e njejta";
                        }
                        else {
                            krahasimi = "Stringjet nuk jane te njejta";
                        }


                      //  Log.w(TAG, "Numri i dokumenteve ne home:" +blla+", stringjet?"+krahasimi);
                        Log.w(TAG, "ID e perdoruesit:"+parking_owner_id);
*/



                        String parkingPostId = doc.getDocument().getId();

                        ParkingPost parkingPost = doc.getDocument().toObject(ParkingPost.class).withId(parkingPostId);
                        parkingsList.add(parkingPost);

                        parkingsRecyclerAdapter.notifyDataSetChanged();

                    }

                }
            }
        });

        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();



    }

}

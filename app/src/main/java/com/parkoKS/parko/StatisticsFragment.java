package com.parkoKS.parko;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

PieChart pieChart;
private FirebaseAuth firebaseAuth;
private FirebaseFirestore firebaseFirestore;
BarChart barChart1;
BarChart barChart2;
private RadioGroup stats_options;
private RadioButton stats_fitimet;
private RadioButton stats_vendetelira;
private RadioButton stats_rezervimet;
    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        pieChart = (PieChart)getView().findViewById(R.id.pieChart1);
        barChart1 = (BarChart)getView().findViewById(R.id.barchartfreePlaces);
        barChart2 = (BarChart)getView().findViewById(R.id.barchartMonthlyres);
        stats_options = (RadioGroup)getView().findViewById(R.id.radioOptions);
        stats_fitimet = (RadioButton)getView().findViewById(R.id.rdbtnFitimet);
        stats_vendetelira = (RadioButton) getView().findViewById(R.id.rdbtnVendeTeLiraptg);
        stats_rezervimet = (RadioButton)getView().findViewById(R.id.rdbtnRezervimeMuaj);
        stats_options.check(R.id.rdbtnFitimet);
        ownerPieChartCreation();
        createFreePlacesBarChart();
        createMonthlyReservationsBarChart();
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String roli= documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: Roli i userit eshte:"+roli);
                if (roli.equals("Parking Owner")){
                    ownerPieChartCreation();
                    createFreePlacesBarChart();
                    createMonthlyReservationsBarChart();
                    stats_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId){
                                case R.id.rdbtnFitimet:
                                    pieChart.setVisibility(View.VISIBLE);
                                    barChart1.setVisibility(View.INVISIBLE);
                                    barChart2.setVisibility(View.INVISIBLE);
                                    ownerPieChartCreation();
                                    Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                    break;
                                case R.id.rdbtnRezervimeMuaj:
                                    pieChart.setVisibility(View.INVISIBLE);
                                    barChart1.setVisibility(View.INVISIBLE);
                                    barChart2.setVisibility(View.VISIBLE);
                                    createMonthlyReservationsBarChart();
                                    Log.d(TAG, "onCheckedChanged: Reservimet e shtypur");
                                    break;
                                case R.id.rdbtnVendeTeLiraptg:
                                    barChart1.setVisibility(View.VISIBLE);
                                    pieChart.setVisibility(View.INVISIBLE);
                                    barChart2.setVisibility(View.INVISIBLE);
                                    createFreePlacesBarChart();
                                    Log.d(TAG, "onCheckedChanged: VENDE TE LIRA E SHTYPUR");
                                    break;
                                default:
                                    ownerPieChartCreation();
                                    Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                            }
                        }
                    });
                }
                else if(roli.equals("Parking Worker")){
                    String works_at = documentSnapshot.getString("works_at");
                    Log.d(TAG, "onSuccess: WORKS AT:"+works_at);
                    if (works_at.equals("0")){
                        stats_fitimet.setText("Shpenzimet");
                        stats_vendetelira.setVisibility(View.INVISIBLE);
                        createPieChartForOrdinaryUser();
                        createMonthlyReservationsBarChartForUser();
                        stats_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.rdbtnFitimet:
                                        pieChart.setVisibility(View.VISIBLE);
                                        barChart1.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.INVISIBLE);
                                        createPieChartForOrdinaryUser();
                                       // createPieChartForWorker();
                                        Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                        break;
                                    case R.id.rdbtnRezervimeMuaj:
                                        pieChart.setVisibility(View.INVISIBLE);
                                        barChart1.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.VISIBLE);
                                        createMonthlyReservationsBarChartForUser();
                                      //  createMonthlyReservationsForWorker();
                                        Log.d(TAG, "onCheckedChanged: Reservimet e shtypur");
                                        break;
                                    case R.id.rdbtnVendeTeLiraptg:
                                        barChart1.setVisibility(View.VISIBLE);
                                        pieChart.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.INVISIBLE);
                                        createFreePlacesForWorker();
                                        Log.d(TAG, "onCheckedChanged: VENDE TE LIRA E SHTYPUR");
                                        break;
                                    default:
                                        ownerPieChartCreation();
                                        Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                }
                            }
                        });

                    }
                    else if(!(works_at.equals("0"))) {
                        createFreePlacesForWorker();
                        createPieChartForWorker();
                        createMonthlyReservationsForWorker();
                        stats_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.rdbtnFitimet:
                                        pieChart.setVisibility(View.VISIBLE);
                                        barChart1.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.INVISIBLE);
                                        createPieChartForWorker();
                                        Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                        break;
                                    case R.id.rdbtnRezervimeMuaj:
                                        pieChart.setVisibility(View.INVISIBLE);
                                        barChart1.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.VISIBLE);
                                        createMonthlyReservationsForWorker();
                                        Log.d(TAG, "onCheckedChanged: Reservimet e shtypur");
                                        break;
                                    case R.id.rdbtnVendeTeLiraptg:
                                        barChart1.setVisibility(View.VISIBLE);
                                        pieChart.setVisibility(View.INVISIBLE);
                                        barChart2.setVisibility(View.INVISIBLE);
                                        createFreePlacesForWorker();
                                        Log.d(TAG, "onCheckedChanged: VENDE TE LIRA E SHTYPUR");
                                        break;
                                    default:
                                        createPieChartForWorker();
                                        Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                }
                            }
                        });

                    }
                }
                else if (roli.equals("Ordinary User")){
                    stats_fitimet.setText("Shpenzimet");
                    stats_vendetelira.setVisibility(View.INVISIBLE);
                    createPieChartForOrdinaryUser();
                    createMonthlyReservationsBarChartForUser();
                    stats_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId){
                                case R.id.rdbtnFitimet:
                                    pieChart.setVisibility(View.VISIBLE);
                                    barChart1.setVisibility(View.INVISIBLE);
                                    barChart2.setVisibility(View.INVISIBLE);
                                    createPieChartForOrdinaryUser();
                                    // createPieChartForWorker();
                                    Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                                    break;
                                case R.id.rdbtnRezervimeMuaj:
                                    pieChart.setVisibility(View.INVISIBLE);
                                    barChart1.setVisibility(View.INVISIBLE);
                                    barChart2.setVisibility(View.VISIBLE);
                                    createMonthlyReservationsBarChartForUser();
                                    //  createMonthlyReservationsForWorker();
                                    Log.d(TAG, "onCheckedChanged: Reservimet e shtypur");
                                    break;
                                default:
                                    createPieChartForOrdinaryUser();
                                    Log.d(TAG, "onCheckedChanged: FITIMET E STHY[PUR");
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Nuk mund te nxjerren te dhena");
            }
        });
        //Log.d(TAG, "onViewCreated: Roli i shfrytezuesit jashte koleksionitl:"+usertype);



}
public void createPieChartForOrdinaryUser(){
    pieChart.getDescription().setEnabled(true);
    pieChart.setExtraOffsets(5,10,5,5);

    pieChart.setDragDecelerationFrictionCoef(0.15f);
    pieChart.setDrawHoleEnabled(true);
    pieChart.setHoleColor(Color.WHITE);

    pieChart.setTransparentCircleRadius(91f);

    pieChart.animateX(1000, Easing.EaseInOutCubic);
//pieChart.setVisibility(View.INVISIBLE);

    final ArrayList<PieEntry> yValues = new ArrayList<>();
    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            // String whereworks = documentSnapshot.getString("works_at");
            firebaseFirestore.collection("Reservations").whereEqualTo("reserved_by",firebaseAuth.getCurrentUser().getUid()).whereEqualTo("paid",true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        ArrayList<Float> cmimet = new ArrayList<>();
                        Log.d(TAG, "onComplete: Po mirren dokumente");
                        HashMap<String,Double> listamap = new HashMap<String,Double>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //String emri_parkingut = document.get("parking_name").toString();
                            Double cmimi = document.getDouble("price");
                            Float cmimiFloat = cmimi.floatValue();
                            //  Float floatFitimi= fitimi.floatValue();
                            //listamap.put(emri_parkingut, fitimi);
                            //Double shuma = 0.0;
                            cmimet.add(cmimiFloat);





                        }
                        Double shuma = 0.0;
                        for(Integer i=0;i<cmimet.size();i++){
                            shuma += cmimet.get(i);
                        }
                        Integer failed = 0;
                        Integer success = 0;

                        for(Integer i=0; i<cmimet.size();i++){
                            if (cmimet.get(i).equals(0.0f)){
                                failed = failed +1;
                            }
                            else if(!(cmimet.get(i).equals(0.0f))){
                                success = success+1;
                            }
                        }


                        yValues.add(new PieEntry(success,"Të suksesshme"));
                        yValues.add(new PieEntry(failed,"Të deshtuara"));
                        Description description = new Description();
                        description.setText("Rezervimet e deshtuara dhe te suksesshme");
                        description.setTextSize(15);
                        pieChart.setDescription(description);
                        pieChart.setCenterText("Totali i shpenzuar:\n"+shuma+" €");
                        pieChart.setCenterTextSize(25f);

                        Log.d(TAG, "onComplete: YVALUES ARE:"+yValues);
                        PieDataSet dataSet = new PieDataSet(yValues,"Lloji i rezervimit");
                        dataSet.setSliceSpace(3f);
                        dataSet.setSelectionShift(5f);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData data = new PieData(dataSet);
                        //  data.setValueFormatter(new m);
                        data.setValueTextSize(12f);
                        data.setValueTextColor(Color.WHITE);
                        //   data.setValueFormatter(tf.bold);

                        pieChart.setData(data);

                    }
                    else {
                        Log.d(TAG, "onComplete: Error ghate marrjes se dokumenteve");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Informacion");
                    alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            });
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

        }
    });

}
public void createMonthlyReservationsBarChartForUser(){
    barChart2.getDescription().setEnabled(false);
    barChart2.setDrawBarShadow(false);
    barChart2.setDrawValueAboveBar(false);
    barChart2.setMaxVisibleValueCount(2500);
    barChart2.setPinchZoom(false);
    barChart2.setDrawGridBackground(true);
    barChart2.setExtraOffsets(5,10,5,5);
    barChart2.animateX(1000, Easing.EaseInOutCubic);

    final ArrayList<BarEntry> barEntries = new ArrayList<>();

    firebaseFirestore.collection("Reservations").whereEqualTo("reserved_by",firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){
                Integer s= 0;
                ArrayList<String> months = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    String data = document.getString("reservation_date");
                    months.add(data);
                    s += s+1;
                }
                //Integer i = 0;
                // Integer j=0;
                ArrayList<String> arrayekstraktuar =new ArrayList<>();
                ArrayList<Float> resMuaj = new ArrayList<>();
                Integer jan=0,feb=0,mar=0,apr=0,may=0,jun=0,jul=0,aug=0,sep=0,oct=0,nov=0,dec=0;
                // HashMap<>
                for (Integer i =0; i<months.size();i++){

                    String extractMonth = months.get(i).substring(0,3);

                    // jan =
                    arrayekstraktuar.add(extractMonth);
                    Log.d(TAG, "onComplete: Pjesa e ekstraktuar e stringut eshte:"+extractMonth);
                    if (extractMonth.equals("Dec")){
                        dec += 1;
                    }
                    else if(extractMonth.equals("Nov")){
                        nov+=1;
                    }
                    else if(extractMonth.equals("Oct")){
                        oct+=1;
                    }
                    else if(extractMonth.equals("Sep")){
                        sep +=1;
                    }
                    else if(extractMonth.equals("Aug")){
                        aug+=1;
                    }
                    else if(extractMonth.equals("Jul")){
                        jul +=1;
                    }
                    else if(extractMonth.equals("Jun")){
                        jun+=1;
                    }
                    else if(extractMonth.equals("May")){
                        may+=1;
                    }
                    else if(extractMonth.equals("Apr")){
                        apr +=1;
                    }
                    else if(extractMonth.equals("Mar")){
                        mar+=1;
                    }
                    else if(extractMonth.equals("Feb")){
                        feb +=1;
                    }
                }
                resMuaj.add(jan.floatValue());
                resMuaj.add(feb.floatValue());
                resMuaj.add(mar.floatValue());
                resMuaj.add(apr.floatValue());
                resMuaj.add(may.floatValue());
                resMuaj.add(jun.floatValue());
                resMuaj.add(jul.floatValue());
                resMuaj.add(aug.floatValue());
                resMuaj.add(sep.floatValue());
                resMuaj.add(oct.floatValue());
                resMuaj.add(nov.floatValue());
                resMuaj.add(dec.floatValue());

                for(Integer i =0; i<resMuaj.size();i++){
                    barEntries.add(new BarEntry(i.floatValue(),resMuaj.get(i)));
                }

                BarDataSet barDataSet = new BarDataSet(barEntries,"Rezervime sipas muajve");
                barDataSet.setVisible(true);

                //  final String[] labels = {"Janar","Shkurt","Mars","Prill","Maj","Qershor","Korrik","Gusht","Shtator","Tetor","Nentor","Dhjetor"};
                final String[] labels = {"Jan","Shk","Mar","Pri","Maj","Qer","Kor","Gus","Sht","Tet","Nen","Dhj"};

                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData= new BarData(barDataSet);
                barData.setBarWidth(0.9f);
                barData.setValueTextSize(12f);
                barData.setValueTextColor(Color.BLACK);

                barChart2.setData(barData);
                barChart2.setFitBars(true);
                Description description = new Description();
                description.setEnabled(false);
                description.setText("Rezervime sipas muajve");
                description.setTextSize(15);
                barChart2.setDescription(description);
                barChart2.invalidate();

                XAxis xAxis = barChart2.getXAxis();
                xAxis.setDrawLabels(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    String[] mValues = labels;

                    @Override
                    public String getFormattedValue(float value) {

                        return mValues[(int)value];
                    }
                });
                //  xAxis.setLabelRotationAngle(-90f);
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setLabelCount(barEntries.size());


            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
            alertDialog.setTitle("Informacion");
            alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    });
}

public void createPieChartForWorker(){
    pieChart.getDescription().setEnabled(true);
    pieChart.setExtraOffsets(5,10,5,5);

    pieChart.setDragDecelerationFrictionCoef(0.15f);
    pieChart.setDrawHoleEnabled(true);
    pieChart.setHoleColor(Color.WHITE);

    pieChart.setTransparentCircleRadius(91f);

    pieChart.animateX(1000, Easing.EaseInOutCubic);
//pieChart.setVisibility(View.INVISIBLE);

    final ArrayList<PieEntry> yValues = new ArrayList<>();
firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        String whereworks = documentSnapshot.getString("works_at");
        firebaseFirestore.collection("Parkings").whereEqualTo(FieldPath.documentId(),whereworks).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: Po mirren dokumente");
                    HashMap<String,Double> listamap = new HashMap<String,Double>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String emri_parkingut = document.get("parking_name").toString();
                        Double fitimi = document.getDouble("earnings");
                        Float floatFitimi= fitimi.floatValue();
                        listamap.put(emri_parkingut, fitimi);
                        Double shuma = 0.0;


                        yValues.add(new PieEntry(floatFitimi,emri_parkingut));
                        Description description = new Description();
                        description.setText("Të ardhurat për parkingjet tuaja");
                        description.setTextSize(15);
                        pieChart.setDescription(description);



                    }
                    Double shuma =0.0;
                    for(Double dbl: listamap.values()){
                        shuma += dbl;
                        //Log.d(TAG, "onComplete: shuma e hashmapit:"+shuma);
                    }
                    pieChart.setCenterText("Totali:\n"+shuma+" €");
                    pieChart.setCenterTextSize(25f);

                    Log.d(TAG, "onComplete: YVALUES ARE:"+yValues);
                    PieDataSet dataSet = new PieDataSet(yValues,"Parking Names");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                    PieData data = new PieData(dataSet);
                    //  data.setValueFormatter(new m);
                    data.setValueTextSize(12f);
                    data.setValueTextColor(Color.WHITE);
                    //   data.setValueFormatter(tf.bold);

                    pieChart.setData(data);

                }
                else {
                    Log.d(TAG, "onComplete: Error ghate marrjes se dokumenteve");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                alertDialog.setTitle("Informacion");
                alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {

    }
});

}
public void createFreePlacesForWorker(){
    barChart1.getDescription().setEnabled(false);
    barChart1.setDrawBarShadow(false);
    barChart1.setDrawValueAboveBar(false);
    barChart1.setMaxVisibleValueCount(500);
    barChart1.setPinchZoom(false);
    barChart1.setDrawGridBackground(true);
    barChart1.setExtraOffsets(5,10,5,5);
    barChart1.animateX(1000, Easing.EaseInOutCubic);


    final ArrayList<BarEntry> barEntries = new ArrayList<>();
    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            final String whereWorks = documentSnapshot.getString("works_at");
            Log.d(TAG, "onSuccess: ku punon:?"+whereWorks);

            firebaseFirestore.collection("Parkings").document(whereWorks).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Double freePlacesDbl = documentSnapshot.getDouble("current_free_places");
                    Float freePlacesFloat = freePlacesDbl.floatValue();
                    String parkingName = documentSnapshot.getString("parking_name");

                    barEntries.add(new BarEntry(1, freePlacesFloat));
                    Log.d(TAG, "Lista per barcchart:"+barEntries);
                    BarDataSet barDataSet = new BarDataSet(barEntries,"Free Places");
                    barDataSet.setVisible(true);


                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    BarData barData= new BarData(barDataSet);
                    barData.setBarWidth(0.9f);
                    barData.setValueTextSize(12f);
                    barData.setValueTextColor(Color.WHITE);

                    ArrayList<String> kupunon=new ArrayList<>();
                    kupunon.add(whereWorks);

                    String[] stringArr = new String[kupunon.size()];
                    stringArr = kupunon.toArray(stringArr);


                    final String[] labels = stringArr;

                    barChart1.setData(barData);
                    barChart1.setFitBars(true);
                    Description description = new Description();
                    description.setEnabled(false);
                    description.setText("Vendet e lira sipas parkingjeve");
                    description.setTextSize(15);
                    barChart1.setDescription(description);
                    barChart1.invalidate();

                    XAxis xAxis = barChart1.getXAxis();
                    xAxis.setDrawLabels(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelCount(barEntries.size());
        /*            xAxis.setValueFormatter(new ValueFormatter() {
                        String[] mValues = labels;

                        @Override
                        public String getFormattedValue(float value) {

                            return mValues[(int)value];
                        }
                    });*/
                    xAxis.setLabelRotationAngle(-90f);
                    xAxis.setTextSize(12f);
                    Log.d(TAG, "onComplete: PARKIGJNET jane:"+barChart1.getXAxis().getLabelCount());

                }

            })
           .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                    alertDialog.setTitle("Informacion");
                    alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            });
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

        }
    });


}
public void createMonthlyReservationsForWorker(){
    barChart2.getDescription().setEnabled(false);
    barChart2.setDrawBarShadow(false);
    barChart2.setDrawValueAboveBar(false);
    barChart2.setMaxVisibleValueCount(2500);
    barChart2.setPinchZoom(false);
    barChart2.setDrawGridBackground(true);
    barChart2.setExtraOffsets(5,10,5,5);
    barChart2.animateX(1000, Easing.EaseInOutCubic);

    final ArrayList<BarEntry> barEntries = new ArrayList<>();
firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        String where_works = documentSnapshot.getString("works_at");
        firebaseFirestore.collection("Reservations").whereEqualTo("parkingId",where_works).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Integer s= 0;
                    ArrayList<String> months = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String data = document.getString("reservation_date");
                        months.add(data);
                        s += s+1;
                    }
                    //Integer i = 0;
                    // Integer j=0;
                    ArrayList<String> arrayekstraktuar =new ArrayList<>();
                    ArrayList<Float> resMuaj = new ArrayList<>();
                    Integer jan=0,feb=0,mar=0,apr=0,may=0,jun=0,jul=0,aug=0,sep=0,oct=0,nov=0,dec=0;
                    // HashMap<>
                    for (Integer i =0; i<months.size();i++){

                        String extractMonth = months.get(i).substring(0,3);

                        // jan =
                        arrayekstraktuar.add(extractMonth);
                        Log.d(TAG, "onComplete: Pjesa e ekstraktuar e stringut eshte:"+extractMonth);
                        if (extractMonth.equals("Dec")){
                            dec += 1;
                        }
                        else if(extractMonth.equals("Nov")){
                            nov+=1;
                        }
                        else if(extractMonth.equals("Oct")){
                            oct+=1;
                        }
                        else if(extractMonth.equals("Sep")){
                            sep +=1;
                        }
                        else if(extractMonth.equals("Aug")){
                            aug+=1;
                        }
                        else if(extractMonth.equals("Jul")){
                            jul +=1;
                        }
                        else if(extractMonth.equals("Jun")){
                            jun+=1;
                        }
                        else if(extractMonth.equals("May")){
                            may+=1;
                        }
                        else if(extractMonth.equals("Apr")){
                            apr +=1;
                        }
                        else if(extractMonth.equals("Mar")){
                            mar+=1;
                        }
                        else if(extractMonth.equals("Feb")){
                            feb +=1;
                        }
                    }
                    resMuaj.add(jan.floatValue());
                    resMuaj.add(feb.floatValue());
                    resMuaj.add(mar.floatValue());
                    resMuaj.add(apr.floatValue());
                    resMuaj.add(may.floatValue());
                    resMuaj.add(jun.floatValue());
                    resMuaj.add(jul.floatValue());
                    resMuaj.add(aug.floatValue());
                    resMuaj.add(sep.floatValue());
                    resMuaj.add(oct.floatValue());
                    resMuaj.add(nov.floatValue());
                    resMuaj.add(dec.floatValue());

                    for(Integer i =0; i<resMuaj.size();i++){
                        barEntries.add(new BarEntry(i.floatValue(),resMuaj.get(i)));
                    }

                    BarDataSet barDataSet = new BarDataSet(barEntries,"Rezervime sipas muajve");
                    barDataSet.setVisible(true);

                    //  final String[] labels = {"Janar","Shkurt","Mars","Prill","Maj","Qershor","Korrik","Gusht","Shtator","Tetor","Nentor","Dhjetor"};
                    final String[] labels = {"Jan","Shk","Mar","Pri","Maj","Qer","Kor","Gus","Sht","Tet","Nen","Dhj"};

                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    BarData barData= new BarData(barDataSet);
                    barData.setBarWidth(0.9f);
                    barData.setValueTextSize(12f);
                    barData.setValueTextColor(Color.BLACK);

                    barChart2.setData(barData);
                    barChart2.setFitBars(true);
                    Description description = new Description();
                    description.setEnabled(false);
                    description.setText("Rezervime sipas muajve");
                    description.setTextSize(15);
                    barChart2.setDescription(description);
                    barChart2.invalidate();

                    XAxis xAxis = barChart2.getXAxis();
                    xAxis.setDrawLabels(false);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new ValueFormatter() {
                        String[] mValues = labels;

                        @Override
                        public String getFormattedValue(float value) {

                            return mValues[(int)value];
                        }
                    });
                    //  xAxis.setLabelRotationAngle(-90f);
                    xAxis.setTextSize(12f);
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setLabelCount(barEntries.size());


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                alertDialog.setTitle("Informacion");
                alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {

    }
});

}
public void createMonthlyReservationsBarChart(){
    barChart2.getDescription().setEnabled(false);
    barChart2.setDrawBarShadow(false);
    barChart2.setDrawValueAboveBar(false);
    barChart2.setMaxVisibleValueCount(2500);
    barChart2.setPinchZoom(false);
    barChart2.setDrawGridBackground(true);
    barChart2.setExtraOffsets(5,10,5,5);
    barChart2.animateX(1000, Easing.EaseInOutCubic);

    final ArrayList<BarEntry> barEntries = new ArrayList<>();

    firebaseFirestore.collection("Reservations").whereEqualTo("parking_owner_id",firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){
                Integer s= 0;
                ArrayList<String> months = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    String data = document.getString("reservation_date");
                    months.add(data);
                    s += s+1;
                }
                //Integer i = 0;
               // Integer j=0;
                ArrayList<String> arrayekstraktuar =new ArrayList<>();
                ArrayList<Float> resMuaj = new ArrayList<>();
                Integer jan=0,feb=0,mar=0,apr=0,may=0,jun=0,jul=0,aug=0,sep=0,oct=0,nov=0,dec=0;
               // HashMap<>
                for (Integer i =0; i<months.size();i++){

                    String extractMonth = months.get(i).substring(0,3);

                   // jan =
                    arrayekstraktuar.add(extractMonth);
                    Log.d(TAG, "onComplete: Pjesa e ekstraktuar e stringut eshte:"+extractMonth);
                    if (extractMonth.equals("Dec")){
                        dec += 1;
                    }
                    else if(extractMonth.equals("Nov")){
                        nov+=1;
                    }
                    else if(extractMonth.equals("Oct")){
                        oct+=1;
                    }
                    else if(extractMonth.equals("Sep")){
                        sep +=1;
                    }
                    else if(extractMonth.equals("Aug")){
                        aug+=1;
                    }
                    else if(extractMonth.equals("Jul")){
                        jul +=1;
                    }
                    else if(extractMonth.equals("Jun")){
                        jun+=1;
                    }
                    else if(extractMonth.equals("May")){
                        may+=1;
                    }
                    else if(extractMonth.equals("Apr")){
                        apr +=1;
                    }
                    else if(extractMonth.equals("Mar")){
                        mar+=1;
                    }
                    else if(extractMonth.equals("Feb")){
                        feb +=1;
                    }
                }
                resMuaj.add(jan.floatValue());
                resMuaj.add(feb.floatValue());
                resMuaj.add(mar.floatValue());
                resMuaj.add(apr.floatValue());
                resMuaj.add(may.floatValue());
                resMuaj.add(jun.floatValue());
                resMuaj.add(jul.floatValue());
                resMuaj.add(aug.floatValue());
                resMuaj.add(sep.floatValue());
                resMuaj.add(oct.floatValue());
                resMuaj.add(nov.floatValue());
                resMuaj.add(dec.floatValue());

                for(Integer i =0; i<resMuaj.size();i++){
                    barEntries.add(new BarEntry(i.floatValue(),resMuaj.get(i)));
                }

                BarDataSet barDataSet = new BarDataSet(barEntries,"Rezervime sipas muajve");
                barDataSet.setVisible(true);

              //  final String[] labels = {"Janar","Shkurt","Mars","Prill","Maj","Qershor","Korrik","Gusht","Shtator","Tetor","Nentor","Dhjetor"};
                final String[] labels = {"Jan","Shk","Mar","Pri","Maj","Qer","Kor","Gus","Sht","Tet","Nen","Dhj"};

                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData= new BarData(barDataSet);
                barData.setBarWidth(0.9f);
                barData.setValueTextSize(12f);
                barData.setValueTextColor(Color.BLACK);

                barChart2.setData(barData);
                barChart2.setFitBars(true);
                Description description = new Description();
                description.setEnabled(false);
                description.setText("Rezervime sipas muajve");
                description.setTextSize(15);
                barChart2.setDescription(description);
                barChart2.invalidate();

                XAxis xAxis = barChart2.getXAxis();
                xAxis.setDrawLabels(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    String[] mValues = labels;

                    @Override
                    public String getFormattedValue(float value) {

                        return mValues[(int)value];
                    }
                });
              //  xAxis.setLabelRotationAngle(-90f);
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setLabelCount(barEntries.size());


            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
            alertDialog.setTitle("Informacion");
            alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    });
}
public void createFreePlacesBarChart(){
        barChart1.getDescription().setEnabled(false);
        barChart1.setDrawBarShadow(false);
        barChart1.setDrawValueAboveBar(false);
        barChart1.setMaxVisibleValueCount(500);
        barChart1.setPinchZoom(false);
        barChart1.setDrawGridBackground(true);
        barChart1.setExtraOffsets(5,10,5,5);
        barChart1.animateX(1000, Easing.EaseInOutCubic);


        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        firebaseFirestore.collection("Parkings").whereEqualTo("owner_id",firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
           if (task.isSuccessful()){
               Log.d(TAG, "onComplete: Po mirret numri i vendeve te lira");
              ArrayList<String> parkingsList= new ArrayList<>();
              ArrayList<Float> freePlacesList = new ArrayList<>();
               for (QueryDocumentSnapshot document : task.getResult()){
                   Integer i=0;
                   //i += i+1;
                   Double vendetelira = document.getDouble("current_free_places");
                   String emri_parkingut = document.getString("parking_name");
                   Float vendeTeLiraFloat = vendetelira.floatValue();

                 //  barEntries.add(new BarEntry("0",vendeTeLiraFloat));


                  Float ifloat= i.floatValue();
              //    barEntries.add(new BarEntry(ifloat, vendeTeLiraFloat) );

                   parkingsList.add(emri_parkingut);
                   freePlacesList.add(vendeTeLiraFloat);


               }

               for(Integer i=0; i<freePlacesList.size();i++){
                   Log.d(TAG, "onComplete: Elementi="+i+" Eshte:"+parkingsList.get(i));
                   Float ifloat = i.floatValue();
                   Float valfloat = freePlacesList.get(i);
                   barEntries.add(new BarEntry(ifloat, valfloat));
                  // listaReParkingjeve.put(i,parkingsList.get(i));
               }

               Log.d(TAG, "Lista per barcchart:"+barEntries);
               BarDataSet barDataSet = new BarDataSet(barEntries,"Free Places");
               barDataSet.setVisible(true);


               barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
               BarData barData= new BarData(barDataSet);
               barData.setBarWidth(0.9f);
               barData.setValueTextSize(12f);
               barData.setValueTextColor(Color.WHITE);

               String[] stringArr = new String[parkingsList.size()];
               stringArr = parkingsList.toArray(stringArr);

  //           final String[] labels = stringArr;
//               final String parkingu = stringArr[0];

                barChart1.setData(barData);
                barChart1.setFitBars(true);
               Description description = new Description();
               description.setEnabled(false);
               description.setText("Vendet e lira sipas parkingjeve");
               description.setTextSize(15);
               barChart1.setDescription(description);
                barChart1.invalidate();

                XAxis xAxis = barChart1.getXAxis();
                xAxis.setDrawLabels(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelCount(barEntries.size());
        /*        xAxis.setValueFormatter(new ValueFormatter() {
                    String[] mValues = labels;

                    @Override
                    public String getFormattedValue(float value) {

                        return parkingu;
                    }
                });*/
                xAxis.setLabelRotationAngle(-90f);
                xAxis.setTextSize(12f);
               Log.d(TAG, "onComplete: PARKIGJNET jane:"+barChart1.getXAxis().getLabelCount());
//barChart1.setXAxisRenderer(new XAxisRenderer(){});
             // xAxis.lab
           }
           else {
               Log.d(TAG, "onComplete: Error ghate marrjes se dokumenteve");
           }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
                alertDialog.setTitle("Informacion");
                alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });
}




public void ownerPieChartCreation(){
    pieChart.getDescription().setEnabled(true);
    pieChart.setExtraOffsets(5,10,5,5);

    pieChart.setDragDecelerationFrictionCoef(0.15f);
    pieChart.setDrawHoleEnabled(true);
    pieChart.setHoleColor(Color.WHITE);

    pieChart.setTransparentCircleRadius(91f);

    pieChart.animateX(1000, Easing.EaseInOutCubic);
//pieChart.setVisibility(View.INVISIBLE);
    final ArrayList<PieEntry> yValues = new ArrayList<>();

    firebaseFirestore.collection("Parkings").whereEqualTo("owner_id",firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){
                Log.d(TAG, "onComplete: Po mirren dokumente");
                HashMap<String,Double> listamap = new HashMap<String,Double>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emri_parkingut = document.get("parking_name").toString();
                    Double fitimi = document.getDouble("earnings");
                    Float floatFitimi= fitimi.floatValue();
                    listamap.put(emri_parkingut, fitimi);
                    Double shuma = 0.0;


                    yValues.add(new PieEntry(floatFitimi,emri_parkingut));
                    Description description = new Description();
                    description.setText("Të ardhurat për parkingjet tuaja");
                    description.setTextSize(15);
                    pieChart.setDescription(description);



                }
                Double shuma =0.0;
                for(Double dbl: listamap.values()){
                    shuma += dbl;
                    //Log.d(TAG, "onComplete: shuma e hashmapit:"+shuma);
                }
                pieChart.setCenterText("Totali:\n"+shuma+" €");
                pieChart.setCenterTextSize(25f);

                Log.d(TAG, "onComplete: YVALUES ARE:"+yValues);
                PieDataSet dataSet = new PieDataSet(yValues,"Parking Names");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                PieData data = new PieData(dataSet);
                //  data.setValueFormatter(new m);
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.WHITE);
                //   data.setValueFormatter(tf.bold);

                pieChart.setData(data);

            }
            else {
                Log.d(TAG, "onComplete: Error ghate marrjes se dokumenteve");
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getView().getRootView().getContext()).create();
            alertDialog.setTitle("Informacion");
            alertDialog.setMessage("Nuk ka te dhena andaj nuk mund te paraqiten grafikisht");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    });
}

}

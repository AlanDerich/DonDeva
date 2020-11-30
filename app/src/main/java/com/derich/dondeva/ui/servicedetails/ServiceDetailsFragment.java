package com.derich.dondeva.ui.servicedetails;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.derich.dondeva.ProductPagerAdapter;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.ServicePics;
import com.derich.dondeva.ViewServicePicsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceDetailsFragment extends Fragment {
    private ViewPager mFragmentsContainer;
    private TabLayout mTabLayout;
    private MaterialTextView edtServName;
    private String pic,name,mainServiceName;
    private List<ServicePics> mServicePics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context mContext;
    private Button selectDate,selectTime;
    private FirebaseUser mUser;
    private String section;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_service_details, container, false);
        mFragmentsContainer=root.findViewById(R.id.specific_service_product_container);
        mTabLayout=root.findViewById(R.id.specific_service_tab_layout);
        edtServName=root.findViewById(R.id.edtServName);
        Button btnBook = root.findViewById(R.id.buttonBookService);
        mContext=getContext();
        getIncomingIntent();
        if (section.equals("admin")){
            btnBook.setVisibility(View.GONE);
        }
        else {
            btnBook.setVisibility(View.VISIBLE);
        }
        btnBook.setOnClickListener(view -> showBookDialog());
        return root;
    }

    private void showBookDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Make an appointment");

        LayoutInflater inflater = this.getLayoutInflater();
        View book_layout = inflater.inflate(R.layout.book_layout,null);
        selectDate = book_layout.findViewById(R.id.buttonDate);
        selectTime = book_layout.findViewById(R.id.buttonTime);
        //event for button
        selectDate.setOnClickListener(v -> chooseDate());
        selectDate.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        selectDate.setTextColor(getResources().getColor(R.color.black));
        selectTime.setOnClickListener(v -> chooseTime());
        selectTime.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        selectTime.setTextColor(getResources().getColor(R.color.black));
        alertDialog.setView(book_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            String kkk=selectDate.getText().toString();
            String mmm=selectTime.getText().toString();
            if (kkk.equals("select date")||mmm.equals("select preferred start time")){
                Toast.makeText(mContext,"Please select a valid date and time",Toast.LENGTH_LONG).show();
            }
            else {
                String addDate=selectDate.getText().toString();
                String addTime=selectTime.getText().toString();
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                final String dateOfAdd = sdf.format(new Date());
                RequestDetails mRequestDetails=new RequestDetails(addDate,addTime,name, mUser.getEmail(),dateOfAdd,pic,"pending");
                db.collection("RequestsStorage").document(encode(addDate)).collection("AllRequests").document(mUser.getEmail()+" " +name)
                        .set(mRequestDetails)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(),"Request sent successfully",Toast.LENGTH_LONG).show();
                            //initRecyclerView();

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }

    public static String encode(String coOrdns){
        return coOrdns.replace("/",",");
    }

    private void chooseTime() {
//        new TimePicker(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(mContext,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (timePicker, selectedHour, selectedMinute) -> selectTime.setText( selectedHour + ":" + selectedMinute), hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void chooseDate() {
        final Calendar mDate = Calendar.getInstance();

//        DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                DatePickerDialog.THEME_HOLO_LIGHT,this,year,month,day);
        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            mDate.set(Calendar.YEAR,year);
            mDate.set(Calendar.MONTH,month);
            mDate.set(Calendar.DAY_OF_MONTH,day);
            String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);selectDate.setText(sdf.format(mDate.getTime()));
        };
        new DatePickerDialog(mContext,DatePickerDialog.THEME_HOLO_LIGHT,date,mDate.get(Calendar.YEAR),mDate.get(Calendar.MONTH),mDate.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void getIncomingIntent() {
        Bundle bundle = this.getArguments();
//        Locale locale = new Locale("en","KE");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        if(bundle != null){
            name=bundle.getString("serviceName");
            edtServName.setText(name);
            pic=bundle.getString("servicePic");
            mainServiceName=bundle.getString("mainServiceName");
            section = bundle.getString("section");
            getServicePhotos();
        }
    }
    private void getServicePhotos(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllPhotos").whereEqualTo("serviceName",name).whereEqualTo("serviceCategory",mainServiceName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mServicePics = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            mServicePics.add(snapshot.toObject(ServicePics.class));
                        }
                    }
                    mServicePics.add(new ServicePics(pic,name,mainServiceName));
                    initViewPager();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for(ServicePics product: mServicePics){
            ViewServicePicsFragment viewProductFragment = new ViewServicePicsFragment(product);
            fragments.add(viewProductFragment);
        }
        ProductPagerAdapter mPagerAdapter = new ProductPagerAdapter(getParentFragmentManager(), fragments);
        mFragmentsContainer.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mFragmentsContainer, true);
    }

}
package com.derich.dondeva.ui.servicedetails;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.derich.dondeva.OfferDetails;
import com.derich.dondeva.ProductPagerAdapter;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.ServicePics;
import com.derich.dondeva.ViewProductFragment;
import com.derich.dondeva.ViewServicePicsFragment;
import com.derich.dondeva.ui.specificservice.SpecificService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceDetailsFragment extends Fragment {
    private ViewPager mFragmentsContainer;
    private TabLayout mTabLayout;
    private MaterialTextView edtServRequirementsPrice,edtServName,edtServRequirements,edtServTime,edtServFee;
    private String price,pic,name,requirements,fee,serviceHours,serviceMinutes,mainServiceName;
    private List<ServicePics> mServicePics;
    private ProductPagerAdapter mPagerAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context mContext;
    private Button btnBook;
    private Button selectDate,selectTime;
    private LinearLayout layoutEndTime;
    private TextView tvAmountSelected,book_service_total_amount_tv,book_service_service_fee_tv,requirements_actual_name,book_service_end_tv;
    private ImageButton imgButtonAdd,imgButtonDeduct;
    private int startHour,startMinute,endHour,endMinute;
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_service_details, container, false);
        mFragmentsContainer=root.findViewById(R.id.specific_service_product_container);
        mTabLayout=root.findViewById(R.id.specific_service_tab_layout);
        edtServRequirementsPrice = root.findViewById(R.id.edtServRequirementsPrice);
        edtServName=root.findViewById(R.id.edtServName);
        edtServRequirements=root.findViewById(R.id.edtServRequirements);
        edtServTime=root.findViewById(R.id.edtServTime);
        edtServFee=root.findViewById(R.id.edtServFee);
        btnBook = root.findViewById(R.id.buttonBookService);
        btnBook.setOnClickListener(view -> showBookDialog());
        mContext=getContext();

        getIncomingIntent();
        return root;
    }

    private void showBookDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Make an appointment");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View book_layout = inflater.inflate(R.layout.book_layout,null);
        selectDate = book_layout.findViewById(R.id.buttonDate);
        selectTime = book_layout.findViewById(R.id.buttonTime);
        //event for button
        selectDate.setOnClickListener(v -> chooseDate());

        selectTime.setOnClickListener(v -> chooseTime());
        layoutEndTime = book_layout.findViewById(R.id.book_service_end_layout);
        layoutEndTime.setVisibility(View.GONE);
        tvAmountSelected = book_layout.findViewById(R.id.textViewCurrentProductsSelected);
        requirements_actual_name = book_layout.findViewById(R.id.requirements_actual_name);
        requirements_actual_name.setText(requirements);
        book_service_end_tv  = book_layout.findViewById(R.id.book_service_end_tv);
        book_service_service_fee_tv  = book_layout.findViewById(R.id.book_service_service_fee_tv);
        book_service_service_fee_tv.setText(String.valueOf(fee));
        book_service_total_amount_tv = book_layout.findViewById(R.id.book_service_total_amount_tv);
        book_service_total_amount_tv.setText(String.valueOf(Integer.valueOf(fee)+Integer.valueOf(price)));
        imgButtonAdd = book_layout.findViewById(R.id.imageButtonAddItem);
        imgButtonDeduct = book_layout.findViewById(R.id.imageButtonSubtractItem);
        imgButtonAdd.setOnClickListener(view -> {
            int k=Integer.parseInt(tvAmountSelected.getText().toString());
            int next=++k;
            tvAmountSelected.setText(String.valueOf(next));
            int total =(Integer.parseInt(price) * next) + Integer.parseInt(fee);
            book_service_total_amount_tv.setText(String.valueOf(total));
        });
        imgButtonDeduct.setOnClickListener(view -> {
            int k=Integer.parseInt(tvAmountSelected.getText().toString());
            int next;
            if (k==1){

            }
            else {
                next=--k;
                tvAmountSelected.setText(String.valueOf(next));
                int total =(Integer.parseInt(price) * next) + Integer.parseInt(fee);
                tvAmountSelected.setText(String.valueOf(next));
                book_service_total_amount_tv.setText(String.valueOf(total));
            }
        });
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
                String addendTime=book_service_end_tv.getText().toString();
                String addSelectedAmount=tvAmountSelected.getText().toString();
                String totalAmount=book_service_total_amount_tv.getText().toString();
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                final String dateOfAdd = sdf.format(new Date());
                RequestDetails mRequestDetails=new RequestDetails(addDate,addTime,addendTime,addSelectedAmount,totalAmount,name, mUser.getEmail(),dateOfAdd,pic,"pending");
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

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
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
        mTimePicker = new TimePickerDialog(mContext,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startHour = selectedHour;
                startMinute=selectedMinute;
                selectTime.setText( selectedHour + ":" + selectedMinute);
                layoutEndTime.setVisibility(View.VISIBLE);
                int hours = Integer.parseInt(serviceHours);
                int minutes = Integer.parseInt(serviceMinutes);
                long minutesMillis = minutes * 60 * 1000;
                long hoursMillis = hours * 60 * 60 * 1000;
                long totalMillis=minutesMillis+hoursMillis;
                final String dateString = selectedHour+":"+selectedMinute;
                DateFormat format = new SimpleDateFormat("HH:mm",Locale.US);
                Date d = null;
                try {
                    d = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                d.setTime(d.getTime() + totalMillis);
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                final String dateOfAdd = format.format(d);
                book_service_end_tv.setText(dateOfAdd);
            }
        }, hour, minute, true);//Yes 24 hour time
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
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        if(bundle != null){
            name=bundle.getString("serviceName");
            edtServName.setText(name);
            pic=bundle.getString("servicePic");

            requirements=bundle.getString("serviceRequirements");
            edtServRequirements.setText(requirements);
            price=bundle.getString("serviceRequirementsPrice");
            edtServRequirementsPrice.setText(fmt.format(Integer.valueOf(price)));
            fee=bundle.getString("serviceFee");
            edtServFee.setText(fmt.format(Integer.valueOf(fee)));
            serviceHours=bundle.getString("serviceHours");
            serviceMinutes=bundle.getString("serviceMinutes");
            mainServiceName=bundle.getString("mainServiceName");
            edtServTime.setText(serviceHours+":"+serviceMinutes);
            getServicePhotos();
        }
    }
    private void getServicePhotos(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllPhotos").whereEqualTo("serviceName",name).whereEqualTo("serviceCategory",mainServiceName).get()
                .addOnSuccessListener((OnSuccessListener<QuerySnapshot>) queryDocumentSnapshots -> {
                    mServicePics = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            mServicePics.add(snapshot.toObject(ServicePics.class));
                        }
                    } else {
//                            Toast.makeText(mContext, "No house photos added yet. photos you add will appear here", Toast.LENGTH_LONG).show();
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
        mPagerAdapter = new ProductPagerAdapter(getParentFragmentManager(), fragments);
        mFragmentsContainer.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mFragmentsContainer, true);
    }

}
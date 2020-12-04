package com.derich.dondeva;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.ui.servicedetails.ServiceDetailsFragment;
import com.derich.dondeva.ui.specificservice.SpecificService;


public class ViewProductFragment extends Fragment {

    private static final String TAG = "ViewProductFragment";

    //widgets
    public ImageView mImageView;
//    private TextView mTitle;
//    private TextView mPrice;
    private Context mContext;
    //vars
    public OfferDetails mProduct;
    private String mSection;
    private SpecificService mService;
    private ConstraintLayout cvLayout;

    public ViewProductFragment(OfferDetails product, SpecificService service,String section) {
        mProduct = product;
        mSection = section;
        mService= service;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_product, container, false);
        mImageView = view.findViewById(R.id.image);
        mContext=getContext();
        cvLayout=view.findViewById(R.id.cv_view_product_fragment);
        cvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailsFragment();
            }
        });
//        mTitle = view.findViewById(R.id.title);
//        mPrice = view.findViewById(R.id.price);

        setProduct();

        return view;
    }

    private void setProduct(){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_loading);

        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(mProduct.getPic())
                .into(mImageView);

//        mTitle.setText(mProduct.getOfferName());
//        mPrice.setText(BigDecimalUtil.getValue(mProduct.getPrice()));
    }
    private void showDetailsFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new ServiceDetailsFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("mainServiceName",mService.getSsMainName());
        args.putString("serviceName",mService.getSsName());
        args.putString("servicePic",mService.getSsPic());
        args.putString("section",mSection);
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }


}
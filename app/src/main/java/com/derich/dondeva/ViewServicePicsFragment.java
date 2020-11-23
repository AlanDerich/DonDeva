package com.derich.dondeva;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class ViewServicePicsFragment extends Fragment {

    private static final String TAG = "ViewProductFragment";

    //widgets
    public ImageView mImageView;
    private TextView mTitle;
    private TextView mPrice;

    //vars
    public ServicePics mProduct;

    public ViewServicePicsFragment(ServicePics product) {
        mProduct = product;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_service_pics, container, false);
        mImageView = view.findViewById(R.id.imageViewServicePic);

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
//        mPrice.setText(BigDecimalUtil.getValue(mProduct.getPrice()));
    }


}
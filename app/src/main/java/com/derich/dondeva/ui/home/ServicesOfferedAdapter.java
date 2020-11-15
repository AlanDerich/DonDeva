package com.derich.dondeva.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.R;
import com.derich.dondeva.ui.gallery.GalleryFragment;

import java.io.File;
import java.util.List;

public class ServicesOfferedAdapter extends RecyclerView.Adapter<ServicesOfferedAdapter.ViewHolder>{
    Context mContext;
    List<Services> mServices;
    private File localFile;
    private Bitmap bmp;
    private ViewHolder holder1;
    private ServicesOfferedAdapter.OnItemsClickListener onItemsClickListener;
    private int pos;

    public ServicesOfferedAdapter(List<Services> mServices, OnItemsClickListener onItemsClickListener) {
        this.mServices = mServices;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_services,parent,false);
        mContext = parent.getContext();
        return new ServicesOfferedAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvName.setText(mServices.get(position).getServiceName());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_loading);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mServices.get(position).getServicePic())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> {
            Toast.makeText(mContext, "On click called!", Toast.LENGTH_LONG).show();
            onItemsClickListener.onItemsClick(mServices.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName;
        private ImageView imgCategory;
        private CardView mainLayout;
        ServicesOfferedAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, ServicesOfferedAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleService);
            imgCategory=itemView.findViewById(R.id.imageService);
            mainLayout=itemView.findViewById(R.id.card_view_plots);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(mServices.get(getAdapterPosition()));
        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(Services mServ);
    }
}

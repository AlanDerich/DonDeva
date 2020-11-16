package com.derich.dondeva.ui.specificservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.R;
import com.derich.dondeva.ui.home.Services;

import java.io.File;
import java.util.List;

public class SpecificServiceAdapter extends RecyclerView.Adapter<SpecificServiceAdapter.ViewHolder>{
    Context mContext;
    List<SpecificService> mServices;
    private File localFile;
    private Bitmap bmp;
    private SpecificServiceAdapter.ViewHolder holder1;
    private SpecificServiceAdapter.OnItemsClickListener onItemsClickListener;
    private int pos;

    public SpecificServiceAdapter(List<SpecificService> mServices, SpecificServiceAdapter.OnItemsClickListener onItemsClickListener) {
        this.mServices = mServices;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public SpecificServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_services,parent,false);
        mContext = parent.getContext();
        return new SpecificServiceAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SpecificServiceAdapter.ViewHolder holder, final int position) {
        holder.tvName.setText(mServices.get(position).getSsName());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_loading);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mServices.get(position).getSsPic())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> {
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
        SpecificServiceAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, SpecificServiceAdapter.OnItemsClickListener onItemsClickListener) {
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
        void onItemsClick(SpecificService mServ);
    }
}
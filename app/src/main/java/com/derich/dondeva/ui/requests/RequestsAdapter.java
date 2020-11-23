package com.derich.dondeva.ui.requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.ui.home.Services;

import java.io.File;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{
    Context mContext;
    List<RequestDetails> mServices;
    private RequestsAdapter.OnItemsClickListener onItemsClickListener;
    String section;
    private int pos;

    public RequestsAdapter(List<RequestDetails> mServices, RequestsAdapter.OnItemsClickListener onItemsClickListener,String section) {
        this.mServices = mServices;
        this.onItemsClickListener = onItemsClickListener;
        this.section=section;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_requests,parent,false);
        mContext = parent.getContext();
        return new RequestsAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsAdapter.ViewHolder holder, final int position) {
        holder.tvName.setText(mServices.get(position).getServiceName());
        holder.tvDate.setText("Date: "+mServices.get(position).getDate());
        holder.tvTime.setText("Time: "+mServices.get(position).getStartTime()+" to "+ mServices.get(position).getEndTime());
        holder.tvAmount.setText("Expected amount: "+mServices.get(position).getTotalAmount());
        holder.tvStatus.setText("Status: "+mServices.get(position).getStatus());
        if (section.equals("admin")){
            if (mServices.get(position).getStatus().equals("Approved")){
                holder.linearLayout.setVisibility(View.GONE);
            }
            else {
                holder.linearLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.linearLayout.setVisibility(View.GONE);
        }
        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemsClickListener.onItemsClick(mServices.get(position));
            }
        });
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_loading);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mServices.get(position).getImage())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> {
//            onItemsClickListener.onItemsClick(mServices.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvDate,tvTime,tvAmount,tvStatus;
        private ImageView imgCategory;
        private RelativeLayout mainLayout;
        private LinearLayout linearLayout;
        private Button btnStatus;
        RequestsAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, RequestsAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.title_view_orders);
            tvDate=itemView.findViewById(R.id.date_view_orders);
            tvTime=itemView.findViewById(R.id.time_view_orders);
            btnStatus=itemView.findViewById(R.id.button_change_status_orders);
            tvAmount=itemView.findViewById(R.id.amount_view_orders);
            tvStatus=itemView.findViewById(R.id.status_view_orders);
            imgCategory=itemView.findViewById(R.id.image_view_orders);
            linearLayout=itemView.findViewById(R.id.linear_layout_change_status_orders);
            mainLayout=itemView.findViewById(R.id.main_list_requests);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(mServices.get(getAdapterPosition()));
        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(RequestDetails mServ);
    }
}
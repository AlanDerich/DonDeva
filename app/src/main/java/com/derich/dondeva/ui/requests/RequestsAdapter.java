package com.derich.dondeva.ui.requests;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{
    Context mContext;
    List<RequestDetails> mServices;
     RequestsAdapter.OnItemsClickListener onItemsClickListener;
    String section;

    public RequestsAdapter(List<RequestDetails> mServices, RequestsAdapter.OnItemsClickListener onItemsClickListener,String section) {
        this.mServices = mServices;
        this.onItemsClickListener = onItemsClickListener;
        this.section=section;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_requests,parent,false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvName.setText(mServices.get(position).getServiceName());
        holder.tvDate.setText("Date: "+mServices.get(position).getDate());
        holder.tvTime.setText("Time: "+mServices.get(position).getStartTime());
        holder.tvPhone.setText(mServices.get(position).getPhoneNum());
        holder.tvStatus.setText(mServices.get(position).getStatus());
        holder.tvPhone.setOnClickListener(view -> {
            Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",mServices.get(position).getPhoneNum(),null));
            mContext.startActivity(intentCall);
        });
        if (section.equals("admin")){
            if (mServices.get(position).getStatus().equals("Approved")){
                holder.linearLayout.setVisibility(View.GONE);
                holder.tvStatus.setTextColor(Color.GREEN);
            }
            else if (mServices.get(position).getStatus().equals("Declined")){
                holder.linearLayout.setVisibility(View.GONE);
                holder.tvStatus.setTextColor(Color.RED);
            }
            else {
                holder.linearLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.linearLayout.setVisibility(View.GONE);
            if (mServices.get(position).getStatus().equals("Approved")){
                holder.tvStatus.setTextColor(Color.GREEN);
            }
            else if (mServices.get(position).getStatus().equals("Declined")){
                holder.tvStatus.setTextColor(Color.RED);
            }
            else {
                holder.tvStatus.setTextColor(Color.BLACK);
            }
        }
        holder.btnApprove.setOnClickListener(view -> onItemsClickListener.onItemsClick(mServices.get(position),"approve"));
        holder.btnDecline.setOnClickListener(view -> onItemsClickListener.onItemsClick(mServices.get(position),"decline"));
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
        private final TextView tvName,tvDate,tvTime,tvStatus,tvPhone;
        private final ImageView imgCategory;
        private final RelativeLayout mainLayout;
        private final LinearLayout linearLayout;
        private final Button btnApprove,btnDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.title_view_orders);
            tvPhone=itemView.findViewById(R.id.status_request_phone);
            tvDate=itemView.findViewById(R.id.date_view_orders);
            tvTime=itemView.findViewById(R.id.time_view_orders);
            btnApprove=itemView.findViewById(R.id.button_change_status_orders);
            btnDecline=itemView.findViewById(R.id.button_change_decline_orders);
            tvStatus=itemView.findViewById(R.id.status_view_orders);
            imgCategory=itemView.findViewById(R.id.image_view_orders);
            linearLayout=itemView.findViewById(R.id.linear_layout_change_status_orders);
            mainLayout=itemView.findViewById(R.id.main_list_requests);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            Toast.makeText(mContext,"Clickedd",Toast.LENGTH_SHORT).show();
            onItemsClickListener.onItemsClick(mServices.get(getAdapterPosition()),"delete");
        }
    }
    public RequestDetails getNoteAtPos(int pos){
        return mServices.get(pos);
    }
    public interface OnItemsClickListener{
        void onItemsClick(RequestDetails mServ,String action);
    }
}
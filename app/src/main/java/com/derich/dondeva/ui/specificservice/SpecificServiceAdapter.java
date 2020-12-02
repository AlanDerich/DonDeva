package com.derich.dondeva.ui.specificservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.dondeva.Favorites;
import com.derich.dondeva.R;
import com.derich.dondeva.ui.home.Services;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class SpecificServiceAdapter extends RecyclerView.Adapter<SpecificServiceAdapter.ViewHolder>{
    Context mContext;
    List<SpecificService> mServices;
    private SpecificServiceAdapter.OnItemsClickListener onItemsClickListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    List<String> mFavorites;

    public SpecificServiceAdapter(List<SpecificService> mServices, SpecificServiceAdapter.OnItemsClickListener onItemsClickListener,List<String> mFavorites) {
        this.mServices = mServices;
        this.mFavorites=mFavorites;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public SpecificServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_specific_service,parent,false);
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
        if (mFavorites!=null){
            if (mFavorites.size()!=0){
                for (String string : mFavorites) {
                    if(string.equals(mServices.get(position).getSsName())){
                        holder.imbBtnFav.setImageResource(R.drawable.ic_favorited);
                    }
                }
            }
        }

        holder.imbBtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser!=null){
                   if (checkIfFavorited(mServices.get(position).getSsName())){
                       db.collection("Favorites").document(mUser.getEmail()).collection("AllFavorites").document(mServices.get(position).getSsName())
                               .delete()
                               .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                   Toast.makeText(mContext,"Removed from Favorites",Toast.LENGTH_LONG).show();
                                   holder.imbBtnFav.setImageResource(R.drawable.ic_favourite);
                                   mServices.remove(position);
                                   notifyDataSetChanged();

                               })
                               .addOnFailureListener(e -> Toast.makeText(mContext,"Not removed. Try again later.",Toast.LENGTH_LONG).show());
                   }
                   else {
                        Favorites mFav=new Favorites(mServices.get(position).getSsName(),mUser.getEmail(),mServices.get(position).getSsMainName());
                        db.collection("Favorites").document(mUser.getEmail()).collection("AllFavorites").document(mServices.get(position).getSsName())
                                .set(mFav)
                                .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(mContext,"Added to Favorites",Toast.LENGTH_LONG).show();
                                    holder.imbBtnFav.setImageResource(R.drawable.ic_favorited);

                                })
                                .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }

    private Boolean checkIfFavorited(String ssName) {
        if (mFavorites.size()!=0){
            for (String string : mFavorites) {
                if(string.equals(ssName)){
                   return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName;
        private ImageView imgCategory;
        private ImageButton imbBtnFav;
        private CardView mainLayout;
        SpecificServiceAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, SpecificServiceAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleService);
            imbBtnFav=itemView.findViewById(R.id.imageButtonFavorite);
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
package com.derich.dondeva.ui.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.dondeva.LoginActivity;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.derich.dondeva.ui.servicedetails.ServiceDetailsFragment.encode;

public class RequestsFragment extends Fragment implements RequestsAdapter.OnItemsClickListener {
    private RecyclerView rvOffers;
    RequestsAdapter mAdapter;
    private List<RequestDetails> mAllOrders;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserDetails> mUserr;
    Context mContext;
    private String section;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_requests, container, false);
        rvOffers = root.findViewById(R.id.rv_requests);
        mContext=getActivity();
        checkUser();
        return root;
    }

    private void getAllOrders() {
        db.collectionGroup("AllRequests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mAllOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                mAllOrders.add(snapshot.toObject(RequestDetails.class));
                            }
                        } else {
                            Toast.makeText(mContext, "No Orders found. Orders you add will appear here", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerview();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    private void initRecyclerview() {
        mAdapter = new RequestsAdapter(mAllOrders,this,section);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        rvOffers.setLayoutManager(linearLayoutManager);
        rvOffers.setAdapter(mAdapter);
        rvOffers.setVisibility(View.VISIBLE);
    }

    private void checkUser() {
        if (mUser!=null){
            mUserr= new ArrayList<>();
            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collectionGroup("registeredUsers").whereEqualTo("username",mUser.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        mUserr.add(snapshot.toObject(UserDetails.class));
                    }
                    int size = mUserr.size();
                    int position;
                    if (size==1){
                        position=0;
                        UserDetails userDetails= mUserr.get(position);
                        section = userDetails.getSection();
                            if (section.equals("admin")){
                                getAllOrders();
                            }
                            else {
                                getUserOrders();
                            }
                    }

                } else {
                    Toast.makeText(mContext,"No bookings found." ,Toast.LENGTH_LONG).show();
                }
            })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("kkk","Error" + e);
                    });
        }
        else {
            Intent loginIntent=new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private void getUserOrders() {
        db.collectionGroup("AllRequests").whereEqualTo("username",mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mAllOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                mAllOrders.add(snapshot.toObject(RequestDetails.class));
                            }
                        } else {
                            Toast.makeText(mContext, "No Orders found. Orders you add will appear here", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerview();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    @Override
    public void onItemsClick(RequestDetails mServ) {
        RequestDetails mRequestDetails=new RequestDetails(mServ.getDate(),mServ.getStartTime(),mServ.getEndTime(),mServ.getRequirementsAmount(),mServ.getTotalAmount(),mServ.getServiceName(),mServ.getUsername(),mServ.getRequestDate(),mServ.getImage(),"Approved");
        db.collection("RequestsStorage").document(encode(mServ.getDate())).collection("AllRequests").document(mServ.getUsername()+" " +mServ.getServiceName())
                .set(mRequestDetails)
                .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                    Toast.makeText(getContext(),"Request approved successfully",Toast.LENGTH_LONG).show();
                     getAllOrders();

                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
    }
}
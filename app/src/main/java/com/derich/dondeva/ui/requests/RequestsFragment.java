package com.derich.dondeva.ui.requests;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.dondeva.LoginActivity;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.UserDetails;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
        mContext = getActivity();
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        if(navView.getBadge(R.id.nav_requests) !=null){
            BadgeDrawable badgeDrawable= navView.getBadge(R.id.nav_requests);
            assert badgeDrawable != null;
            badgeDrawable.setVisible(false);
            badgeDrawable.clearNumber();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                RequestDetails mServ=mAdapter.getNoteAtPos(viewHolder.getAdapterPosition());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Do you want to delete this request?");
                alertDialog.setPositiveButton("YES", (dialog, i) -> {
                    {
                        db.collection("RequestsStorage").document(encode(mServ.getDate())).collection("AllRequests").document(mServ.getUsername() + " " + mServ.getServiceName())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(), "Request deleted successfully", Toast.LENGTH_LONG).show();
                                   getIncomingIntent();

                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Not deleted. Try again later.", Toast.LENGTH_LONG).show());
                    }
                    dialog.dismiss();
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(rvOffers);
        return root;
    }


    @Override
    public void onPause() {
        super.onPause();
//        checkUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUser();
    }

    private void getAllRequests() {
        db.collectionGroup("AllRequests").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOrders = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mAllOrders.add(snapshot.toObject(RequestDetails.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No requests found. Your requests will appear here", Toast.LENGTH_LONG).show();
                    }
                    initRecyclerview();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.d("HouseInfo", "error " + e);
                });
    }
    private void getAllRequests(String date) {
        db.collectionGroup("AllRequests").whereEqualTo("date",date).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOrders = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mAllOrders.add(snapshot.toObject(RequestDetails.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No requests found for the specific date", Toast.LENGTH_LONG).show();
                        getAllRequests();
                    }
                    initRecyclerview();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    private void initRecyclerview() {
        mAdapter = new RequestsAdapter(mAllOrders, this, section);
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rvOffers.setLayoutManager(linearLayoutManager);
        rvOffers.setAdapter(mAdapter);
        rvOffers.setVisibility(View.VISIBLE);
    }

    private void checkUser() {
        if (mUser != null) {
            mUserr = new ArrayList<>();
            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collectionGroup("registeredUsers").whereEqualTo("username", mUser.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        mUserr.add(snapshot.toObject(UserDetails.class));
                    }
                    int size = mUserr.size();
                    int position;
                    if (size == 1) {
                        position = 0;
                        UserDetails userDetails = mUserr.get(position);
                        section = userDetails.getSection();
                        getIncomingIntent();
                    }

                } else {
                    Toast.makeText(mContext, "No bookings found.", Toast.LENGTH_LONG).show();
                }
            })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("kkk", "Error" + e);
                    });
        } else {
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private void getUserOrders() {
        db.collectionGroup("AllRequests").whereEqualTo("username", mUser.getEmail()).orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOrders = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mAllOrders.add(snapshot.toObject(RequestDetails.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No requests found. Your requests will appear here", Toast.LENGTH_LONG).show();
                    }
                    initRecyclerview();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }
    private void getUserOrders(String date) {
        db.collectionGroup("AllRequests").whereEqualTo("date",date).whereEqualTo("username", mUser.getEmail()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOrders = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mAllOrders.add(snapshot.toObject(RequestDetails.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No requests found for that specific date", Toast.LENGTH_LONG).show();
                        getUserOrders();
                    }
                    initRecyclerview();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    @Override
    public void onItemsClick(RequestDetails mServ, String action) {
        if (section.equals("admin")) {
            if (action.equals("approve")) {
                RequestDetails mRequestDetails = new RequestDetails(mServ.getDate(), mServ.getStartTime(),mServ.getPhoneNum(), mServ.getServiceName(), mServ.getUsername(), mServ.getRequestDate(), mServ.getImage(), "Approved");
                db.collection("RequestsStorage").document(encode(mServ.getDate())).collection("AllRequests").document(mServ.getUsername() + " " + mServ.getServiceName())
                        .set(mRequestDetails)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(), "Request approved successfully", Toast.LENGTH_LONG).show();
                            getAllRequests();

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Not saved. Try again later.", Toast.LENGTH_LONG).show());
            } else if (action.equals("decline")) {
                RequestDetails mRequestDetails = new RequestDetails(mServ.getDate(), mServ.getStartTime(),mServ.getPhoneNum(), mServ.getServiceName(), mServ.getUsername(), mServ.getRequestDate(), mServ.getImage(), "Declined");
                db.collection("RequestsStorage").document(encode(mServ.getDate())).collection("AllRequests").document(mServ.getUsername() + " " + mServ.getServiceName())
                        .set(mRequestDetails)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(), "Request declined successfully", Toast.LENGTH_LONG).show();
                            getAllRequests();

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Not saved. Try again later.", Toast.LENGTH_LONG).show());
            }
        }
    }
    private void getIncomingIntent() {
        Bundle bundle = this.getArguments();
//        Locale locale = new Locale("en","KE");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        if (section.equals("admin")) {
            if(bundle != null){
                String name=bundle.getString("fromAdmin");
                if (name.equals("fromAdmin")){
                    getAllRequests(bundle.getString("dateToLoad"));
                }
            }
            else{
                getAllRequests();
            }
        } else {
            if(bundle != null){
                String name=bundle.getString("fromAdmin");
                if (name.equals("fromAdmin")){
                    getUserOrders(bundle.getString("dateToLoad"));
                }
            }
            else{
                getUserOrders();
            }
        }
    }
}
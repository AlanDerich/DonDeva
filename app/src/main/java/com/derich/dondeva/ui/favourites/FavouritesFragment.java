package com.derich.dondeva.ui.favourites;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.dondeva.Favorites;
import com.derich.dondeva.R;
import com.derich.dondeva.UserDetails;
import com.derich.dondeva.ui.servicedetails.ServiceDetailsFragment;
import com.derich.dondeva.ui.specificservice.SpecificService;
import com.derich.dondeva.ui.specificservice.SpecificServiceAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment implements SpecificServiceAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    SpecificServiceAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<SpecificService> mServices;
    List<Favorites> mFavorites;
    List<String> mFavoriteNames,mFavoriteCategoryNames;
    Context mContext;
    //widgets
    private RecyclerView mRecyclerView;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private SpecificService mServFromAdapter;
    private List<UserDetails> mUserr;
    private String section;
    private ProgressBar progressBar;
    private int pos = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);
        mRecyclerView = root.findViewById(R.id.rv_products_offered);
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar = root.findViewById(R.id.progressBarServices);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mContext= getActivity();
        checkUser();
        registerForContextMenu(mRecyclerView);
//        getServices();
        getFavorites();
        return root;
    }

    private void getFavorites() {
        if (mUser!=null) {
            db.collectionGroup("AllFavorites").whereEqualTo("username", mUser.getEmail()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        mFavorites = new ArrayList<>();
                        mFavoriteNames=new ArrayList<>();
                        mFavoriteCategoryNames=new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                mFavorites.add(document.toObject(Favorites.class));
                            }
                            int k;
                            for (k=0;k<mFavorites.size();k++){
                                mFavoriteNames.add(mFavorites.get(k).getServName());
                                mFavoriteCategoryNames.add(mFavorites.get(k).getMainService());
                            }
                            mServices = new ArrayList<>();
                            getServices(pos);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("SpecificService", "error " + e);
                    });
        }
        else {
            Toast.makeText(mContext, "This section is only available for logged in customers.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getServices(int k){
        String mmm=mFavoriteCategoryNames.get(k);
        String kkk=mFavoriteNames.get(k);
            db.collection(mmm+ " Products").whereEqualTo("ssName",kkk)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                mServices.add(document.toObject(SpecificService.class));
                            }
                        } else {
                            Toast.makeText(mContext, "No products found", Toast.LENGTH_SHORT).show();
                        }
                        ++pos;
                        if (pos>=mFavoriteNames.size()){
                            progressBar.setVisibility(View.GONE);
                            initRecyclerView();
                        }
                        else {
                            getServices(pos);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("SpecificService", "error " + e);
                    });

        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
    }

    private void initRecyclerView(){
        mAdapter = new SpecificServiceAdapter(mServices,this,mFavoriteNames);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
//
//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View view, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(contextMenu, view, menuInfo);
//        contextMenu.setHeaderTitle("Select an action");
//        contextMenu.add(Menu.NONE, 5, 5, "View");
//        contextMenu.add(Menu.NONE, 6, 6, "Update");
//        contextMenu.add(Menu.NONE, 7, 7, "Delete");
//
//    }
//
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case 5:
//                startViewFragment();
//                break;
//
//            case 6:
//                //Do stuff
//                showUpdateDialog(mServFromAdapter);
//                break;
//            case 7:
//                //Do stuff
//                deleteItem(mServFromAdapter);
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }

    private void startViewFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new ServiceDetailsFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("mainServiceName",mServFromAdapter.getSsMainName());
        args.putString("serviceName",mServFromAdapter.getSsName());
        args.putString("servicePic",mServFromAdapter.getSsPic());
        args.putString("section",section);
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    @Override
    public void onItemsClick(SpecificService mServ) {
//        view.showContextMenu();
        mServFromAdapter = mServ;
        startViewFragment();
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
                    }

                } else {
                    String username =mUser.getEmail();
                    section = "simpleUser";
                    UserDetails newUser = new UserDetails(username,section);
                    Toast.makeText(mContext,"No data found.",Toast.LENGTH_LONG).show();
                    db.collection("users").document("all users").collection("registeredUsers").document(mUser.getEmail())
                            .set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(mContext,"User added successfully",Toast.LENGTH_LONG).show();
                                section="simpleUser";
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());

                }
            })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("LoginAct","Error" + e);
                    });
        }
    }
}
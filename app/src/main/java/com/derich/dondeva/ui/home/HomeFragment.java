package com.derich.dondeva.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.derich.dondeva.OfferDetails;
import com.derich.dondeva.ProductPagerAdapter;
import com.derich.dondeva.R;
import com.derich.dondeva.UserDetails;
import com.derich.dondeva.ViewProductFragment;
import com.derich.dondeva.ui.specificservice.SpecificServiceFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment implements ServicesOfferedAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    ServicesOfferedAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Services> mServices;
    Context mContext;
    private ViewPager mProductContainer;
    private TabLayout mTabLayout;
    MaterialEditText edtName;
    Button btnUpload, btnSelect;
    //widgets
    private RecyclerView mRecyclerView;
    private Services mNewService;
    private FloatingActionButton fabAdd;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserDetails> mUserr;
    private String plotIMage;
    private Services mServFromAdapter;
    private Services mReplacingService;
    private String section;
    private ProgressBar progressBar;
    private List<OfferDetails> mAllOffers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.rv_services_offered);
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar = root.findViewById(R.id.progressBarServices);
        fabAdd=root.findViewById(R.id.fabAddService);
        FloatingActionButton fabAddOffer = root.findViewById(R.id.fabAddOffer);
        mProductContainer = root.findViewById(R.id.product_container);
        mTabLayout = root.findViewById(R.id.tab_layout);
        fabAdd.setVisibility(View.GONE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(view -> showDialog());
        mContext= getActivity();
        checkUser();
        registerForContextMenu(mRecyclerView);
        getAllOffers();
        getServices();
        fabAddOffer.setOnClickListener(view -> addOffer());
        return root;
    }

    private void addOffer() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Offer");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_service_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtServiceName);
        edtName.setHint("Offer Name");
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> uploadImage());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(plotIMage !=  null)
            {
                OfferDetails mNewOffer = new OfferDetails(plotIMage,edtName.getText().toString(),"All Are Welcome");
                db.collection("AllOffers").document(mNewOffer.getOfferName())
                        .set(mNewOffer)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(),"Offer saved successfully",Toast.LENGTH_LONG).show();
                            //initRecyclerView();
                            getAllOffers();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
            }
            else {
                Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }

    private void getAllOffers() {
        db.collectionGroup("AllOffers").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOffers = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            mAllOffers.add(snapshot.toObject(OfferDetails.class));
                        }
                    }
                    initPagerAdapter();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }

    private void initPagerAdapter(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        for(OfferDetails product: mAllOffers){
            ViewProductFragment viewProductFragment = new ViewProductFragment(product);
            fragments.add(viewProductFragment);
        }
        ProductPagerAdapter mPagerAdapter = new ProductPagerAdapter(getParentFragmentManager(), fragments);
        mProductContainer.setAdapter(mPagerAdapter);
//        mTabLayout.setupWithViewPager(mProductContainer,
//                true);
    }
    private void getServices(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllServices").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mServices = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            mServices.add(snapshot.toObject(Services.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No services found. Please add a new service", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    initRecyclerView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.d("HomeFragment","Error " + e);
                });
    }

    private void initRecyclerView(){
        mAdapter = new ServicesOfferedAdapter(mServices,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Service");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_service_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtServiceName);
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> uploadImage());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(plotIMage !=  null)
            {
                mNewService = new Services();
                mNewService.setServiceName(edtName.getText().toString());
                mNewService.setServicePic(plotIMage);
                db.collection("AllServices").document(mNewService.getServiceName())
                        .set(mNewService)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(),"Service saved successfully",Toast.LENGTH_LONG).show();
                            //initRecyclerView();
                            getServices();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
            }
            else {
                Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
        alertDialog.show();

    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        mDialog.dismiss();
                        Toast.makeText(mContext,"Image Uploaded!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> plotIMage = uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        mDialog.dismiss();
                        Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploaded: "+progress+"%");
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText(R.string.image_selected);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View view, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo);
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(Menu.NONE, 1, 1, "View");
        contextMenu.add(Menu.NONE, 2, 2, "Update");
        contextMenu.add(Menu.NONE, 3, 3, "Delete");

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
               startViewFragment();
                break;

            case 2:
                //Do stuff
                showUpdateDialog();
                break;
            case 3:
                //Do stuff
                deleteItem(mServFromAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void startViewFragment() {
        unregisterForContextMenu(mRecyclerView);
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new SpecificServiceFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("serviceName",mServFromAdapter.getServiceName());
        args.putString("servicePic",mServFromAdapter.getServicePic());
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    @Override
    public void onItemsClick(Services mServ) {
//        view.showContextMenu();
        mServFromAdapter = mServ;
        if (mUser!=null && section.equals("admin")){
            getView().setOnCreateContextMenuListener(this);
            getView().showContextMenu();
        }
        else {
            startViewFragment();
        }
    }
    private void deleteItem(Services servTodelete) {
        db.collection("AllServices").document(servTodelete.getServiceName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                    mServices.remove(mServFromAdapter);
                    mAdapter.notifyDataSetChanged();
                    deleteImage(servTodelete);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    private void deleteImage(Services mDeleteServ) {
        FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
        final StorageReference imageFolder = mFirebaseStorage.getReferenceFromUrl(mDeleteServ.servicePic);
        imageFolder.delete().addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Image successfully deleted!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(mContext, "Failed! "+ e, Toast.LENGTH_LONG).show());
    }

    private void showUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Current Service");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_menu_layout = inflater.inflate(R.layout.add_new_service_layout,null);


        MaterialEditText edtServiceName;
        edtServiceName = add_menu_layout.findViewById(R.id.edtServiceName);
        edtServiceName.setText(mServFromAdapter.getServiceName());
        plotIMage=mServFromAdapter.getServicePic();

        Button btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        Button btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setOnClickListener(view -> chooseImage());
        btnUpload.setOnClickListener(view -> uploadImage());
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(plotIMage !=  null)
            {
                mReplacingService = new Services(edtServiceName.getText().toString(),plotIMage);
                deleteService(mServFromAdapter,mReplacingService);
            }
            else {
                Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }
    private void deleteService(Services servTodelete,Services replacingService) {
        db.collection("AllServices").document(servTodelete.getServiceName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    mNewService = replacingService;
                    db.collection("AllServices").document(mNewService.getServiceName())
                            .set(mNewService)
                            .addOnSuccessListener(aVoid1 -> {

//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Service updated successfully",Toast.LENGTH_LONG).show();
                                    //initRecyclerView();
                                    getServices();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
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
//                            Toast.makeText(mContext,"Admin Login",Toast.LENGTH_LONG).show();
                        fabAdd.setVisibility(View.VISIBLE);
                    }
                    else if (section.equals("simpleUser")){
                        fabAdd.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(mContext,"Error validating details. Please login again",Toast.LENGTH_LONG).show();
                    }
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
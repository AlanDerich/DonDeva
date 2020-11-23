package com.derich.dondeva.ui.specificservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.derich.dondeva.R;
import com.derich.dondeva.UserDetails;
import com.derich.dondeva.ui.servicedetails.ServiceDetailsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class SpecificServiceFragment extends Fragment implements SpecificServiceAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    SpecificServiceAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<SpecificService> mServices;
    Context mContext;
    MaterialEditText edtName;
    Button btnUpload, btnSelect;
    //widgets
    private RecyclerView mRecyclerView;
    private SpecificService mNewService;
    private FloatingActionButton fabAdd;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String plotIMage;
    private SpecificService mServFromAdapter;
    private SpecificService mReplacingService;
    private List<UserDetails> mUserr;
    private String section;
    private ProgressBar progressBar;
    private String serviceName;
    private MaterialEditText edtServiceRequirements,edtRequirementsPrice,edtServiceHours,edtServiceMinutes,edtServiceFee;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_specific_service, container, false);
        mRecyclerView = root.findViewById(R.id.rv_products_offered);
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar = root.findViewById(R.id.progressBarServices);
        fabAdd=root.findViewById(R.id.fabAddService);
        fabAdd.setVisibility(View.GONE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(view -> showDialog());
        mContext= getActivity();
        getIncomingIntent();
        checkUser();
        registerForContextMenu(mRecyclerView);
        getServices();
        return root;
    }

    private void getIncomingIntent() {
        if (getArguments().getString("serviceName")!=null) {
            serviceName = getArguments().getString("serviceName");
        }
    }

    private void getServices(){
        db.collection(serviceName+ " Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mServices = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                mServices.add(document.toObject(SpecificService.class));
                            }
                        } else {
                            Toast.makeText(mContext, "No products found", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        initRecyclerView();
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                Log.w("SpecificService", "error " + e);
            }
        });
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
    }

    private void initRecyclerView(){
        mAdapter = new SpecificServiceAdapter(mServices,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new product");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_product_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtServiceName);
        edtServiceRequirements = add_menu_layout.findViewById(R.id.edtServiceRequirements);
        edtRequirementsPrice = add_menu_layout.findViewById(R.id.edtRequirementsPrice);
        edtServiceHours = add_menu_layout.findViewById(R.id.edtServiceHour);
        edtServiceMinutes = add_menu_layout.findViewById(R.id.edtServiceMinute);
        edtServiceFee = add_menu_layout.findViewById(R.id.edtServiceFee);
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(plotIMage !=  null)
            {
                mNewService = new SpecificService();
                mNewService.setSsName(edtName.getText().toString());
                mNewService.setSsPic(plotIMage);
                mNewService.setSsRequirements(edtServiceRequirements.getText().toString());
                mNewService.setSsRequirementsPrice(edtRequirementsPrice.getText().toString());
                mNewService.setSsServiceHours(edtServiceHours.getText().toString());
                mNewService.setSsServiceMinutes(edtServiceMinutes.getText().toString());
                mNewService.setSsServiceFee(edtServiceFee.getText().toString());
                db.collection(serviceName+ " Products").document(mNewService.getSsName())
                        .set(mNewService)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(),"Product saved successfully",Toast.LENGTH_LONG).show();
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

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
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
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                plotIMage = uri.toString();

                            }
                        });
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
            btnSelect.setText("Image Selected");
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
        contextMenu.add(Menu.NONE, 5, 5, "View");
        contextMenu.add(Menu.NONE, 6, 6, "Update");
        contextMenu.add(Menu.NONE, 7, 7, "Delete");

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 5:
                startViewFragment();
                break;

            case 6:
                //Do stuff
                showUpdateDialog(mServFromAdapter);
                break;
            case 7:
                //Do stuff
                deleteItem(mServFromAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void startViewFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new ServiceDetailsFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("mainServiceName",serviceName);
        args.putString("serviceName",mServFromAdapter.getSsName());
        args.putString("servicePic",mServFromAdapter.getSsPic());
        args.putString("serviceRequirements",mServFromAdapter.getSsRequirements());
        args.putString("serviceRequirementsPrice",mServFromAdapter.getSsRequirementsPrice());
        args.putString("serviceFee",mServFromAdapter.getSsServiceFee());
        args.putString("serviceHours",mServFromAdapter.getSsServiceHours());
        args.putString("serviceMinutes",mServFromAdapter.getSsServiceMinutes());
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    @Override
    public void onItemsClick(SpecificService mServ) {
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
    private void deleteItem(SpecificService servTodelete) {
        db.collection(serviceName+ " Products").document(servTodelete.getSsName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mServices.remove(mServFromAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void showUpdateDialog(final SpecificService mServ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new product");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_product_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtServiceName);
        edtName.setText(mServ.getSsName());
        edtServiceRequirements = add_menu_layout.findViewById(R.id.edtServiceRequirements);
        edtServiceRequirements.setText(mServ.getSsRequirements());
        edtRequirementsPrice = add_menu_layout.findViewById(R.id.edtRequirementsPrice);
        edtRequirementsPrice.setText(mServ.getSsRequirementsPrice());
        edtServiceHours = add_menu_layout.findViewById(R.id.edtServiceHour);
        edtServiceHours.setText(mServ.getSsServiceHours());
        edtServiceMinutes = add_menu_layout.findViewById(R.id.edtServiceMinute);
        edtServiceMinutes.setText(mServ.getSsServiceMinutes());
        edtServiceFee = add_menu_layout.findViewById(R.id.edtServiceFee);
        edtServiceFee.setText(mServ.getSsServiceFee());
        plotIMage=mServ.ssPic;
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setText("IMAGE SELECTED");
        btnSelect.setOnClickListener(view -> chooseImage());
        btnUpload.setOnClickListener(view -> uploadImage());
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.don_deva);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(plotIMage !=  null)
                {
                    mReplacingService = new SpecificService(edtName.getText().toString(),plotIMage,edtServiceRequirements.getText().toString(),edtRequirementsPrice.getText().toString(),edtServiceHours.getText().toString(),edtServiceMinutes.getText().toString(),edtServiceFee.getText().toString());
                    deleteService(mServFromAdapter,mReplacingService);
                }
                else {
                    Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void deleteService(SpecificService servTodelete,SpecificService replacingService) {
        db.collection(serviceName+ " Products").document(servTodelete.getSsName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    mNewService = replacingService;
                    db.collection(serviceName+ " Products").document(servTodelete.getSsName())
                            .set(mNewService)
                            .addOnSuccessListener(aVoid1 -> {

//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(getContext(),"Product updated successfully",Toast.LENGTH_LONG).show();
                                //initRecyclerView();
                                getServices();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    private void checkUser() {
        if (mUser!=null){
            mUserr= new ArrayList<>();
            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collectionGroup("registeredUsers").whereEqualTo("username",mUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mUserr.add(snapshot.toObject(UserDetails.class));
                        }
                        int size = mUserr.size();
                        int position;
                        if (size==1){
                            position=0;
                            UserDetails userDetails= mUserr.get(position);
                            String namee=userDetails.getUsername();
                            section = userDetails.getSection();
                            if (section.equals("admin")){
//                            Toast.makeText(mContext,"Admin Login",Toast.LENGTH_LONG).show();
                                fabAdd.setVisibility(View.VISIBLE);
                            }
                            else if (section.equals("simpleUser")){
//                            Toast.makeText(mContext,"Normal user Login",Toast.LENGTH_LONG).show();
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
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext,"User added successfully",Toast.LENGTH_LONG).show();
                                        section="simpleUser";
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            Log.d("LoginAct","Error" + e);
                        }
                    });
        }
    }
}
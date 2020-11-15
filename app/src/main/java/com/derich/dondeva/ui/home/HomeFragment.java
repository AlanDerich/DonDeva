package com.derich.dondeva.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.dondeva.R;
import com.derich.dondeva.ui.requests.RequestsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    List<String> cats = new ArrayList<>();
    Context mContext;
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
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String plotIMage;
    private Services mServFromAdapter;
    private Services mReplacingService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.rv_services_offered);
        mRecyclerView.setVisibility(View.INVISIBLE);
        fabAdd=root.findViewById(R.id.fabAddService);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(view -> showDialog());
        mContext= getActivity();
        registerForContextMenu(mRecyclerView);
        getServices();
        return root;
    }
    private void getServices(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllServices").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mServices = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mServices.add(snapshot.toObject(Services.class));
                        } else {
                            Toast.makeText(mContext, "No services found. Please add a new service", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("HomeFragment","Error " + e);
                    }
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
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_service_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtServiceName);
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
        alertDialog.setIcon(R.drawable.imgg);
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
        contextMenu.add(Menu.NONE, 1, 1, "View");
        contextMenu.add(Menu.NONE, 2, 2, "Update");
        contextMenu.add(Menu.NONE, 3, 3, "Delete");

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Bundle args = new Bundle();
                AppCompatActivity activity = (AppCompatActivity) mContext;
                Fragment fragmentStaff = new RequestsFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                args.putString("serviceName",mServFromAdapter.getServiceName());
                args.putString("servicePic",mServFromAdapter.getServicePic());
                fragmentStaff.setArguments(args);
                transactionStaff.commit();
                break;

            case 2:
                //Do stuff
                showUpdateDialog(mServFromAdapter);
                break;
            case 3:
                //Do stuff
                deleteItem(mServFromAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemsClick(Services mServ) {
//        view.showContextMenu();
        mServFromAdapter = mServ;
        getView().setOnCreateContextMenuListener(this);
        getView().showContextMenu();
    }
    private void deleteItem(Services servTodelete) {
        db.collection("AllServices").document(servTodelete.getServiceName())
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

    private void showUpdateDialog(final Services mServ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Current Service");
        alertDialog.setMessage("Fill all the details.");

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
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(plotIMage !=  null)
                {
                    mReplacingService = new Services(edtServiceName.getText().toString(),plotIMage);
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}
package com.derich.dondeva.ui.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.derich.dondeva.LoginActivity;
import com.derich.dondeva.MainActivity;
import com.derich.dondeva.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {

    private FirebaseUser mUser;
    private TextView tvName,tvEmail,Verify,tvPassword;
    private ImageView imgProfile;
    private String m_text;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Context mContext;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvName = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        Verify = view.findViewById(R.id.tvVerify);
        imgProfile = view.findViewById(R.id.imageView_prof_pic);
        tvPassword = view.findViewById(R.id.tvPassword);
        Button logout = view.findViewById(R.id.buttonLogout);
        // Inflate the layout for this fragment
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext = getContext();
        checkLogIn();
        logout.setOnClickListener(view1 -> logOut());
        return view;
    }

    private void checkLogIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            refresh();
        } else {
            Intent intentLogin= new Intent(mContext,LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    private void refresh() {
        if (mUser!=null){
            String name = mUser.getDisplayName();
            String email = mUser.getEmail();
            Uri photoUrl = mUser.getPhotoUrl();
            boolean emailVerified = mUser.isEmailVerified();
            if (emailVerified){
                Verify.setText(R.string.verified);
            }
            else {

                Verify.setOnClickListener(view -> {
                    if (mUser.getEmail().isEmpty()){
                        Toast.makeText(getContext(),"Please add an email address to continue",Toast.LENGTH_LONG).show();
                    }
                    else {
                        mUser.sendEmailVerification()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Verification Email sent. Please verify and login again to continue",Toast.LENGTH_SHORT).show();
                                        logOut();

                                    }
                                    else {
                                        Toast.makeText(getContext(), "Email not sent. Try again later.",Toast.LENGTH_SHORT).show();
                                    }
                                });}
                });
            }
            //String uid = mUser.getUid();
            if (name!=null){
                tvName.setText(name);}
            else {
                tvName.setText("Click here to set nickname");
            }
            if (email!=null){
                if (!email.equals(""))
                {
                    tvEmail.setText(email);}
            }
            // UID.setText(uid);
            if (photoUrl != null && !Uri.EMPTY.equals(photoUrl)){
                Glide.with(this).load(photoUrl).into(imgProfile);
            }
            imgProfile.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Alert!");
                final TextView tvChange = new TextView(getContext());
                tvChange.setTextSize(22);
                tvChange.setText(R.string.change_profile_picture);
                tvChange.setOnClickListener(v -> chooseImage());
                builder.setView(tvChange);
                builder.show();

            });
            tvName.setOnClickListener(view -> EditNameInfo());
            tvEmail.setOnClickListener(view -> EditEmail());
            tvPassword.setOnClickListener(view -> {
                m_text = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Email Address");

// Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setText(mUser.getEmail());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", (dialog, which) -> {
                    m_text = input.getText().toString();
                    mUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if (!(mUser.getEmail().isEmpty())){
                        auth.sendPasswordResetEmail(mUser.getEmail())


                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(),"Reset password email sent. Tap on the email to verify and then login",Toast.LENGTH_SHORT).show();
                                        signOut();
                                    }
                                    else {
                                        Toast.makeText(getContext(),"Sorry an error occured.",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                    else {
                        Toast.makeText(getContext(),"No email was inserted",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            });

        }
    }

    private void logOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(task -> startActivity(new Intent(getContext(), LoginActivity.class)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(filePath)
                        .build();

                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(),"Profile pic updated successfully",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),"Sorry an error occured",Toast.LENGTH_SHORT).show();
                            }
                            refresh();
                        });
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void EditEmail() {
        m_text = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Email");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            m_text = input.getText().toString();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (!(m_text.isEmpty())) {
                mUser.updateEmail(m_text)

                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Session expired. Please log out and login again to change email.", Toast.LENGTH_SHORT).show();
                            }
                            refresh();
                        });
            }
            else {
                Toast.makeText(getContext(), "No email inserted.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void EditNameInfo(){
        m_text = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Username");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            m_text = input.getText().toString();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (!(m_text.isEmpty())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(m_text)
                        .build();
                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                refresh();
                                Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Session expired. Please log out and login again to change username.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Toast.makeText(getContext(),"Username cannot be empty",Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            getProfPic();

        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(getContext(),"The permission only allows for image uploading",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
        }
        else  if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(getContext(),"Oops! The required permission was denied.Go to settings and enable it to upload your profile picture",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );

        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
        }
    }

    private void getProfPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getProfPic();
            }
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(task -> {
                    Intent intent= new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                });
    }
}
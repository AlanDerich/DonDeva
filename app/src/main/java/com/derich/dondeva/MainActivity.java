package com.derich.dondeva;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.derich.dondeva.ui.help.HelpFragment;
import com.derich.dondeva.ui.requests.RequestsFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserDetails> mUserr;
    private List<RequestDetails> mAllOrders;
    private String section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests, R.id.nav_favourites, R.id.nav_help, R.id.nav_account,R.id.nav_specific_service,R.id.nav_calendar)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        checkUser();

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
                            checkIfAdmin();
                        }

                    } else {
                        Toast.makeText(this, "No users found in db.", Toast.LENGTH_LONG).show();
                    }
                })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                            Log.d("kkk", "Error" + e);
                        });
            } else {
//                Intent loginIntent = new Intent(this, LoginActivity.class);
//                startActivity(loginIntent);
            }

    }

    private void checkIfAdmin() {
        if (section.equals("admin")) {
            db.collectionGroup("AllRequests").whereEqualTo("status","pending").orderBy("date", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        mAllOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                mAllOrders.add(snapshot.toObject(RequestDetails.class));
                            }
                            BadgeDrawable badgeDrawable= navView.getOrCreateBadge(R.id.nav_requests);
                            badgeDrawable.setNumber(mAllOrders.size());
                        }
//                        else {
////                            Toast.makeText(this, "No requests found. Your requests will appear here", Toast.LENGTH_LONG).show();
//                        }

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("HouseInfo", "error " + e);
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //            case R.id.action_logout:
        //                if (mUser!=null){
        //                    signOut();
        //                }
        //                else {
        //                    Toast.makeText(MainActivity.this, "You are not logged in!", Toast.LENGTH_LONG).show();
        //                }
        //                break;
        if (item.getItemId() == R.id.action_requests) {
            Bundle args = new Bundle();
            AppCompatActivity activity = this;
            Fragment fragmentStaff = new HelpFragment();
            FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
            transactionStaff.replace(R.id.nav_host_fragment, fragmentStaff);
            transactionStaff.addToBackStack(null);
            fragmentStaff.setArguments(args);
            transactionStaff.commit();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
//    public void signOut() {
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(task -> Toast.makeText(MainActivity.this, "Logged out successfully!", Toast.LENGTH_LONG).show());
//    }
}
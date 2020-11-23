package com.derich.dondeva;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.derich.dondeva.ui.requests.RequestsFragment;
import com.derich.dondeva.ui.specificservice.SpecificServiceFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar mTopToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests, R.id.nav_favourites, R.id.nav_help, R.id.nav_account,R.id.nav_specific_service)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                if (mUser!=null){
                    signOut();
                }
                else {
                    Toast.makeText(MainActivity.this, "You are not logged in!", Toast.LENGTH_LONG).show();
                }
                break;
                case R.id.action_requests:
                    Bundle args = new Bundle();
                    AppCompatActivity activity = (AppCompatActivity) this;
                    Fragment fragmentStaff = new RequestsFragment();
                    FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                    transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                    transactionStaff.addToBackStack(null);
                    fragmentStaff.setArguments(args);
                    transactionStaff.commit();
                    break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> Toast.makeText(MainActivity.this, "Logged out successfully!", Toast.LENGTH_LONG).show());
    }
}
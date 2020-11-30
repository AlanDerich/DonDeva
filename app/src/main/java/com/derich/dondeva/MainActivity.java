package com.derich.dondeva;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.derich.dondeva.ui.requests.RequestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
            Fragment fragmentStaff = new RequestsFragment();
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
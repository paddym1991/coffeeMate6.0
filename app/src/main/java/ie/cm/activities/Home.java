package ie.cm.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

import ie.cm.R;
import ie.cm.api.CoffeeApi;
import ie.cm.api.VolleyListener;
import ie.cm.fragments.AddFragment;
import ie.cm.fragments.CoffeeFragment;
import ie.cm.fragments.EditFragment;
import ie.cm.fragments.HelpFragment;
import ie.cm.fragments.SearchFragment;
import ie.cm.main.CoffeeMateApp;
import ie.cm.models.Coffee;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EditFragment.OnFragmentInteractionListener /*,
        VolleyListener*/ {

    public CoffeeMateApp app = CoffeeMateApp.getInstance();
    private ImageView googlePhoto;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Information", Snackbar.LENGTH_LONG)
                        .setAction("More Info...", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openInfoDialog(Home.this);
                            }
                        }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SetUp GooglePhoto and Email for Drawer here
        googlePhoto = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.googlephoto);
        CoffeeApi.getGooglePhoto(app.googlePhotoURL,googlePhoto);

        TextView googleName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.googlename);
        googleName.setText(app.googleName);

        TextView googleMail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.googlemail);
        googleMail.setText(app.googleMail);

        dialog = new ProgressDialog(this);
        CoffeeApi.attachDialog(dialog);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        CoffeeFragment fragment = CoffeeFragment.newInstance();
        ft.replace(R.id.homeFrame, fragment);
        ft.commit();

    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        googlePhoto.setImageBitmap(app.googlePhoto);
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        // http://stackoverflow.com/questions/32944798/switch-between-fragments-with-onnavigationitemselected-in-new-navigation-drawer

        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (id == R.id.nav_home) {
            fragment = CoffeeFragment.newInstance();
            ((CoffeeFragment)fragment).favourites = false;
            ft.replace(R.id.homeFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_add) {
            fragment = AddFragment.newInstance();
            ft.replace(R.id.homeFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_favourites) {
            fragment = CoffeeFragment.newInstance();
            ((CoffeeFragment)fragment).favourites = true;
            ft.replace(R.id.homeFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_search) {
            fragment = SearchFragment.newInstance();
            ((SearchFragment)fragment).favourites = false;
            ft.replace(R.id.homeFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_camera) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void toggle(View v) {
        EditFragment editFrag = (EditFragment) getFragmentManager().findFragmentById(R.id.homeFrame);
        if (editFrag != null) {
            editFrag.toggle(v);
        }
    }

    @Override
    public void update(View v) {
        EditFragment editFrag = (EditFragment) getFragmentManager().findFragmentById(R.id.homeFrame);
        if (editFrag != null) {
            editFrag.update(v);
        }
    }


//    @Override
//    public void setList(List list) {
//        app.coffeeList = list;
//    }
//
//    @Override
//    public void setCoffee(Coffee c) {
//
//    }

    public void openInfoDialog(Activity current) {
        Dialog dialog = new Dialog(current);
        dialog.setTitle("About CoffeeMate");
        dialog.setContentView(R.layout.info);

        TextView currentVersion = (TextView) dialog
                .findViewById(R.id.versionTextView);
        currentVersion.setText("1.0.0");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void menuInfo(MenuItem m)
    {
        openInfoDialog(this);
    }

    public void menuHelp(MenuItem m)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = HelpFragment.newInstance();
        ft.replace(R.id.homeFrame, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void menuHome(MenuItem m)
    {
        startActivity(new Intent(this, Home.class));
    }

    // [START signOut]

    public void menuSignOut(MenuItem m) {

        //https://stackoverflow.com/questions/38039320/googleapiclient-is-not-connected-yet-on-logout-when-using-firebase-auth-with-g
        app.mGoogleApiClient.connect();
        app.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                //FirebaseAuth.getInstance().signOut();
                if(app.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(app.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.v("coffeemate", "User Logged out");
                                Intent intent = new Intent(Home.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("coffeemate", "Google API Client Connection Suspended");
            }
        });
    }
    // [END signOut]
}

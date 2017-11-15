package ie.cm.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import ie.cm.R;
import ie.cm.main.CoffeeMateApp;


public class Login extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        OnClickListener {

    public CoffeeMateApp app = CoffeeMateApp.getInstance();

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "coffeemate";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        app.mGoogleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by mGoogleSignInOptions.
        app.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, app.mGoogleSignInOptions)
                .build();
        // [END build_client]

        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

//        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION,0);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(app.mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            app.googleName = acct.getDisplayName();

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
//            personPhotoUrl = personPhotoUrl.substring(0,
//                    personPhotoUrl.length() - 2)
//                    + 100;

            app.googleToken = acct.getId();
            app.signedIn = true;
            app.googleMail = acct.getEmail();
            if(acct.getPhotoUrl() == null)
                ; //New Account may not have Google+ photo
            else app.googlePhotoURL = acct.getPhotoUrl().toString();

            // Show a message to the user that we are signing in.
            Toast.makeText(this, "Signing in " + app.googleName +" with " + app.googleMail , Toast.LENGTH_SHORT).show();
            startHomeScreen();
        } else
            Toast.makeText(this, "Please Sign in " , Toast.LENGTH_SHORT).show();
    }
    // [END handleSignInResult]


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            signIn();
        }
        else
        if (v.getId() == R.id.disconnect_button) {
            revokeAccess();
        }
        //  else
        //      Toast.makeText(this, "No Account to Disconenct....", Toast.LENGTH_SHORT).show();
    }

    private void startHomeScreen() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void startLoginScreen() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(app.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(app.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        startLoginScreen();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error Signing in to Google " + connectionResult, Toast.LENGTH_LONG).show();
        Log.v(TAG, "ConnectionResult : " + connectionResult);
    }

//    private void loadPermissions(String perm,int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
//                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 123: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // granted
//                }
//                else{
//                    // no granted
//                }
//                return;
//            }
//
//        }
//
//    }
}

package com.example.financialassistance;

/* Matteo Caruso Main Activity */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 100;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Log.d("MainActivity", currentUser.getDisplayName());
//            getAccessToken(currentUser);
//        }
//        updateUI(currentUser);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        NetworkManager.sharedInstance.getAccessCode(new NetworkResponseCallback() {
//            @Override
//            public void success(JSONObject json) {
////                NetworkManager.sharedInstance.
//            }
//
//            @Override
//            public void failure() {
//
//            }
//        });

        // 401640767936-ums4tnqsi6083ij25f55lj1stu70l2ll.apps.googleusercontent.com

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope("https://www.googleapis.com/auth/cloud-platform"), new Scope("https://www.googleapis.com/auth/dialogflow"))
                .requestIdToken("401640767936-ums4tnqsi6083ij25f55lj1stu70l2ll.apps.googleusercontent.com")
                .requestServerAuthCode("401640767936-ums4tnqsi6083ij25f55lj1stu70l2ll.apps.googleusercontent.com")
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        /* create button object with its id being the enter button on login */


        Button enterButton = (Button) findViewById(R.id.enterButton);

        /* listen for button input */

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
//                EditText userName = (EditText) findViewById(R.id.email);
//                EditText password = (EditText) findViewById(R.id.newPassword);
//
//                /* check if login is correct */
//
//                correctLogin(userName.getText().toString(), password.getText().toString());

                /* if function did not initiate new activity, the info was invalid
                * and this method terminates */


            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult();
                NetworkManager.sharedInstance.setAuthCode(account.getServerAuthCode());
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Log.d("MainActivity", e.getLocalizedMessage());
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getAccessToken(user);
                            Log.d("MainActivity", user.getDisplayName());
//                            updateUI(user);
                        } else {
                            Log.d("MainActivity", "Failed to authenticate");
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    public void getAccessToken(FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> tokenResultTask) {
                String token = tokenResultTask.getResult().getToken();
                NetworkManager.sharedInstance.setIdToken(token);
                NetworkManager.sharedInstance.storeAccessToken(new NetworkResponseCallback() {
                    @Override
                    public void success(JSONObject json) {
                        Intent intent = new Intent(MainActivity.this, PushToTalkActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });
    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /* method to check for correct credentials */

    private void correctLogin(String name, String password){

        if ((name.equals("matteocaruso")) && (password.equals("123456"))){

            /* if the login is correct, move to voice activity */

            Intent intent = new Intent(MainActivity.this, PushToTalkActivity.class);
            startActivity(intent);
        }





    }

}

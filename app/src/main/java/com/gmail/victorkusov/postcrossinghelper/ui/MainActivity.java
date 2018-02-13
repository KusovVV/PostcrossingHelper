package com.gmail.victorkusov.postcrossinghelper.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LOG MainActivity";
    private static final int RC_SIGN_IN = 101;
    public static final Pattern EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final int SCREEN_CHANGE_DURATION = 3000;


    private TextInputLayout mEmail, mPassword;
    private ViewFlipper mFlipper;
    private LoginButton loginButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;


    // TODO MAKE A FIREBASE GOOGLE SIGN IN  merge fireBase sign with credential
    //TODO add app activity with find field and try to configure screens
    // FIXME codereview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [FireBase instance]
        mFirebaseAuth = FirebaseAuth.getInstance();

        // [Setup Flipper]
        mFlipper = findViewById(R.id.main_layout_flipper);
        setupFlipper();


        // [Setup Views]
        mEmail = findViewById(R.id.sign_container_email);
        mPassword = findViewById(R.id.sign_container_password);
        mProgressBar = findViewById(R.id.sign_progress);

        (findViewById(R.id.sign_btn_email)).setOnClickListener(this);
        (findViewById(R.id.sign_btn_register)).setOnClickListener(this);

        //FIXME TEST. just for signing out
        (findViewById(R.id.btn_sign_out)).setOnClickListener(this);

        loginButton = findViewById(R.id.sign_btn_facebook);
        setupFacebookButton();

        SignInButton signInButton = findViewById(R.id.sign_btn_google);
        setupGoogleButton(signInButton);

        if (savedInstanceState != null) {
            mEmail.getEditText().setText(savedInstanceState.getString("email"));
            mPassword.getEditText().setText(savedInstanceState.getString("password"));
            mProgressBar.setVisibility(savedInstanceState.getInt("visibility"));
            mFlipper.setDisplayedChild(savedInstanceState.getInt("layout"));
        }

    }

    private void setupFlipper() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFlipper.addView(inflater.inflate(R.layout.layout_splash_screen, null));
        mFlipper.addView(inflater.inflate(R.layout.layout_login, null));
        mFlipper.addView(inflater.inflate(R.layout.layout_main_menu, null));

        mFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

    }

    private void setupFacebookButton() {
        // [Facebook button setup]
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook signIn: success");
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                loginWithCredential(credential);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook signIn cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook signIn Error");
            }
        });
    }

    private void setupGoogleButton(SignInButton signInButton) {
        // [Google sign in]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();
        mProgressBar.setVisibility(View.VISIBLE);

        switch (view.getId()) {
            case R.id.sign_btn_email: {
                if (validateData(email, password)) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    mFirebaseAuth.signInWithCredential(credential);
                }
                break;
            }
            case R.id.sign_btn_register: {
                createUserWithEmailPassword(email, password);
                break;
            }
            case R.id.sign_btn_google: {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            }
            case R.id.btn_sign_out: {
                // FIXME just for signing out
                mFirebaseAuth.signOut();
                mFlipper.showPrevious();
                updateUI(false);
            }
        }
    }

    private boolean validateData(String email, String password) {
        if (email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Need an email and a password", Toast.LENGTH_SHORT).show();
            updateUI(false);
            return false;
        }
        Matcher matcher = EMAIL_ADDRESS_REGEX.matcher(email);
        if (matcher.find()) {
            Log.d(TAG, "Email validation: email is correct");
            return true;
        }
        Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
        updateUI(false);
        return false;
    }

    private void createUserWithEmailPassword(final String email, final String password) {

        if (validateData(email, password)) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // [Success with login]
                                Log.d(TAG, "Created with custom email");
                                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                                loginWithCredential(credential);
                            } else {
                                // [Some problems with login]
                                Log.d(TAG, "Creation with custom email is failed\n" + task.getException());
                                updateUI(false);
                            }
                        }
                    });
        }
    }

    private void loginWithCredential(AuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String userName;
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    userName = task.getResult().getUser().getDisplayName();
                    if (userName == null) {
                        userName = "Stranger";
                    }
                    Toast.makeText(MainActivity.this, "Welcome " + userName, Toast.LENGTH_SHORT).show();
                    updateUI(true);
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.getException());
                    updateUI(false);
                }
            }
        });
    }

    private void updateUI(boolean success) {

        mProgressBar.setVisibility(View.GONE);

        mEmail.getEditText().setText("");
        mPassword.getEditText().setText("");

        if (success) {
            mFlipper.showNext();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // [Google callback]
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle: success");
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    loginWithCredential(credential);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.d(TAG, "Google sign in failed", e);
                }
                return;
            }

            // [Facebook callback]
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // [Check Auth]
        if (mFlipper.getDisplayedChild() == 0) {
            mFlipper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mFirebaseAuth.getCurrentUser() != null) {
                        Log.d(TAG, "FirebaseAuth: Login is ok");
                        mFlipper.showNext();
                    }
                    mFlipper.showNext();
                }
            }, SCREEN_CHANGE_DURATION);
        } else {
            Log.d(TAG, "FirebaseAuth: Login is failed");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("email", mEmail.getEditText().getText().toString());
        outState.putString("password", mPassword.getEditText().getText().toString());
        outState.putInt("visibility", mProgressBar.getVisibility());
        outState.putInt("layout", mFlipper.getDisplayedChild());
    }
}


package com.gmail.victorkusov.postcrossinghelper.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gmail.victorkusov.postcrossinghelper.utils.MessageDialog;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LOG " + MainActivity.class.getSimpleName();

    private static final long SCREEN_CHANGE_DURATION = TimeUnit.SECONDS.toMillis(3);

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_VISIBILITY = "visibility";
    private static final String KEY_LAYOUT = "layout";
    private static final int RC_SIGN_IN = 101;
    private static final String KEY_USER_NAME = "display_name";

    private EditText mEditEmail;
    private EditText mEditPassword;
    private ViewFlipper mFlipper;
    private LoginButton loginButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private CallbackManager mCallbackManager;
    private SignInButton signInButton;

    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        [FireBase instance]
        // FIXME: 23.02.2018 convert to JAVADOC!
        mFirebaseAuth = FirebaseAuth.getInstance();

        // [Setup Flipper]
        mFlipper = findViewById(R.id.main_layout_flipper);
        setupFlipper();

        // [Setup Views]
        mEditEmail = ((TextInputLayout) findViewById(R.id.sign_container_email)).getEditText();
        mEditPassword = ((TextInputLayout) findViewById(R.id.sign_container_password)).getEditText();
        mProgressBar = findViewById(R.id.sign_progress);

        loginButton = findViewById(R.id.sign_btn_facebook);
        signInButton = findViewById(R.id.sign_btn_google);

        if (savedInstanceState != null) {
            mEditEmail.setText(savedInstanceState.getString(KEY_EMAIL));
            mEditPassword.setText(savedInstanceState.getString(KEY_PASSWORD));
            mProgressBar.setVisibility(savedInstanceState.getInt(KEY_VISIBILITY));
            mFlipper.setDisplayedChild(savedInstanceState.getInt(KEY_LAYOUT));
        }

    }


    private void setupFlipper() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            mFlipper.addView(inflater.inflate(R.layout.layout_splash_screen, null));
            mFlipper.addView(inflater.inflate(R.layout.layout_login, null));

            mFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            mFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        }
    }

    private void useFacebookButton() {
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
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook signIn Error");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void useGoogleButton() {
        // [Google sign in]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        email = mEditEmail.getText().toString();
        password = mEditPassword.getText().toString();

        mProgressBar.setVisibility(View.VISIBLE);

        switch (view.getId()) {
            case R.id.sign_btn_email: {
                if (isDataValid()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    mFirebaseAuth.signInWithCredential(credential);
                }
                mProgressBar.setVisibility(View.GONE);
                break;
            }
            case R.id.sign_btn_register: {
                AlertDialog.Builder dialog;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    dialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    dialog = new AlertDialog.Builder(MainActivity.this, android.R.style.TextAppearance_Theme_Dialog);
                }
                dialog.setTitle(R.string.dialog_confirm_registration);
                dialog.setMessage(String.format(getResources().getString(R.string.dialog_reg_message), email));
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDataValid()) {
                            createUserWithEmailPassword(email, password);
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.sign_btn_google: {
                useGoogleButton();
                break;
            }
            case R.id.sign_btn_facebook: {
                useFacebookButton();
                break;
            }
        }
    }

    private boolean isDataValid() {

        if (!Utils.isEmailAndPasswordValid(email, password)) {
            Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            mEditPassword.setText("");
            return false;
        }
        return true;
    }

    private void createUserWithEmailPassword(final String email, final String password) {

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // [Success with login]
                            Toast.makeText(MainActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            loginWithCredential(credential);
                        } else {
                            // [Some problems with login]
                            Log.d(TAG, "Creation with custom email is failed\n" + task.getException());
                            Toast.makeText(MainActivity.this, "Register failed! Please try again", Toast.LENGTH_SHORT).show();
                            mEditEmail.setText("");
                            mEditPassword.setText("");
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loginWithCredential(AuthCredential credential) {

        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    showWorkspace();
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.getException());
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

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
        }

        // [Facebook callback]
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // [Check Auth]
        if (mFlipper.getDisplayedChild() == 0) {
            mFlipper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Utils.hasNetworkConnection(MainActivity.this)) {
                        if (isSigned()) {
                            showWorkspace();
                        }
                        addListenersToButtons();
                        mFlipper.showNext();
                    } else {
                        showWorkspace();
                    }
                }
            }, SCREEN_CHANGE_DURATION);
        } else {
            addListenersToButtons();
        }
    }

    private void addListenersToButtons() {
        // [add onClick listener to buttons]
        loginButton.setOnClickListener(MainActivity.this);
        signInButton.setOnClickListener(MainActivity.this);
        (findViewById(R.id.sign_btn_email)).setOnClickListener(MainActivity.this);
        (findViewById(R.id.sign_btn_register)).setOnClickListener(MainActivity.this);
    }

    private void showWorkspace() {
        Intent intent = new Intent(MainActivity.this, WorkScreenActivity.class);

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            intent.putExtra(KEY_USER_NAME, displayName);
        }
        startActivity(intent);
        MainActivity.this.finish();
    }

    private boolean isSigned() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            return false;
        }

        String userName = user.getDisplayName();
        if (userName == null) {
            userName = "Stranger";
        }
        Toast.makeText(MainActivity.this, "Welcome " + userName, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "FirebaseAuth: Login is ok");
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(KEY_EMAIL, mEditEmail.getText().toString());
        outState.putString(KEY_PASSWORD, mEditPassword.getText().toString());
        outState.putInt(KEY_VISIBILITY, mProgressBar.getVisibility());
        outState.putInt(KEY_LAYOUT, mFlipper.getDisplayedChild());

        super.onSaveInstanceState(outState);
    }
}


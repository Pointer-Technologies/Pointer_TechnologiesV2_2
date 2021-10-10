package com.example.pointer_technologiesv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private TextView textForgotPassword, textWrongInput, textNotRegistered;
    private EditText editLoginName, editLoginPassword;
    private Button buttonSignIn;
    private MaterialCheckBox checkboxRememberMe;
    private MaterialToolbar signInToolbar;
    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private final int RC_SIGN_IN = 1;
    private DatabaseReference databaseReference;


    // Checks SharedPreferences for light or dark mode and adjusts the toolbar button.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_menu, menu);
        MenuItem checkable = menu.findItem(R.id.nightModeSignInMenu);
        SharedPreferences preferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        checkable.setChecked(preferences.getBoolean("nightMode", false));

        if (preferences.getBoolean("nightMode", false)) {
            checkable.setTitle("LIGHT MODE");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            checkable.setTitle("DARK MODE");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        return true;
    }

    // Changes themes upon pressing the theme button.
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nightModeSignInMenu) {
            item.setChecked(!item.isChecked());
            SharedPreferences preferences_night = getSharedPreferences("nightMode", MODE_PRIVATE);
            SharedPreferences.Editor editor_night = preferences_night.edit();

            if (!preferences_night.getBoolean("nightMode", false)) {
                editor_night.putBoolean("nightMode", true);
                editor_night.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                editor_night.putBoolean("nightMode", false);
                editor_night.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    // Checks shared preferences for theme choice and whether the user asked to stay signed in.
    // If so, navigate them straight to their AppCenterActivity.
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("remember",MODE_PRIVATE);
        boolean staySignedIn = preferences.getBoolean("remember",false);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null && currentUser.isEmailVerified() && staySignedIn) {
            startActivity(new Intent(SignInActivity.this, AppCenterActivity.class));
        } else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            updateUI(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SharedPreferences preferences_night = getSharedPreferences("nightMode", MODE_PRIVATE);
        if (preferences_night.getBoolean("nightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        initializeViews();
        setSupportActionBar(signInToolbar);

        // Enable sign in with Google and set up the default settings.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Pass the "Sign in with Google" functionality to the googleSignInButton.
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        // Enable the "Sign in with Email" button as well.
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    firebaseAuth.signInWithEmailAndPassword(editLoginName.getText().toString(), editLoginPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("", "signInWithEmail:success");
                                    textWrongInput.setVisibility(View.INVISIBLE);

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                                    textWrongInput.setVisibility(View.VISIBLE);
                                    updateUI(null);
                                }
                            }
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                    updateUI(null);
                }
            }
        });

        // Enable the user to register, if they don't have an existing account.
        textNotRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Set the "Forgot password" key to prompt the user to give a backup email, using a Dialog.
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
                v.setTranslationZ(10);
            }
        });

        // The "Remember me" checkbox passes on information to SharedPreferences, to be kept
        // after the application is closed.
        checkboxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (compoundButton.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("remember", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("remember", true);
                editor.apply();
            } else {
                SharedPreferences preferences = getSharedPreferences("remember", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("remember", false);
                editor.apply();
            }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "Successfully signed in.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sign in failed.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }


    private void FirebaseGoogleAuth(final GoogleSignInAccount account) {
        try {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user=new User(account.getDisplayName(),account.getEmail(),account.getIdToken(),account.getPhotoUrl());
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //databaseReference.child(firebaseUser.getUid()).setValue(user);
                        Toast.makeText(SignInActivity.this, "Successful.", Toast.LENGTH_SHORT).show();
                        updateUI(firebaseUser);
                    } else {
                        Toast.makeText(SignInActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.isEmailVerified()) {
                startActivity(new Intent(SignInActivity.this, AppCenterActivity.class));
            }
            else{
                Toast.makeText(this, "You need to click the verification link we send in your email.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleForgotPassword() {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog();
        dialog.show(getSupportFragmentManager(), "forgot password dialog");
    }


    private void initializeViews() {
        textNotRegistered = findViewById(R.id.textNotRegistered);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        textWrongInput = findViewById(R.id.textWrongInput);
        editLoginName = findViewById(R.id.editLoginName);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        checkboxRememberMe = findViewById(R.id.checkBoxRememberMe);
        signInToolbar = findViewById(R.id.signInToolbar);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }
}
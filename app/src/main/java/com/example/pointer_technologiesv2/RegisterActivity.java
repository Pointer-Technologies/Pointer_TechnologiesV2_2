package com.example.pointer_technologiesv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editName,editPassword,editEmail,editReEnterPassword;
    private TextView textCheckLicence;
    private CheckBox checkBox;
    private Button buttonRegister;
    private MaterialToolbar registerToolbar;
    private TextInputLayout editNameLayout,editPasswordLayout,editEmailLayout,editReEnterPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ΑΡΧΙΚΟΠΟΙΗΣΗ ΤΟΥ ACTIVITY
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();

        // Enable the toolbar "back button" and navigate it to the sign in screen.
        registerToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEntries()){

                    firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    // Make sure the user that's been created has been properly passed to the app.
                                    Log.d("", "createUserWithEmail:success");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;

                                    sendEmailVerification();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(editName.getText().toString())
                                        .build();
                                    user.updateProfile(profileUpdates);
                                    Toast.makeText(RegisterActivity.this, "An email has been sent to email address given.", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Log.w("", "createUserWithEmail:failure", task.getException());
                                    editEmailLayout.setError(Objects.requireNonNull(task.getException()).getMessage());
                                }


                            }
                        });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "There are errors.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Checks if the user included a number in the new password.
    private boolean containsNum(String string){
        return(string.contains("0")||
            string.contains("1")||
            string.contains("2")||
            string.contains("3")||
            string.contains("4")||
            string.contains("5")||
            string.contains("6")||
            string.contains("7")||
            string.contains("8")||
            string.contains("9"));
    }


    // Checks if the password meets the security requirements.
    private boolean checkPassword() {
        if(editPassword.getText().toString().length() < 8 || !containsNum(editPassword.getText().toString())) {
            editPasswordLayout.setError("Password must be:\nAt least 8 characters long.\nAt least a number.");
            return false;
        }
        else{
            editPasswordLayout.setError("");
            return true;
        }
    }


    // Checks for proper username size.
    private boolean checkUsername(){
        if(editName.length() < 4 || editName.length() > 30) {
            editNameLayout.setError("Username must be between 4 and 20 characters long.");
            return false;
        }
        else {
            editNameLayout.setError("");
            return true;
        }
    }


    // Checks for email validity.
    private boolean checkEmail() {
        if(!editEmail.getText().toString().contains("@")){
            editEmailLayout.setError("This doesn't seem as a valid email.");
            return false;
        }
        else{
            editEmailLayout.setError("");
            return true;
        }
    }


    // Checks if the two password text fields are identical.
    private boolean checkReEnterPassword() {
        if (editReEnterPassword.getText().toString().equals(editPassword.getText().toString())){
            editReEnterPasswordLayout.setError("");
            return true;
        }
        else{
            editReEnterPasswordLayout.setError("There is already an account registered with this email.");
            return false;
        }
    }


    // Checks if the "Accept terms & conditions" button has been selected.
    private boolean checkCheckBox() {
        if(checkBox.isChecked()){
            textCheckLicence.setVisibility(View.INVISIBLE);
            return true;
        }
        else{
            textCheckLicence.setVisibility(View.VISIBLE);
            return false;
        }
    }


    // Brings all the checks together.
    private boolean checkEntries(){
        checkUsername();
        checkEmail();
        checkPassword();
        checkReEnterPassword();
        checkCheckBox();
        return checkUsername() && checkEmail() && checkPassword() && checkReEnterPassword() && checkCheckBox();
    }


    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    // Sends an email to the given address, in order to verify the new user.
    private void sendEmailVerification(){
        final FirebaseUser user=firebaseAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e("", "sendEmailVerification",task.getException());
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initializeViews() {
        editName=findViewById(R.id.editName);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        editReEnterPassword=findViewById(R.id.editReEnterPassword);
        textCheckLicence=findViewById(R.id.textCheckLicence);
        checkBox=findViewById(R.id.checkBox);
        buttonRegister=findViewById(R.id.buttonRegister);
        registerToolbar=findViewById(R.id.registerToolbar);
        editNameLayout=findViewById(R.id.editNameLayout);
        editEmailLayout=findViewById(R.id.editEmailLayout);
        editPasswordLayout=findViewById(R.id.editPasswordLayout);
        editReEnterPasswordLayout=findViewById(R.id.editReEnterPasswordLayout);
        firebaseAuth=FirebaseAuth.getInstance();
    }
}

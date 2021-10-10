package com.example.pointer_technologiesv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddDeviceActivity extends AppCompatActivity {
    private Button buttonAddDevice;
    private EditText editAddName, editAddCode;
    private final List<Device> devices = new ArrayList<>();
    private FloatingActionButton floatingActionButtonHome;
    private TextInputLayout editAddNameLayout, editAddPasswordLayout;
    private DatabaseReference databaseReference;

    private static final String TAG = "AddDeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initializeViews();

        buttonAddDevice.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {

                // If the entry requirements are met, then try to add the device to the DatabaseReference.
                if (checkEntries()) {
                    try {
                        Device device = new Device(editAddName.getText().toString(), editAddCode.getText().toString());
                        databaseReference.child(editAddCode.getText().toString().trim())
                            .setValue(device)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddDeviceActivity.this, "we did it boyz", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddDeviceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e){
                        Toast.makeText(AddDeviceActivity.this,"Something went wrong.",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    intent = new Intent(AddDeviceActivity.this, AppCenterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });

        floatingActionButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDeviceActivity.this, AppCenterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

    }


    // The new device name must be over 3 and below 20 characters long.
    // Also, the device code must not be the same as some codes the company is supposed to use.
    public boolean checkEntries() {
        if ((editAddName.getText().toString().length() < 3 || editAddName.getText().toString().length() > 20) && !editAddCode.getText().toString().equals("12345678")) {
            editAddNameLayout.setError("Name must be between 3 and 20 characters.");
            editAddPasswordLayout.setError("This 8-digit code is not valid.");
            return false;
        }
        else if (editAddName.getText().toString().length() < 3 || editAddName.getText().toString().length() > 20) {
            editAddNameLayout.setError("Name must be between 3 and 20 characters.");
            editAddPasswordLayout.setError("");
            return false;
        }
        else if (!(editAddCode.getText().toString().equals("12345678") || editAddCode.getText().toString().equals("12345671") || editAddCode.getText().toString().equals("12345672") || editAddCode.getText().toString().equals("12345673") || editAddCode.getText().toString().equals("12345674") || editAddCode.getText().toString().equals("12345675") || editAddCode.getText().toString().equals("12345676") || editAddCode.getText().toString().equals("12345677") || editAddCode.getText().toString().equals("12345679") )) {
            editAddNameLayout.setError("");
            editAddPasswordLayout.setError("This 8-digit code is not valid.");
            return false;
        }

        else{
            editAddNameLayout.setError("");
            editAddPasswordLayout.setError("");
            return true;
        }

    }


    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void initializeViews() {
        buttonAddDevice = findViewById(R.id.buttonAddDevice);
        editAddName = findViewById(R.id.editAddName);
        editAddCode = findViewById(R.id.editAddPassword);
        //MaterialToolbar addDeviceToolbar = findViewById(R.id.addDeviceToolbar);
        floatingActionButtonHome = findViewById(R.id.floatingActionButtonHome);
        editAddNameLayout=findViewById(R.id.editAddNameLayout);
        editAddPasswordLayout=findViewById(R.id.editAddPasswordLayout);
        databaseReference= FirebaseDatabase.getInstance().getReference("devices").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

}

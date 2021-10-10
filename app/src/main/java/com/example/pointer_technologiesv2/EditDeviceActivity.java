package com.example.pointer_technologiesv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EditDeviceActivity extends AppCompatActivity {
    private MaterialToolbar editDeviceToolbar;
    private TextInputEditText editNewDeviceName;
    private Button buttonApply;
    private MaterialRadioButton iconColorBlack, iconColorYellow, iconColorRed, iconColorGreen, iconColorBlue;
    private Integer position = -1;
    private Integer colorId = -1;
    private ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);

        // Given to the activity via AppCenterActivity.
        final String oldName = getIntent().getStringExtra("oldName");
        final String deviceId = getIntent().getStringExtra("deviceId");

        initializeViews();
        editNewDeviceName.setText(oldName);

        // Takes care of drawing a border around the icon that was selected.
        for (int i = 0; i < 12; i++) {
            final int I = i;
            imageViews[I].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == -1) {
                        imageViews[I].setBackground(getResources().getDrawable(R.drawable.imageview_border));
                        position = I;
                    } else if (position == I) {
                        imageViews[position].setBackground(null);
                        position = -1;
                    } else {
                        imageViews[I].setBackground(getResources().getDrawable(R.drawable.imageview_border));
                        imageViews[position].setBackground(null);
                        position = I;
                    }

                }
            });
        }

        editDeviceToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(EditDeviceActivity.this, AppCenterActivity.class));
            }
        });


        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkText = false;
                boolean checkLogo = false;
                String code = getIntent().getStringExtra("code");

                final String newName = Objects.requireNonNull(editNewDeviceName.getText()).toString();


                // Check the icon color.
                if (iconColorBlack.isChecked())
                    colorId = 0;
                else if (iconColorYellow.isChecked())
                    colorId = 1;
                else if (iconColorRed.isChecked())
                    colorId = 2;
                else if (iconColorGreen.isChecked())
                    colorId = 3;
                else if (iconColorBlue.isChecked())
                    colorId = 4;

                // Check the icon itself.
                if ((colorId != -1) || (position != -1)) {
                    checkLogo = true;
                    if (position == -1 && colorId != -1)
                        code = code.substring(0, 2) + colorId.toString();
                    else if (position != -1) {
                        if (position<10 &&colorId == -1)
                            code="0"+position.toString()+code.substring(2);
                        else if(position>10 && colorId==-1)
                            code=position.toString()+colorId.toString();
                        else if (position < 10 && colorId != -1)
                            code = "0" + position.toString() + colorId.toString();
                        else
                            code = position.toString() + colorId.toString();
                    }
                }

                // Check if the new name size is appropriate. If not, give out an error message.
                // Then check if any component has actually changed for the device to be updated.
                if (checkName(newName)) {
                    checkText = true;
                } else if (newName.length() == 0) {
                    Toast.makeText(EditDeviceActivity.this, R.string.emptyDeviceName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditDeviceActivity.this, R.string.longDeviceName, Toast.LENGTH_SHORT).show();
                }

                if (checkLogo || checkText) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference("devices")
                        .child(Objects
                        .requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(deviceId);

                    Device device = new Device(newName, deviceId, code);
                    databaseReference.setValue(device);
                    Toast.makeText(EditDeviceActivity.this, "Device updated successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditDeviceActivity.this, AppCenterActivity.class));
                }
            }
        });
    }


    private boolean checkName(String newName) {
        return newName.length() >= 3 && newName.length() <= 20;
    }


    private void initializeViews() {
        editDeviceToolbar = findViewById(R.id.editDeviceToolbar);
        editNewDeviceName = findViewById(R.id.editNewDeviceName);
        buttonApply = findViewById(R.id.buttonApply);
        imageViews = new ImageView[12];
        imageViews[0] = findViewById(R.id.imageCar);
        imageViews[1] = findViewById(R.id.imageBike);
        imageViews[2] = findViewById(R.id.imageMotor);
        imageViews[3] = findViewById(R.id.imageScooter);
        imageViews[4] = findViewById(R.id.imageTV);
        imageViews[5] = findViewById(R.id.imageKeys);
        imageViews[6] = findViewById(R.id.imageTablet);
        imageViews[7] = findViewById(R.id.imageAndroid);
        imageViews[8] = findViewById(R.id.imageIphone);
        imageViews[9] = findViewById(R.id.imagePet);
        imageViews[10] = findViewById(R.id.imageWallet);
        imageViews[11] = findViewById(R.id.imagePeople);
        iconColorBlack = findViewById(R.id.iconColorBlack);
        iconColorYellow = findViewById(R.id.iconColorYellow);
        iconColorRed = findViewById(R.id.iconColorRed);
        iconColorGreen = findViewById(R.id.iconColorGreen);
        iconColorBlue = findViewById(R.id.iconColorBlue);
    }
}
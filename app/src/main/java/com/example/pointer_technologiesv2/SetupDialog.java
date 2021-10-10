package com.example.pointer_technologiesv2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SetupDialog extends AppCompatDialogFragment {
    private EditText editTextConnectWiFi;
    private int itemPosition;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.setup_dialog, null);

        // Bundle that carries the position of the device in the adapter.
        Bundle bundle = this.getArguments();
        editTextConnectWiFi = view.findViewById(R.id.editTextConnectWiFi);
        itemPosition = bundle.getInt("position"); // Get the position.

        builder.setView(view)
            .setTitle("Device Setup")
            .setNegativeButton(R.string.WiFiSettingsSetupDialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Open WiFi settings in order to connect to the Access Point.
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                }
            })
            .setPositiveButton(R.string.continueSetupDialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), SetupActivity.class);

                /**
                * The position is passed to the setup activity in order to get the local IP of the device,
                * then store it to device list. This makes sure that the same device doesn't need
                * to be set up twice while connected to the same network.
                */
                intent.putExtra("position", itemPosition);
                startActivity(intent);
                }
            });

        return builder.create();
    }

}

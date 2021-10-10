package com.example.pointer_technologiesv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class HelpDialog extends AppCompatDialogFragment {
    private onClickListener listener;

    public interface onClickListener{
        void onEmailClick();
        void onPhoneClick();
    }

    public void setOnClickListener(HelpDialog.onClickListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.help_dialog,null);

        RelativeLayout email = view.findViewById(R.id.helpDialogEmailRelative);
        RelativeLayout phone = view.findViewById(R.id.helpDialogPhoneRelative);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEmailClick();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhoneClick();
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.contact);

        return builder.create();
    }
}

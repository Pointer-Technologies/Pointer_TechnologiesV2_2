package com.example.pointer_technologiesv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

	Activity activity;
	AlertDialog alertDialog;

	LoadingDialog(Activity activity){
		this.activity = activity;
	}

	public void startLoadingDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		LayoutInflater inflater = activity.getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_loading, null));
		builder.setCancelable(false); // Cancels itself when it gets the local IP.

		alertDialog = builder.create();
		alertDialog.show();
	}

	public void dismissDialog(){
		alertDialog.dismiss();
	}

}

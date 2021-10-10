package com.example.pointer_technologiesv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import okhttp3.HttpUrl;

public class SetupActivity extends AppCompatActivity {
	DatagramSocket datagramSocket;
	DatagramPacket packet;
	private WebView webViewSetup;
	private int itemPosition;
	private String ssid;
	private String password;
	private String local_IP;
	private String UDP_IP;
	private int UDP_PORT;

	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
		setContentView(R.layout.activity_setup);
		final LoadingDialog loadingDialog = new LoadingDialog(SetupActivity.this);

		// Get the intent that called the activity.
		Intent onCreateIntent = this.getIntent();
		// The intent contained the item position.
		itemPosition = onCreateIntent.getIntExtra("position",0);

		// Initialize the remote IP and the port.
		UDP_IP = "192.168.43.200";
		UDP_PORT = 4210;
		String url = "http://192.168.4.1";

		webViewSetup = (WebView)findViewById(R.id.webViewSetup);
		WebSettings webSettings = webViewSetup.getSettings();
		webViewSetup.setWebViewClient(new WebViewClient());
		webViewSetup.loadUrl(url);
		webSettings.setJavaScriptEnabled(true);

		webViewSetup.addJavascriptInterface(new Object(){
			// This function is called when the "Submit" button is pressed.
			public void performClick(){

				// Call the loading dialog and get everything ready.
				loadingDialog.startLoadingDialog();

				// Use the new URL produced to get the SSID and password.
				final HttpUrl updatedUrl = HttpUrl.parse(webViewSetup.getUrl());
				if (updatedUrl != null) {
					ssid = updatedUrl.queryParameter("SSID");
					password = updatedUrl.queryParameter("password");
				}

				// Wait for the module to send its local IP.
				while(local_IP == null){
					try{
						datagramSocket = new DatagramSocket(UDP_PORT);
					}catch(SocketException e){
						e.printStackTrace();
					}

					// Listen for packets in the Remote IP port.
					byte[] buffer = new byte[20];
					packet = new DatagramPacket(buffer, buffer.length);

					try{
						datagramSocket.receive(packet);
					}catch(IOException e){
						e.printStackTrace();
					}

					local_IP = String.valueOf(packet.getData());
				}

				// After the setup is done, wrap the process up.
				loadingDialog.dismissDialog();

				/**
				 * Give the IP and position information back to the AppCenterActivity, in order to update the device info.
				 * NOTE: This hasn't been thoroughly tested, the first (and last) test showed the expected results.
 				 */
				Intent intent = new Intent(SetupActivity.this, AppCenterActivity.class);
				intent.putExtra("local_IP", local_IP);
				intent.putExtra("position", itemPosition);
				startActivity(intent);



				finish();
			}
		}, "submit");
	}


	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
	}

	// Getters.
	public String getSsid(){
		return ssid;
	}

	public String getPassword(){
		return password;
	}

	public String getLocal_IP(){
		return local_IP;
	}

	public int getItemPosition(){
		return itemPosition;
	}
}

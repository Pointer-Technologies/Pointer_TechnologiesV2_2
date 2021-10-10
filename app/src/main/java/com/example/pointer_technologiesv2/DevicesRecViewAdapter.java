package com.example.pointer_technologiesv2;

import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.WIFI_SERVICE;
import static androidx.core.content.ContextCompat.startActivity;

public class DevicesRecViewAdapter extends RecyclerView.Adapter<DevicesRecViewAdapter.ViewHolder> {
    private List<Device> devices=new ArrayList<>();
    private onItemClickListener mListener;
    private final Context context;

    public interface onItemClickListener{
        void onDeleteClick(int position);
        void onEditClick(int position);
        void onLocationClick(int position);
        void onVibrationClick(int position);
        void onSoundClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public DevicesRecViewAdapter(Context context){
        this.context = context;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRecViewAdapter.ViewHolder holder, final int position) {
        holder.textCardViewName.setText(devices.get(position).getName());
        holder.locationCardView.setImageResource(R.drawable.ic_location);

        if(!devices.get(position).getIconCode().equals("")){
            String pos=devices.get(position).getIconCode().substring(0,2);
            String colorId=devices.get(position).getIconCode().substring(2);
            holder.logoCardView.setImageResource(getLogoDrawableId(pos));
            holder.logoCardView.setColorFilter(colorDecode(colorId), PorterDuff.Mode.LIGHTEN);
        }
        else
          holder.logoCardView.setImageResource(R.drawable.ic_launcher_background);
        holder.deleteCardView.setImageResource(R.drawable.ic_delete);
        holder.editCardView.setImageResource(R.drawable.ic_edit);
        holder.idCardView.setText(context.getString(R.string.serialNumber,devices.get(position).getId()));
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }

    /**
     * The code is a 3-digit integer.
     * The first digit is the color of the icon.
     * The last two digits are the icon selected.
     */
    private int getLogoDrawableId(String position) {
        int id = -1;
        switch (position) {
            case "00":
                id = R.drawable.ic_car;
                break;
            case "01":
                id = R.drawable.ic_bike;
                break;
            case "02":
                id = R.drawable.ic_motor;
                break;
            case "03":
                id = R.drawable.ic_scooter;
                break;
            case "04":
                id = R.drawable.ic_tv;
                break;
            case "05":
                id = R.drawable.ic_key;
                break;
            case "06":
                id = R.drawable.ic_tablet;
                break;
            case "07":
                id = R.drawable.ic_android;
                break;
            case "08":
                id = R.drawable.ic_iphone;
                break;
            case "09":
                id = R.drawable.ic_pet;
                break;
            case "10":
                id = R.drawable.ic_wallet;
                break;
            case "11":
                id = R.drawable.ic_people;
                break;
            default:
                break;
        }
        return id;
    }

    private int colorDecode(String id){
        switch (id){
            case "0":
                return Color.BLACK;
            case "1":
                return Color.YELLOW;
            case "2":
                return Color.RED;
            case "3":
                return Color.GREEN;
            case "4":
                return Color.BLUE;
            default:
                return -1;
        }
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private ImageView logoCardView;
        private final ImageView locationCardView;
        private final ImageView deleteCardView;
        private final ImageView editCardView;
        private final ImageView up_arrow;
        private final ImageView down_arrow;
        private final CardView parentCardView;
        private final TextView textCardViewName;
        private final TextView idCardView;
        private final ConstraintLayout expandableLayout;
        private final SwitchMaterial soundSwitch;
        private final SwitchMaterial vibrationSwitch;

        public ImageView getLogoCardView() {
            return logoCardView;
        }

        public void setLogoCardView(ImageView logoCardView) {
            this.logoCardView = logoCardView;
        }

        public ViewHolder(@NonNull final View itemView , final onItemClickListener listener) {
            super(itemView);
            textCardViewName=itemView.findViewById(R.id.textCardViewName);
            logoCardView=itemView.findViewById(R.id.logoCardView);
            locationCardView=itemView.findViewById(R.id.locationCardView);
            parentCardView=itemView.findViewById(R.id.parentCardView);
            deleteCardView=itemView.findViewById(R.id.deleteCardView);
            editCardView=itemView.findViewById(R.id.editCardView);
            expandableLayout=itemView.findViewById(R.id.expandableLayout);
            up_arrow=itemView.findViewById(R.id.up_arrow);
            down_arrow=itemView.findViewById(R.id.down_arrow);
            RelativeLayout editRelativeLayout = itemView.findViewById(R.id.editRelativeLayout);
            RelativeLayout deleteRelativeLayout = itemView.findViewById(R.id.deleteRelativeLayout);
            RelativeLayout locationRelativeLayout = itemView.findViewById(R.id.locationRelativeLayout);
            idCardView=itemView.findViewById(R.id.idCardView);
            vibrationSwitch = itemView.findViewById(R.id.vibrationSwitch);
            soundSwitch = itemView.findViewById(R.id.soundSwitch);

            // Expands the device tab when the user presses it.
            // Then shrinks it if pressed again.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {

                            if (expandableLayout.getVisibility() == View.GONE) {
                                expandableLayout.setVisibility(View.VISIBLE);
                                down_arrow.setVisibility(View.INVISIBLE);
                                up_arrow.setVisibility(View.VISIBLE);

                            }
                            else if (expandableLayout.getVisibility() == View.VISIBLE) {
                                expandableLayout.setVisibility(View.GONE);
                                up_arrow.setVisibility(View.INVISIBLE);
                                down_arrow.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });

            deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                            listener.onDeleteClick(position);
                    }
                }
            });

            locationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                            listener.onLocationClick(position);
                    }
                }
            });

            editRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                            listener.onEditClick(position);
                    }
                }
            });

            vibrationSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                            listener.onVibrationClick(position);
                    }
                }
            });

            soundSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position= getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                            listener.onSoundClick(position);
                    }
                }
            });


            // Turn vibration on or off, depending on the switch's state.
            /**
             * The sound and vibration of the tracker are controlled via a port in the local network.
             * The tracker hosts a website, containing only the buttons used to turn sound and vibration on or off.
             * The website is accessed via the tracker's local IP, when connected to the local network.
             */
            vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    String ip = getIPAddress(true);
                    String url;

                    if (isChecked) {
                        url = "http://" + ip + "/5/on";

                    } else {
                        url = "http://" + ip + "/5/off";
                    }
                    client.get(url, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }

                    });

                }
            });

            // Do the same for the sound.
            soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    String ip = getIPAddress(true);
                    String url;

                    if (isChecked) {
                        url = "http://" + ip + "/4/on";

                    } else {
                        url = "http://" + ip + "/4/off";
                    }
                    client.get(url, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }

                    });

                }
            });
        }
    }


    // Gets the network's IP address.
    public static String getIPAddress(boolean useIPv4){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }
}

package com.example.smit.sadr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smit.sadr.Adapters.ListLSAdapter;
import com.example.smit.sadr.Adapters.ListMusicAdapter;

public class Loudspeakers extends AppCompatActivity {
    TextView ip;
    ListView listLS;
    Handler updateListLSHandler = new Handler();
    Switch startStopServ;
    Button clean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loudspeakers);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        ip = (TextView)findViewById(R.id.ServIp);
        listLS = (ListView)findViewById(R.id.listLS);
        startStopServ = (Switch)findViewById(R.id.startStopServ);
        clean = (Button)findViewById(R.id.clean);
        getSupportActionBar().setTitle("Loudspeakers");
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if(wm.getWifiState()!=3){
            Toast.makeText(Loudspeakers.this, "Please turn on  WiFi", Toast.LENGTH_LONG).show();
        }else{
            ip.append(getIpAddrOfWIFI());
        }
        if(General.SERVER_STATUS==General.SERVER_ON){
            startStopServ.setChecked(true);
            updateListLS();
        }


        startStopServ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                General sock = new General();
                if (isChecked) {
                    WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    if (wm.getWifiState() != 3) {
                        Toast.makeText(Loudspeakers.this, "Please turn on the WiFi", Toast.LENGTH_LONG).show();
                        startStopServ.setChecked(false);
                    } else {

                        sock.runUDPserver(getIpAddrOfWIFI(), (short) 11221);
                        Toast.makeText(Loudspeakers.this, "Server is run", Toast.LENGTH_LONG).show();
                        General.SERVER_STATUS = General.SERVER_ON;
                        updateListLS();
                    }
                } else {
                    sock.stopUDPServer();
                    Toast.makeText(Loudspeakers.this, "Server is stoped", Toast.LENGTH_LONG).show();
                    General.SERVER_STATUS = General.SERVER_OFF;
                }
            }
        });

        clean.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View v) {
                General.LUnits.clear();
            }
        });
    }

    public  String getIpAddrOfWIFI(){
        WifiManager wManager =  (WifiManager)getSystemService(Context.WIFI_SERVICE);
        int ip_adress = wManager.getConnectionInfo().getIpAddress();
        String newip = String.format("%d.%d.%d.%d", (ip_adress & 0xff),
                (ip_adress >> 8 & 0xff),
                (ip_adress >> 16 & 0xff),
                (ip_adress >> 24 & 0xff));
        return newip;
    }

    public void updateListLS() {
        listLS.setAdapter(new ListLSAdapter(Loudspeakers.this, General.LUnits));
        if ( General.SERVER_STATUS==General.SERVER_ON) {
            Runnable notification = new Runnable() {
                public void run() {
                    updateListLS();
                }
            };
            updateListLSHandler.postDelayed(notification,2000);
        }
    }
}

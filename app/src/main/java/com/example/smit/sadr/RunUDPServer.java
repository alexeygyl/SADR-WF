package com.example.smit.sadr;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RunUDPServer implements Runnable {
    Context context;
    @Override
    public void run () {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int ip_adress = wifiManager.getConnectionInfo().getIpAddress();
            String newip = String.format("%d.%d.%d.%d", (ip_adress & 0xff),
                    (ip_adress >> 8 & 0xff),
                    (ip_adress >> 16 & 0xff),
                    (ip_adress >> 24 & 0xff));
            int port = 11221;//Не помню но вроде как должно быть выше 5000
            Log.e("E",newip);
            /*ip_s.setText(newip);
            port_s.setText(Integer.toString(port));
            mess.append("Set port: "+Integer.toString(port)+"\n");
            InetAddress serv_address = InetAddress.getByName(newip);
            DatagramSocket serversocket = new DatagramSocket(port,serv_address);
            mess.append("Create socket \n");
            byte [] buf = new byte[17];
            while(true) {
                DatagramPacket packet= new DatagramPacket(buf, buf.length);
                serversocket.receive(packet);
                result_mess=new String(packet.getData(),0,packet.getData().length);
                mess.post(new Runnable() {
                    @Override
                    public void run() {
                        mess.append("Create datagramPacket \n");
                        mess.append("Wait for packet ....\n");
                        mess.append(result_mess);
                    }
                });
                for (int i=0;i<buf.length;i++) buf[i]=0;
            }*/
        }
        catch (Exception e){
           // mess.append("Error: "+e);
        }
    }

}

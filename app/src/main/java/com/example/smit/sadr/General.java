package com.example.smit.sadr;


import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CompoundButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public  class General {
    static Context context;
    public  static Integer SERVER_ON=1;
    public  static Integer SERVER_OFF=2;
    public  static Integer INIT = 1;
    public  static Integer INIT_ACK = 2;
    public static Integer  SYNC = 3;
    public static Integer  TURN_ON = 1;
    public static Integer  TURN_OFF = 2;
    public  static  Integer SERVER_STATUS=SERVER_OFF;
    public static DatagramSocket serversocket;
    public  static Integer CYES = 1;
    public  static Integer NYES = 2;
    public static List<LoudspeakerUnits> LUnits = new ArrayList<LoudspeakerUnits>();
    public  static List<MusicUnits> getMusicList(String PATH){
        List<MusicUnits> musicList = new ArrayList<MusicUnits>();
        File file = new File(PATH);
        File [] fileList = file.listFiles();

        for (Integer i=0;i<fileList.length;i++){
            musicList.add(new MusicUnits(fileList[i].getName(),"Unknown",fileList[i].getAbsolutePath()));
        }
        return musicList;
    }

    public static String getStrTime(Integer curTime){
        String time  = new String();
        Integer h,m,s,tmp;
        if(curTime/1000>=60&&curTime/1000<60*60){
            m = curTime/(1000*60);
            s = (curTime/1000)-(60*m);
            time = m<10?"0"+Integer.toString(m):Integer.toString(m);
            time += ":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }else if(curTime/1000<60){
            s = curTime/1000;
            time ="00:";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }else if(curTime/1000 >= 60*60 ){
            h = curTime/(1000*60*60);
            m = ((curTime/1000)-(h*60*60))/60;
            s = (curTime/1000)-(h*60*60)-(m*60);
            time = Integer.toString(h);
            time +=":";
            time = m<10?"0"+Integer.toString(m):Integer.toString(m);
            time +=":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }

        return time;
    }

    public void runUDPserver(final String ip, final short port){
        Thread thrd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress serv_address = InetAddress.getByName(ip);
                    serversocket = new DatagramSocket(port,serv_address);
                    byte [] rxBuff = new byte[17];
                    Log.e("MSG", ip);
                    int pos;
                    while(true) {
                        DatagramPacket packet= new DatagramPacket(rxBuff, rxBuff.length);
                        serversocket.receive(packet);
                        String result_mess=new String(packet.getData(),0,packet.getLength());
                        Long now = System.currentTimeMillis();
                        switch (packet.getData()[0]){
                            case 0x01:
                                String name = new String((packet.getData()),2,packet.getLength()-2);
                                if((pos = existsName(name))<0) {
                                    LUnits.add(new LoudspeakerUnits(name,
                                            packet.getData()[1],
                                            LUnits.size(),
                                            INIT,
                                            packet.getAddress().getHostAddress(),
                                            packet.getPort(),
                                            now,
                                            TURN_OFF));

                                }else if(LUnits.get(pos).status==General.INIT_ACK){
                                    sendInitAck(pos);
                                }
                                break;
                            case 0x03:
                                if((pos = existsId(packet.getData()[1]))>=0) {
                                    LUnits.get(packet.getData()[1]).status = General.SYNC;
                                    LUnits.get(packet.getData()[1]).canal = packet.getData()[2];
                                }else sendReset(packet.getAddress().getHostAddress(),packet.getPort());
                                break;
                            case 0x55:
                                    LUnits.get(packet.getData()[1]).FF=General.CYES;
                                break;
                        }
                        for (int i=0;i<rxBuff.length;i++) rxBuff[i]='\0';
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thrd.start();


    }

public   void stopUDPServer(){
    serversocket.close();
}

    public static void sendInitAck(int pos) {
        byte [] rxBuff = new byte[2];
        rxBuff[0]=0x02;
        rxBuff[1]=(byte)pos;
        final DatagramPacket packet= new DatagramPacket(rxBuff, rxBuff.length);
        try {
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            Thread thrd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serversocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thrd.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendReset(String ip, int port) {
        byte [] rxBuff = new byte[1];
        rxBuff[0]=0x10;
        final DatagramPacket packet= new DatagramPacket(rxBuff, rxBuff.length);
        try {
            packet.setAddress(InetAddress.getByName(ip));
            packet.setPort(port);
            Thread thrd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serversocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thrd.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  static void sendData(int pos, String PATH, String name){
        Long  sendTime;
        try {
            byte [] rxBuff = new byte[505];
            byte[] BName = name.getBytes();
            byte[] FBody =new byte[500];
            File file = new File(PATH);
            Integer all=0;
            int count=1;
            Integer byte_read=0;
            InputStream is = new FileInputStream(file);
            final DatagramPacket packet= new DatagramPacket(rxBuff,1);
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            //--------------------0x04--------
            rxBuff[0]=0x04;
            for(int i=0;i<name.length();i++){
                rxBuff[i+1]=BName[i];
            }
            packet.setData(rxBuff);
            packet.setLength(name.length() + 1);
            serversocket.send(packet);
            sendTime=System.currentTimeMillis();
            while (LUnits.get(pos).FF==General.NYES){
                if(System.currentTimeMillis()-sendTime>2000){
                    serversocket.send(packet);
                    sendTime=System.currentTimeMillis();
                    Log.e("RESEND","Pack 0");
                }
            }
            Log.e("OPEN","Confirmed");
            LUnits.get(pos).FF = General.NYES;
            //----------------------------------------
            //------------------0x05------------------
            rxBuff[0]=0x05;
            //all+=byte_read;
            while((byte_read=is.read(FBody,0,500))!=-1){
                rxBuff[1]=(byte)(count>>0);
                rxBuff[2]=(byte)(count>>8);
                rxBuff[3]=(byte)(count>>16);
                rxBuff[4]=(byte)(count>>24);
                for(int i=0;i<byte_read;i++){
                    rxBuff[i+5]=FBody[i];
                }
                packet.setData(rxBuff);
                packet.setLength(byte_read + 5);
                serversocket.send(packet);
                sendTime=System.currentTimeMillis();
                while (LUnits.get(pos).FF==General.NYES){
                    if(System.currentTimeMillis()-sendTime>2000){
                        serversocket.send(packet);
                        sendTime=System.currentTimeMillis();
                        Log.e("RESEND","Pack "+Integer.toString(count));
                    }
                }
               // Log.e("SENT","Pack "+Integer.toString(count)+" confirmed " + Integer.toString(byte_read)+ "bytes");
                LUnits.get(pos).FF = General.NYES;
                count++;
            }


            //----------------------------------------
            //------------------0x06------------------
            rxBuff[0]=0x06;
            packet.setData(rxBuff);
            packet.setLength(1);
            serversocket.send(packet);
            sendTime=System.currentTimeMillis();
            while (LUnits.get(pos).FF==General.NYES){
                if(System.currentTimeMillis()-sendTime>2000){
                    serversocket.send(packet);
                    sendTime=System.currentTimeMillis();
                    Log.e("RESEND","Pack CLOSE");
                }
            }
            Log.e("CLOSE","Confirmed");
            Log.e("FILE",Integer.toString((int)file.length()));
            LUnits.get(pos).FF = General.NYES;
            //----------------------------------------
           //
            //Log.e("READ", Long.toString(file.length()));
           // Log.e("READ2", Integer.toString(all));


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int existsId(int id){
        for(int i=0;i<LUnits.size();i++)if(LUnits.get(i).id==id) {
            return i;
        }
        return -1;
    }
    public int existsName(String name){
        for(int i=0;i<LUnits.size();i++)if(LUnits.get(i).name.equals(name)) {
            return i;
        }
        return -1;
    }
    public int existsIp(String ip){
        for(int i=0;i<LUnits.size();i++)if(LUnits.get(i).ip.equals(ip)) {
            return i;
        }
        return -1;
    }

}

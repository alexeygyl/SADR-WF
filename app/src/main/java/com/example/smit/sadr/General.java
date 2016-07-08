package com.example.smit.sadr;


import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CompoundButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public  class General {
    static Context context;
    public  static Integer SERVER_ON=1;
    public  static Integer SERVER_OFF=2;
    public  static Integer INIT = 1;
    public  static Integer INIT_ACK = 2;
    public static Integer  SYNC = 3;
    public static Integer  TURN_ON = 1;
    public static Integer  TURN_OFF = 2;
    public static Integer  STOPED = 2;
    public static Integer  PLAYING = 1;
    public static Integer COLUN_STATUS=STOPED;

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
            String format =  fileList[i].getName().substring(fileList[i].getName().lastIndexOf(".") + 1, fileList[i].getName().length());
            if(format.equals("wav")) musicList.add(new MusicUnits(fileList[i].getName(),"Unknown",fileList[i].getAbsolutePath(),1));
            else if(format.equals("flac")) musicList.add(new MusicUnits(fileList[i].getName(),"Unknown",fileList[i].getAbsolutePath(),2));
            else if(format.equals("mp3")) musicList.add(new MusicUnits(fileList[i].getName(),"Unknown",fileList[i].getAbsolutePath(),3));
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
                            case 0x32:
                                Log.e("STOPING","STOPED");
                                    General.COLUN_STATUS=STOPED;
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

    public static void sendStopPlay(String ip, int port) {
        byte [] rxBuff = new byte[1];
        rxBuff[0]=0x31;
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

    private static void sendDataInit(int pos,String PATH,String name, int formatIndex){
        try {
            byte [] rxBuff = new byte[200];
            byte[] BName = name.getBytes();
            Long  sendTime;
            long fileSize;
            File file = new File(PATH);
            final DatagramPacket packet= new DatagramPacket(rxBuff,1);
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            if(formatIndex==1)fileSize= file.length();
            else fileSize=0;
            //--------------------0x04--------
            if(MainActivity.status==MainActivity.ON) {
                rxBuff[0] = 0x04;
                rxBuff[1] = (byte) (fileSize >> 0);
                rxBuff[2] = (byte) (fileSize >> 8);
                rxBuff[3] = (byte) (fileSize >> 16);
                rxBuff[4] = (byte) (fileSize >> 24);
                for (int i = 0; i < name.length(); i++) {
                    rxBuff[i + 5] = BName[i];
                }
                packet.setData(rxBuff);
                packet.setLength(name.length() + 5);
                serversocket.send(packet);
                sendTime = System.currentTimeMillis();
                while (LUnits.get(pos).FF == General.NYES) {
                    if (System.currentTimeMillis() - sendTime > 2000) {
                        serversocket.send(packet);
                        sendTime = System.currentTimeMillis();
                        Log.e("RESEND", "DataInit");
                    }
                }
                Log.e("OPEN", "Confirmed");
                LUnits.get(pos).FF = General.NYES;
            }
            //----------------------------------------
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendDataEnd(int pos){
        try {
            byte [] rxBuff = new byte[1];
            Long  sendTime;
            final DatagramPacket packet= new DatagramPacket(rxBuff,1);
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            //------------------0x06------------------
            if(MainActivity.status==MainActivity.ON) {
                rxBuff[0] = 0x06;
                packet.setData(rxBuff);
                packet.setLength(1);
                serversocket.send(packet);
                sendTime = System.currentTimeMillis();
                while (LUnits.get(pos).FF == General.NYES) {
                    if (System.currentTimeMillis() - sendTime > 2000) {
                        serversocket.send(packet);
                        sendTime = System.currentTimeMillis();
                        Log.e("RESEND", "Data End");
                    }
                }
                Log.e("CLOSE", "Confirmed");
                LUnits.get(pos).FF = General.NYES;
            }
            //----------------------------------------

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void senDataBodyMP3(int pos, Integer duration, String PATH){
        try {
            byte [] rxBuff = new byte[1500];
            byte[] byteData = null;
            int startTimeRead=0;
            int posByteToSend=0;
            int step=1024;
            int countToSend;
            Integer all=0;
            int count=1;
            Integer byte_read=0;
            Long  sendTime;
            final DatagramPacket packet= new DatagramPacket(rxBuff,1);
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            //------------------0x05------------------
            rxBuff[0]=0x05;
            //all+=byte_read;
            while(startTimeRead<duration&& MainActivity.status == MainActivity.ON) {
                posByteToSend=0;
                byteData=null;
                byteData = General.decode_path(PATH,startTimeRead,1000);
                //Log.e("BUFF", Integer.toString(byteData.length)+" POS "+ Integer.toString(startTimeRead));
                startTimeRead+=1000;
                while (posByteToSend<byteData.length&& MainActivity.status == MainActivity.ON) {
                    if(byteData.length-posByteToSend>=1024)countToSend=1024;
                    else countToSend = byteData.length-posByteToSend;
                    rxBuff[1] = (byte) (count >> 0);
                    rxBuff[2] = (byte) (count >> 8);
                    rxBuff[3] = (byte) (count >> 16);
                    rxBuff[4] = (byte) (count >> 24);

                    for (int i = 0; i < countToSend; i++) {
                        rxBuff[i + 5] = byteData[i+posByteToSend];
                    }
                    packet.setData(rxBuff);
                    packet.setLength(countToSend + 5);
                    serversocket.send(packet);
                    sendTime = System.currentTimeMillis();
                    while (LUnits.get(pos).FF == General.NYES) {
                        if (System.currentTimeMillis() - sendTime > 2000) {
                            serversocket.send(packet);
                            sendTime = System.currentTimeMillis();
                            Log.e("RESEND", "Pack " + Integer.toString(count));
                        }
                    }
                    //Log.e("SENT", "Pack " + Integer.toString(count) + " confirmed " + Integer.toString(countToSend) + "bytes");
                    LUnits.get(pos).FF = General.NYES;
                    count++;
                    posByteToSend+=countToSend;
                }
            }
            //----------------------------------------

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendDataBodyWAV(int pos, String PATH, String name){
        try {
            byte [] rxBuff = new byte[1500];
            byte[] BName = name.getBytes();
            byte[] FBody =new byte[1024];
            File file = new File(PATH);
            Long  sendTime;
            Integer all=0;
            int count=1;
            Integer byte_read=0;
            InputStream is = new FileInputStream(file);
            final DatagramPacket packet= new DatagramPacket(rxBuff,1);
            packet.setAddress(InetAddress.getByName(LUnits.get(pos).ip));
            packet.setPort(LUnits.get(pos).port);
            //------------------0x05------------------
            rxBuff[0]=0x05;
            while((byte_read=is.read(FBody,0,1024))!=-1&&MainActivity.status==MainActivity.ON){
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
                //Log.e("SENT","Pack "+Integer.toString(count)+" confirmed " + Integer.toString(byte_read)+ "bytes");
                LUnits.get(pos).FF = General.NYES;
                count++;
            }
            //----------------------------------------

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static void sendData(int pos, String PATH, String name, Integer duration,int formatIndex){
        sendDataInit(pos,PATH,name,formatIndex);
        switch (formatIndex){
            case 1:
                sendDataBodyWAV(pos,PATH,name);
                break;
            case 2:
                break;
            case 3:
                senDataBodyMP3(pos,duration,PATH);
                break;
        }
        sendDataEnd(pos);

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

    public static byte[] decode_path(String path, int startMs, int maxMs) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

        float totalMs = 0;
        boolean seeking = true;

        File file = new File(path);
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();

            boolean done = false;
            while (! done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();

                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (! seeking) {
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);

                        if (output.getSampleFrequency() != 44100
                                || output.getChannelCount() != 2) {
                            throw new IllegalArgumentException("mono or non-44100 MP3 not supported");
                        }

                        short[] pcm = output.getBuffer();
                        for (short s : pcm) {
                            outStream.write(s & 0xff);
                            outStream.write((s >> 8 ) & 0xff);
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                bitstream.closeFrame();
            }

            return outStream.toByteArray();
        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        } catch (DecoderException e) {
            Log.e("DEC_ERROR", "Decoder error", e);
            throw new IOException("Decoder error: " + e);
        }
    }

}

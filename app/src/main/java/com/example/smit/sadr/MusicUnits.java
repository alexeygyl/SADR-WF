package com.example.smit.sadr;


import android.media.MediaPlayer;

import com.example.smit.sadr.General;

import java.io.IOException;

public class MusicUnits {
     public String Mname;
     public String MAuthor;
     public String Mtime;
     public String Path;

    public MusicUnits(String Name, String Author,String Path){
        String tmp;
        Integer pos;
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(Path);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    if((pos=Name.indexOf("-"))>0){
        this.MAuthor = Name.substring(0,pos);
        this.Mname = Name.substring(pos+1,Name.lastIndexOf("."));
    }
    else{
        this.Mname = Name.substring(0,Name.lastIndexOf("."));
        this.MAuthor = "Unknown";
    }


        this.Mtime = General.getStrTime(mp.getDuration());
        this.Path = Path;
        mp.release();
    }


}

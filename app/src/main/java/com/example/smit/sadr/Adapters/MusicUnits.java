package com.example.smit.sadr.Adapters;


import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

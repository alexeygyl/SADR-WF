package com.example.smit.sadr;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public  class General {
    public  static List<MusicUnits> getMusicList(String PATH){
        List<MusicUnits> musicList = new ArrayList<MusicUnits>();
        File file = new File(PATH);
        //String[] list = file.list();
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
}

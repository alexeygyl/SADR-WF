package com.example.smit.sadr;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smit.sadr.Adapters.ListMusicAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

     public List<MusicUnits> musicUnits = new ArrayList<MusicUnits>();

    ImageButton Mplay;
    ImageButton MplayNext;
    ImageButton MplayPrev;
    static Integer ON =1;
    static Integer PAUSE = 2;
    static  Integer STOP =0;
    static Integer status=STOP;
    Integer lastMusPos = 0;
    Long lastTime;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Handler seekHandler = new Handler();
    TextView musicName;
    TextView musicAuthor;
    ListView listmusic;
    TextView mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        musicUnits = General.getMusicList(Environment.getExternalStorageDirectory().toString()+"/Audio");
        initText();
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        //seekBar.setBackgroundColor(Color.rgb(255,213,0));
        Mplay.setBackgroundColor(Color.WHITE);
        Mplay.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
        musicName.setMaxWidth(350);
        musicName.setTextColor(Color.WHITE);
        musicAuthor.setTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        listmusic.setAdapter(new ListMusicAdapter(this, musicUnits));
        mediaPlayer = new MediaPlayer();

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (status != STOP) {
                    SeekBar sb = (SeekBar) v;
                    mediaPlayer.seekTo(sb.getProgress());
                }
                return false;
            }
        });

        listmusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                musicName.setText(musicUnits.get(position).Mname);
                musicAuthor.setText(musicUnits.get(position).MAuthor);
                //getMetaMp3Info(position);

                //startPlay(position, status);
                if(status==ON){
                    status=STOP;
                    for(int i=0; i<General.LUnits.size();i++){
                        if(General.LUnits.get(i).status==General.SYNC&&General.LUnits.get(i).turn==General.TURN_ON){

                            final int finalI = i;
                            Thread thrd_stop_play = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    General.sendStopPlay(General.LUnits.get(finalI).ip,General.LUnits.get(finalI).port);
                                }
                            });
                            thrd_stop_play.start();
                        }
                    }
                }
                while(General.COLUN_STATUS==General.PLAYING){}
                Mplay.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                status = ON;
                lastMusPos = position;
                General.COLUN_STATUS = General.PLAYING;
                for(int i=0; i<General.LUnits.size();i++){
                    if(General.LUnits.get(i).status==General.SYNC&&General.LUnits.get(i).turn==General.TURN_ON){
                        final int finalI = i;
                        Thread thrd_start_play = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                General.sendData(finalI,
                                                    musicUnits.get(position).Path,
                                                    musicUnits.get(position).Mname,
                                                    musicUnits.get(position).MDuration,
                                                    musicUnits.get(position).formatIndex);
                            }
                        });
                        thrd_start_play.start();

                    }

                }
            }
        });
        Mplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                       /* if (status == PAUSE) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                            mediaPlayer.start();

                            startPlayProgressUpdater();
                            status = ON;*/
                         if (status == ON) {
                            v.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
                            //mediaPlayer.pause();
                             mediaPlayer.stop();
                             mediaPlayer.reset();
                             status = STOP;
                            lastTime = SystemClock.elapsedRealtime();
                             for(int i=0; i<General.LUnits.size();i++){
                                 if(General.LUnits.get(i).status==General.SYNC&&General.LUnits.get(i).turn==General.TURN_ON){
                                     final int finalI = i;
                                     Thread thrd = new Thread(new Runnable() {
                                         @Override
                                         public void run() {
                                             General.sendStopPlay(General.LUnits.get(finalI).ip,General.LUnits.get(finalI).port);
                                         }
                                     });
                                     thrd.start();
                                 }
                             }

                        }
                        /*else if (status == STOP) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                            initText();
                            startPlay(0, status);
                            status = ON;
                        }*/
                        return true;
                }
                return false;
            }
        });

        MplayNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               /* switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:

                        v.setBackgroundColor(Color.WHITE);
                        lastMusPos++;
                        if (lastMusPos >= musicUnits.size()) lastMusPos = 0;
                        musicName.setText(musicUnits.get(lastMusPos).Mname);
                        musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                        startPlay(lastMusPos, status);

                        return true;
                }*/
                return false;
            }
        });
        MplayPrev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
             /*   switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                        lastMusPos--;
                        if (lastMusPos < 0) lastMusPos = musicUnits.size() - 1;
                        musicName.setText(musicUnits.get(lastMusPos).Mname);
                        musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                        startPlay(lastMusPos, status);
                        return true;
                }*/
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Loudspeakers:
                Intent intent = new Intent(this,Loudspeakers.class);
                startActivity(intent);


                return true;
        }
        return(super.onOptionsItemSelected(item));
    }


    public void startPlay( int position, final Integer status){
        if(status==ON || status ==PAUSE){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(musicUnits.get(position).Path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            startPlayProgressUpdater();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getMetaMp3Info( int position){
        
        File file = new File(musicUnits.get(position).Path);
        byte[] buff = new byte[128];
        Log.e("ERR",Long.toString(file.length()));
        try {
            InputStream is = new FileInputStream(file);

            is.skip(file.length() - 128);
            int count = is.read(buff, 0, 128);
            String meta = new String(buff, 0, 128);
            Log.e("ERR", meta);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("META",musicUnits.get(position).Mname);

    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        mDuration.setText(General.getStrTime(mediaPlayer.getCurrentPosition()));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            seekHandler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
        }
    }

public void initViews(){
    listmusic = (ListView) findViewById(R.id.listViewMusic);
    musicName = (TextView) findViewById(R.id.MusicName2);
    musicAuthor = (TextView) findViewById(R.id.MusicAuthor2);
    Mplay = (ImageButton) findViewById(R.id.Mplay);
    MplayNext = (ImageButton) findViewById(R.id.MplayNext);
    MplayPrev = (ImageButton) findViewById(R.id.MplayPrev);
    mDuration = (TextView)findViewById(R.id.mDuration);
    //mChronometer = (Chronometer) findViewById(R.id.chronometer);

}
    public void initText(){
        if(musicUnits.size()!=0){
            musicName.setText(musicUnits.get(0).Mname);
            musicAuthor.setText(musicUnits.get(0).MAuthor);
        }
    }



}

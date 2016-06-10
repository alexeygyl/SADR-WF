package com.example.smit.sadr;

import android.app.ActionBar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smit.sadr.Adapters.General;
import com.example.smit.sadr.Adapters.ListMusicAdapter;
import com.example.smit.sadr.Adapters.MusicUnits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicName.setText(musicUnits.get(position).Mname);
                musicAuthor.setText(musicUnits.get(position).MAuthor);
                startPlay(position, status);
                Mplay.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                status = ON;
                lastMusPos = position;
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
                        if (status == PAUSE) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                            mediaPlayer.start();

                            startPlayProgressUpdater();
                            status = ON;
                        } else if (status == ON) {
                            v.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
                            mediaPlayer.pause();
                            lastTime = SystemClock.elapsedRealtime();


                            status = PAUSE;
                        } else if (status == STOP) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                            initText();
                            startPlay(0, status);
                            status = ON;
                        }
                        return true;
                }
                return false;
            }
        });

        MplayNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:

                        v.setBackgroundColor(Color.WHITE);
                        lastMusPos++;
                        if(lastMusPos>=musicUnits.size())lastMusPos=0;
                        musicName.setText(musicUnits.get(lastMusPos).Mname);
                        musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                        startPlay(lastMusPos, status);

                        return true;
                        }
                return false;
                }
        });
        MplayPrev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
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
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

package com.example.smit.sadr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.smit.sadr.Adapters.ListFoldersAdapter;
import com.example.smit.sadr.Adapters.ListNewFolderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddNewFolder extends AppCompatActivity {
    public static List<String> folderUnits;
    public static ListView listFolder;
    String root = "/storage";
    public  static String currentDir;
    ListNewFolderAdapter adapter;
    Long firstClick;
    Integer posClicked=-1;
    public static Timer timer;
    public  static Handler mHandler;
    public  static Integer isShown=1;
    Integer toUpdate=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_folder);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        getSupportActionBar().setTitle(root);
        listFolder = (ListView)findViewById(R.id.listViewNewFolders);
        currentDir=root;
        folderUnits =getListFolder(currentDir);
        adapter = new ListNewFolderAdapter(this, folderUnits);
        listFolder.setAdapter(adapter);
        listFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (posClicked == position && System.currentTimeMillis() - firstClick < 500) {
                    timer.cancel();
                    isShown=1;
                    File f = new File(currentDir + "/" + folderUnits.get(position));
                    if (f.isDirectory()) {
                        currentDir += "/" + folderUnits.get(position);
                        folderUnits = getListFolder(currentDir);
                        adapter = new ListNewFolderAdapter(AddNewFolder.this, folderUnits);
                        listFolder.setAdapter(adapter);
                        getSupportActionBar().setTitle(currentDir);
                    }
                } else {
                    posClicked = position;
                    firstClick = System.currentTimeMillis();
                    if(posClicked != position&&posClicked!=-1){
                        timer.cancel();
                        isShown=1;
                        waiting();
                    }
                    else if(isShown==1) {
                        waiting();
                        isShown=0;
                    }

                }

            }
        });

       mHandler = new Handler(){
            public void handleMessage(Message msg) {
                 AlertDialog.Builder builder = new AlertDialog.Builder(AddNewFolder.this);
                builder.setMessage("Do you want to add this folder?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.e("YES", currentDir+"/"+folderUnits.get(posClicked));
                        General.folderUnits.add(currentDir + "/" + folderUnits.get(posClicked));
                        toUpdate=1;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
    }

    public  List<String> getListFolder(String path){
        List<String> listFolders = new ArrayList<String>();
        File  list = new File(path);
        for(String elem : list.list()){
            File f = new File(path+"/"+elem);
            if(f.isDirectory())listFolders.add(elem);
            else if(f.isFile()){
                if(getFileFormat(elem).equals("mp3"))listFolders.add(elem);
                else if (getFileFormat(elem).equals("wav"))listFolders.add(elem);
            }
        }
        return listFolders;
    }

    public  String getFileFormat(String file){
        return  file.substring(file.lastIndexOf(".")+1, file.length());
    }

    @Override
    public void onBackPressed() {
        if(currentDir.equals(root)){
            Intent returnIntent = new Intent();
            setResult(toUpdate, returnIntent);
            this.finish();
        }
        else{
            currentDir = currentDir.substring(0,currentDir.lastIndexOf("/"));
            folderUnits=getListFolder(currentDir);
            adapter = new ListNewFolderAdapter(AddNewFolder.this, folderUnits);
            listFolder.setAdapter(adapter);
            getSupportActionBar().setTitle(currentDir);
        }
    }

    public void waiting(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
                isShown=1;
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,501,501);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newfolder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Apply:
                Intent returnIntent = new Intent();
                setResult(toUpdate, returnIntent);
                this.finish();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }
}

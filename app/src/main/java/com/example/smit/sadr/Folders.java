package com.example.smit.sadr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.smit.sadr.Adapters.ListFoldersAdapter;

import java.util.ArrayList;
import java.util.List;

public  class Folders extends AppCompatActivity {
    //public static List<String> folderUnits;
    public static ListView listFolder;
    public  static ListFoldersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        getSupportActionBar().setTitle("Folders");
        listFolder = (ListView)findViewById(R.id.listViewFolders);
        adapter = new ListFoldersAdapter(this,  General.folderUnits);
        listFolder.setAdapter(adapter);

        listFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Folders.this);
                builder.setMessage("Do you  want delete this folder?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        General.folderUnits.remove(position);
                        adapter = new ListFoldersAdapter(Folders.this,  General.folderUnits);
                        listFolder.setAdapter(adapter);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog =  builder.create();
                alertDialog.show();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.NewFolder:
                Intent AddNewFoldersActivity = new Intent(this,AddNewFolder.class);
                startActivityForResult(AddNewFoldersActivity,1);
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                adapter = new ListFoldersAdapter(this,  General.folderUnits);
                listFolder.setAdapter(adapter);
                break;
        }

    }
}

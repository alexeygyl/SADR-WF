package com.example.smit.sadr.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.AddNewFolder;
import com.example.smit.sadr.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListNewFolderAdapter  extends ArrayAdapter<String> {
    private final Context context;
    private List<String> listOfFolders;

    public ListNewFolderAdapter(Context context, List<String> listOfFolders) {
        super(context, R.layout.list_new_folders,listOfFolders);
        this.context = context;
        this.listOfFolders =  listOfFolders;

    }

    public  void updateListOfFolders( List<String> listOfFolders){
        this.listOfFolders =  listOfFolders;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_new_folders, parent, false);
        TextView folderNPath = (TextView) rowView.findViewById(R.id.NewFolderPath);
        TextView itemCount = (TextView) rowView.findViewById(R.id.NewFolderItemCount);
        ImageView folderIcon = (ImageView) rowView.findViewById(R.id.NewFolderLogo);
        folderNPath.setText(listOfFolders.get(position));
        folderNPath.setTextColor(Color.BLACK);
        folderNPath.setMaxWidth(320);
        File f = new File(AddNewFolder.currentDir+"/"+listOfFolders.get(position));
        if(f.isFile()){
            if(getFileFormat(position).equals("mp3"))folderIcon.setImageResource(R.drawable.mp3_7317);
            else if (getFileFormat(position).equals("wav"))folderIcon.setImageResource(R.drawable.wav_9679);
        }else{
            try{
                Integer count = f.list().length;
                itemCount.setText("Items: "+ Long.toString(count) );
            }catch (NullPointerException e){
               itemCount.setText("Items: 0");
             }
            itemCount.setTextColor(Color.rgb(150, 150, 150));
        }




        return rowView;
    }

    public  String getFileFormat(int position){
        return  listOfFolders.get(position).substring(
                                            listOfFolders.get(position).lastIndexOf(".")+1,
                                            listOfFolders.get(position).length());
    }
}

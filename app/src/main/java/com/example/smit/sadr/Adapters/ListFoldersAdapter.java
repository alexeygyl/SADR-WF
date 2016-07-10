package com.example.smit.sadr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.io.File;
import java.util.List;


public class ListFoldersAdapter extends ArrayAdapter<String> {
    private final Context context;
    private List<String> listOfFolders;

    public ListFoldersAdapter(Context context, List<String> listOfFolders) {
        super(context, R.layout.list_folders,listOfFolders);
        this.context = context;
        this.listOfFolders =  listOfFolders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_folders, parent, false);
        TextView folderNPath = (TextView) rowView.findViewById(R.id.FolderPath);
        TextView itemCount = (TextView) rowView.findViewById(R.id.FolderItemCount);
        ImageView folderIcon = (ImageView) rowView.findViewById(R.id.FolderLogo);

        folderNPath.setText(listOfFolders.get(position));
        folderNPath.setTextColor(Color.BLACK);
        folderNPath.setMaxWidth(320);
        File f = new File(listOfFolders.get(position));
        try{
            Integer count = f.list().length;
            itemCount.setText("Items: "+ Long.toString(count) );
        }catch (NullPointerException e){
            itemCount.setText("Items: 0" );
        }
        itemCount.setTextColor(Color.rgb(150,150,150));


        return rowView;
    }
}

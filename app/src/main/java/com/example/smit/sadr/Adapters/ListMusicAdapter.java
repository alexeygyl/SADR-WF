package com.example.smit.sadr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.util.List;

public class ListMusicAdapter extends ArrayAdapter<MusicUnits>  {
    private final Context context;
    private List<MusicUnits> musicUnitses;

    public ListMusicAdapter(Context context, List<MusicUnits> musicUnitses) {
        super(context, R.layout.list_music,musicUnitses);
        this.context = context;
        this.musicUnitses =  musicUnitses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_music, parent, false);
        TextView textName = (TextView) rowView.findViewById(R.id.MusicName);
        TextView textAuthor = (TextView) rowView.findViewById(R.id.MusicAuthor);
        TextView textTime = (TextView) rowView.findViewById(R.id.MusicTime);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);

        textName.setText(musicUnitses.get(position).Mname.substring(0,musicUnitses.get(position).Mname.lastIndexOf(".")));
        textName.setTextColor(Color.BLACK);
        textName.setMaxWidth(320);
        textAuthor.setText(musicUnitses.get(position).MAuthor);
        textAuthor.setTextColor(Color.rgb(200,200,200));
        textTime.setText(musicUnitses.get(position).Mtime);

        String s = musicUnitses.get(position).MAuthor;

        if (s.substring(0,1).equalsIgnoreCase("A")) {
            imageView.setImageResource(R.drawable.alph_a);
        }else if (s.substring(0,1).equalsIgnoreCase("B")) {
            imageView.setImageResource(R.drawable.alph_b);
        }else if (s.substring(0,1).equalsIgnoreCase("C")) {
            imageView.setImageResource(R.drawable.alph_c);
        } else if (s.substring(0,1).equalsIgnoreCase("D")) {
            imageView.setImageResource(R.drawable.alph_d);
        }else if (s.substring(0,1).equalsIgnoreCase("E")) {
            imageView.setImageResource(R.drawable.alph_e);
        }else if (s.substring(0,1).equalsIgnoreCase("F")) {
            imageView.setImageResource(R.drawable.alph_f);
        }else if (s.substring(0,1).equalsIgnoreCase("G")) {
            imageView.setImageResource(R.drawable.alph_g);
        }else if (s.substring(0,1).equalsIgnoreCase("H")) {
            imageView.setImageResource(R.drawable.alph_h);
        }else if (s.substring(0,1).equalsIgnoreCase("I")) {
            imageView.setImageResource(R.drawable.alph_i);
        }else if (s.substring(0,1).equalsIgnoreCase("J")) {
            imageView.setImageResource(R.drawable.alph_j);
        }else if (s.substring(0,1).equalsIgnoreCase("K")) {
            imageView.setImageResource(R.drawable.alph_k);
        }
        else if (s.substring(0,1).equalsIgnoreCase("S")) {
            imageView.setImageResource(R.drawable.alph_s);
        }
        else if (s.substring(0,1).equalsIgnoreCase("M")) {
            imageView.setImageResource(R.drawable.alph_m);
        }
        else if (s.substring(0,1).equalsIgnoreCase("X")) {
            imageView.setImageResource(R.drawable.alph_x);
        }
        else if (s.substring(0,1).equalsIgnoreCase("Z")) {
            imageView.setImageResource(R.drawable.alph_z);
        }
        return rowView;
    }
}

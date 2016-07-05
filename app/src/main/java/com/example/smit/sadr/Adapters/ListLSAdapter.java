package com.example.smit.sadr.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smit.sadr.General;
import com.example.smit.sadr.LoudspeakerUnits;
import com.example.smit.sadr.MainActivity;
import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.util.List;

public class ListLSAdapter extends ArrayAdapter<LoudspeakerUnits> {
    private final Context context;
    private List<LoudspeakerUnits> loudspeakerUnitses;
    General general = new General();
    public ListLSAdapter(Context context, List<LoudspeakerUnits> loudspeakerUnitses) {
        super(context, R.layout.list_loudspeakers,loudspeakerUnitses);
        this.context = context;
        this.loudspeakerUnitses =  loudspeakerUnitses;
   // Toast.makeText(getContext(),"asd", Toast.LENGTH_LONG).show();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_loudspeakers, parent, false);
        TextView LSName = (TextView) rowView.findViewById(R.id.LSName);
        TextView LSip = (TextView) rowView.findViewById(R.id.LSip);
        Switch startStopLS = (Switch) rowView.findViewById(R.id.startStopLS);
        TextView LScanal = (TextView) rowView.findViewById(R.id.LSCanal);
        Switch LSTurn = (Switch)rowView.findViewById(R.id.startStopLS);
        ImageView LSStatus = (ImageView) rowView.findViewById(R.id.LSstatus);

        LSName.setText(loudspeakerUnitses.get(position).name);
        LSName.setTextColor(Color.BLACK);
        LSName.setMaxWidth(320);
        LScanal.setText(Integer.toString(loudspeakerUnitses.get(position).canal));
        LScanal.setTextSize(24);
        LSip.setText(loudspeakerUnitses.get(position).ip);
        if(loudspeakerUnitses.get(position).turn== General.TURN_ON)LSTurn.setChecked(true);

        // textAuthor.setText(musicUnitses.get(position).MAuthor);
        //textAuthor.setTextColor(Color.rgb(200,200,200));
        //textTime.setText(musicUnitses.get(position).Mtime);

        //String s = musicUnitses.get(position).MAuthor;

        if(loudspeakerUnitses.get(position).status==General.INIT)LSStatus.setBackgroundResource(R.drawable.status_init);
        else if(loudspeakerUnitses.get(position).status==General.INIT_ACK)LSStatus.setBackgroundResource(R.drawable.status_init_ack);
        else if(loudspeakerUnitses.get(position).status==General.SYNC)LSStatus.setBackgroundResource(R.drawable.status_syn);


        LSTurn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(loudspeakerUnitses.get(position).status!=General.SYNC) {
                        General.sendInitAck(position);
                        loudspeakerUnitses.get(position).status = General.INIT_ACK;
                        loudspeakerUnitses.get(position).turn = General.TURN_ON;
                        loudspeakerUnitses.get(position).id = position;
                    }else loudspeakerUnitses.get(position).turn = General.TURN_ON;
                }else{
                    loudspeakerUnitses.get(position).turn=General.TURN_OFF;
                }
            }
        });

        return rowView;
    }
}

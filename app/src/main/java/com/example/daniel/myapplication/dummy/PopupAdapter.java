package com.example.daniel.myapplication.dummy;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.daniel.myapplication.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Daniel on 4/4/2015.
 */
public class PopupAdapter implements InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;

    public PopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popup==null){
            popup = inflater.inflate(R.layout.popup,null);
        }
        String title = marker.getTitle();
        String snippit = marker.getSnippet();

        String delim = "[;]";
        String[] tokens = snippit.split(delim);

        TextView tv1 = (TextView) popup.findViewById(R.id.textView);
        tv1.setText(title);
        TextView tv2 = (TextView) popup.findViewById(R.id.textView2);
        if(tokens[0]!=null) tv2.setText(tokens[0]);
        else tv2.setText("");
        TextView tv3 = (TextView) popup.findViewById(R.id.textView3);
        if(tokens[0]!=null) tv3.setText(tokens[1]);
        else tv3.setText("");
        return popup;
    }
}

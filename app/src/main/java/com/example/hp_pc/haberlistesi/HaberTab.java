package com.example.hp_pc.haberlistesi;

import java.io.InputStream;
import java.net.URL;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HaberTab extends Fragment{

    public static ListView lv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.habertab, container, false);
        lv = rootView.findViewById(R.id.haberlist);


        return rootView;
    }



}

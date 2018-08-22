package com.example.hp_pc.haberlistesi;

import android.graphics.Bitmap;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] baslik;
    private final String[] altmetin;
    private final String[] yazar;
    private final Bitmap[] bitmap;

    public CustomListAdapter(Activity context, String[] baslik,String[] altmetin,String[] yazar, Bitmap[] bitmap) {
        super(context, R.layout.tekhaber, baslik);
       

        this.context=context;
        this.baslik=baslik;
        this.altmetin= altmetin;
        this.yazar=yazar;
        this.bitmap=bitmap;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.tekhaber, null,true);

        TextView baslikT = (TextView) rowView.findViewById(R.id.haberbaslik);
        ImageView imageViewT = (ImageView) rowView.findViewById(R.id.imageView);
        TextView yazarT = (TextView) rowView.findViewById(R.id.yazar);
        TextView altmetinT = (TextView) rowView.findViewById(R.id.haberaltmetin);

        baslikT.setText(baslik[position]);
        imageViewT.setImageBitmap(bitmap[position]);
        altmetinT.setText(altmetin[position]);
        yazarT.setText(yazar[position]);
        return rowView;

    };

}

package com.example.hp_pc.haberlistesi;

import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class HaberTab extends Fragment{

    public static ListView lv;
    private TextView baslikTw;
    private TextView altmetinTw;
    private TextView aciklamaTw;
    private TextView idtext;
    private String idTxt;
    private TextView yazarTw;
    private TextView toobarText;
    private ImageView detayresim;
    private Button geri;
    private Bitmap tmpresim;
    private String[] textviews = new String[4];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.habertab, container, false);
        lv = rootView.findViewById(R.id.haberlist);
        baslikTw = rootView.findViewById(R.id.baslik);
        altmetinTw = rootView.findViewById(R.id.altmetin);
        detayresim = rootView.findViewById(R.id.imageView2);
        aciklamaTw = rootView.findViewById(R.id.aciklama);
        yazarTw = rootView.findViewById(R.id.yazardetay);
        geri = rootView.findViewById(R.id.geribut);
        toobarText = rootView.findViewById(R.id.tbtextnews);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        idtext=rootView.findViewById(R.id.newsid);
                        idTxt = idtext.getText().toString();
                        geri.setVisibility(View.VISIBLE);
                        baslikTw.setVisibility(View.VISIBLE);
                        toobarText.setText("Haber Detayı");
                        altmetinTw.setVisibility(View.VISIBLE);
                        detayresim.setVisibility(View.VISIBLE);
                        aciklamaTw.setVisibility(View.VISIBLE);
                        aciklamaTw.setMovementMethod(new ScrollingMovementMethod());
                        yazarTw.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.INVISIBLE);
                        new GetNewsDetail().execute();
            }
        });
        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv.setVisibility(View.VISIBLE);
                altmetinTw.setVisibility(View.INVISIBLE);
                detayresim.setVisibility(View.INVISIBLE);
                aciklamaTw.setVisibility(View.INVISIBLE);
                yazarTw.setVisibility(View.INVISIBLE);
                geri.setVisibility(View.INVISIBLE);
                baslikTw.setVisibility(View.INVISIBLE);
                toobarText.setText("Haber Listesi");
            }
        });
        return rootView;
    }

    private class GetNewsDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://demo5362749.mockable.io/getNewsDetail?id=" + idTxt;

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject altobje = jsonObj.getJSONObject("response");

                    JSONObject haberdetay = altobje.getJSONObject("news");



                        textviews[0] = haberdetay.getString("title");
                        textviews[1] = haberdetay.getString("sub-title");
                        String resimlink = haberdetay.getString("image");
                        textviews[2] = haberdetay.getString("description");
                        textviews[3] = haberdetay.getString("writer");

                        String urlOfImage = resimlink;
                        Bitmap logo = null;
                        try{
                            InputStream is = new URL(urlOfImage).openStream();
                            logo = BitmapFactory.decodeStream(is);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                       tmpresim = logo;



                } catch (final JSONException e) {


                }
            } else {


            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            baslikTw.setText(textviews[0]);
            altmetinTw.setText(textviews[1]);
            detayresim.setImageBitmap(tmpresim);
            aciklamaTw.setText(textviews[2]);
            yazarTw.setText(textviews[3]);
        }

    }

}

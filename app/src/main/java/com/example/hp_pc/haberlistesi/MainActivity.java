package com.example.hp_pc.haberlistesi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference myref = FirebaseDatabase.getInstance().getReference();


    private ProgressDialog pDialog;

    private static String url = "http://demo5362749.mockable.io/getAllNews";

    ArrayList<HashMap<String, String>> HaberList;
    ArrayList<HashMap<String,Bitmap>> ResimList;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        HaberList = new ArrayList<>();
        ResimList = new ArrayList<>();

        new GetNews().execute();

    }

    private class GetNews extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();


            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject altobje = jsonObj.getJSONObject("response");

                    JSONArray haberler = altobje.getJSONArray("newsList");


                    for (int i = 0; i < haberler.length(); i++) {
                        JSONObject hbr = haberler.getJSONObject(i);

                        String id = hbr.getString("id");
                        String baslik = hbr.getString("title");
                        String altmetin = hbr.getString("sub-title");
                        String resimlink = hbr.getString("image");
                        String yazar = hbr.getString("writer");

                        String urlOfImage = resimlink;
                        Bitmap logo = null;
                        try{
                            InputStream is = new URL(urlOfImage).openStream();
                            logo = BitmapFactory.decodeStream(is);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        Bitmap Gresim = logo;

                        HashMap<String, String> tmphbr = new HashMap<>();
                        HashMap<String, Bitmap> tmpresim = new HashMap<>();

                        tmphbr.put("id", id);
                        tmphbr.put("baslik", baslik);
                        tmphbr.put("altmetin", altmetin);
                        tmphbr.put("yazar", yazar);

                        tmpresim.put("resim",Gresim);
                        ResimList.add(tmpresim);
                        HaberList.add(tmphbr);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            String ids[] = new String[HaberList.size()];
            for (int i = 0; i < HaberList.size(); i++) {
                ids[i] = HaberList.get(i).get("id");
                //myref.child("notread").push().setValue(ids[i]);
                CheckData(ids[i]);
            }
            String baslik[] = new String[HaberList.size()];
            for (int i = 0; i < HaberList.size(); i++) {
                baslik[i] = HaberList.get(i).get("baslik");
            }
            String altmetin[] = new String[HaberList.size()];
            for (int i = 0; i < HaberList.size(); i++) {
                altmetin[i] = HaberList.get(i).get("altmetin");
            }
            String yazar[] = new String[HaberList.size()];
            for (int i = 0; i < HaberList.size(); i++) {
                yazar[i] = HaberList.get(i).get("yazar");
            }
            Bitmap Resimler[] = new Bitmap[ResimList.size()];
            for (int i = 0; i < ResimList.size(); i++) {
                Resimler[i] = ResimList.get(i).get("resim");
            }
            CustomListAdapter adapter1 = new CustomListAdapter(MainActivity.this,ids,baslik,altmetin,yazar,Resimler);
            HaberTab.lv.setAdapter(adapter1);
        }

    }

    public void CheckData(final String test){
        final DatabaseReference tmp = myref.child("notread");
        tmp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(test).exists()) {

                        //do ur stuff
                    } else {

                        final DatabaseReference tmp2 = myref.child("readed");
                        tmp2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(test).exists())
                                {

                                }else
                                {
                                    Map<String, String> mapdata = new HashMap<String, String>();

                                    mapdata.put(test,"id" + test);

                                    myref.child("notread").child(test).push().setValue(mapdata);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                    }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
                switch (position){
                        case 0:
                            HaberTab tab1 = new HaberTab();
                            return tab1;
                        case 1:
                            HakkindaTab tab2 = new HakkindaTab();
                            return tab2;
                        default:
                            return null;

                }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}

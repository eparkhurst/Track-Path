package com.elijahparkhurst.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final String MAP_API_URL = "http://trackpath.herokuapp.com";
    private static final String TAG = "MapsActivity";
    private ArrayList<String> allMaps = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    HashMap<Integer, Object> mapper = new HashMap<Integer, Object>();
    ProgressBar progressBar;
    SharedPreferences prefs = null;
    private int userId;

//    Intent i = new Intent(this, BackgroundService.class);
//    i.putExtra("url", getIntent().getExtras().getString("url"));
//    startService(i);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        prefs = getSharedPreferences("com.elijahparkhurst.capstone", MODE_PRIVATE);



        final ListView myList;
        myList = (ListView)findViewById(R.id.listView);

        toggleRefresh();

        myList.setClickable(true);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String obj = mapper.get((position)).toString();
                Log.i(TAG,"Clicked on an old map");

                try {
                    JSONObject jsonObj = new JSONObject(obj);
                    Intent intent = new Intent(MainActivity.this, OldMapsActivity.class);
                    intent.putExtra("Title", jsonObj.get("title").toString());
                    intent.putExtra("Note", jsonObj.get("note").toString());

                    JSONObject jsonArrayObj = jsonObj.getJSONObject("location");
                    JSONArray jArray = jsonArrayObj.getJSONArray("array");
                    Log.i(TAG, "Array to convert" + jArray.toString());
                    intent.putParcelableArrayListExtra("Location", convertJsonArray(jArray));
//                    intent.putExtra("Reminder_Name", jsonObj.get("name").toString());
//                    intent.putExtra("Reminder_Latitude", Double.parseDouble(jsonObj.get("lat").toString()));
//                    intent.putExtra("Reminder_Longitude", Double.parseDouble(jsonObj.get("long").toString()));
//                    intent.putExtra("Reminder_Radius", Double.parseDouble(jsonObj.get("radius").toString()));
                    startActivity(intent);

                } catch (Exception e) {
                    Log.d("Didgeridoo", "Exception", e);
                }
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, allMaps);
        adapter.clear();
        adapter.notifyDataSetChanged();
        myList.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = prefs.getInt("user_id", 1);
        Log.i(TAG, "From on Resume:" + String.valueOf(userId));
        if(userId > 1){
            new DownloadTask().execute(MAP_API_URL, "GET");
        }else {
            new DownloadTask().execute(MAP_API_URL, "POST");
        }
    }

    public ArrayList convertJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> listdata = new ArrayList<String>();
        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                listdata.add(jsonArray.get(i).toString());
            }
        }
        return listdata;
    }



    private class DownloadTask extends AsyncTask<String, Void, String> {
        String verb ="";

        @Override
        protected String doInBackground(String... params) {
            String url = params[0].toString();
            verb = params[1];
            if(verb.equals("POST")){
                url = url +"/newuser";
            }else if(verb.equals("GET")){
                url = url +"/maps/"+String.valueOf(userId);
                Log.i(TAG, url);
            }

            try {
                return downloadContent(url, verb);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(verb.equals("GET")) {
                adapter.notifyDataSetChanged();
                toggleRefresh();
            }else if(verb.equals("POST")){
                String num = result.substring(1,result.length()-2);
                Log.i(TAG,"From Post Execute:"+num);
                newUser(Integer.parseInt(num));
            }
        }
    }

    public void newUser(int newId){
        prefs.edit().putInt("user_id", newId).commit();
        userId = newId;
        new DownloadTask().execute(MAP_API_URL, "GET");
    }

    private String downloadContent(String myurl, String verb) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(verb);
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(TAG, "The response is: " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = convertStreamToString(is);
            Log.i(TAG, contentAsString);

            try {
                // Parse the entire JSON string
                JSONArray maps = new JSONArray(contentAsString);

                for(int i=0;i<maps.length();i++) {
                    // parse the JSON object into fields and values
                    JSONObject jsonPost = maps.getJSONObject(i);
                    String aPost = jsonPost.getString("title");
                    allMaps.add(aPost);
                    Log.i(TAG, aPost);
                    int position = allMaps.indexOf(aPost);
                    mapper.put(position, jsonPost);
                }

            } catch (Exception e) {
                Log.d("Mapit","Exception",e);
            }

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void toggleRefresh() {
        if (progressBar.getVisibility() == View.INVISIBLE){
            progressBar.setVisibility(View.VISIBLE);
            //mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
           // mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    public void openMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }
}



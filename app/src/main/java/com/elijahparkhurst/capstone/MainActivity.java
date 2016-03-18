package com.elijahparkhurst.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
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

    private static final String MAP_API_URL = "http://thebankjob.herokuapp.com/data";
    private static final String TAG = "MapsActivity";
    private ArrayList<String> allMaps = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    HashMap<Integer, Object> mapper = new HashMap<Integer, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new DownloadTask().execute(MAP_API_URL);

        final ListView myList;
        myList = (ListView)findViewById(R.id.listView);

        myList.setClickable(true);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String obj = mapper.get((position)).toString();

                try {
                    JSONObject jsonObj = new JSONObject(obj);
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                    intent.putExtra("Reminder_User_Id", User_ID);
//                    intent.putExtra("Reminder_Task_Id", jsonObj.get("task_id").toString());
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



    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(TAG, "The response is: " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = convertStreamToString(is);

            try {
                // Parse the entire JSON string
                JSONObject root = new JSONObject(contentAsString);
                JSONArray posts = root.getJSONArray("posts");
                //JSONArray tasks = user.getJSONArray("tasks");

                for(int i=0;i<posts.length();i++) {
                    // parse the JSON object into fields and values
                    JSONObject jsonPost = posts.getJSONObject(i);
                    String aPost = jsonPost.getString("post");
                    allMaps.add(aPost);
                    Log.i(TAG, aPost);
                    int position = allMaps.indexOf(aPost);
                    mapper.put(position, posts);
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



    public void openMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}



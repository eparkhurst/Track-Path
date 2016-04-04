package com.elijahparkhurst.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {

    private final String TAG="MapsActivity";
    public EditText mTitle;
    public EditText mNote;
    public JSONArray jsonLocationArray = new JSONArray();
    private ArrayList locationArray = new ArrayList();
    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        mTitle = (EditText)findViewById(R.id.editTitle);
        mNote = (EditText)findViewById(R.id.editNote);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",1);
        locationArray = intent.getParcelableArrayListExtra("locationData");
        LatLng[] array = new LatLng[locationArray.size()];
        locationArray.toArray(array);
        try {
            for (int i = 0; i < array.length; i++) {
                double lat = array[i].latitude;
                double lng = array[i].longitude;
                JSONObject loc = new JSONObject();
                loc.put("latitude", lat);
                loc.put("longitude", lng);
                jsonLocationArray.put(loc);
            }
        }catch (Exception e) {
        e.printStackTrace();
    }

}


    public void saveIt(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String title = mTitle.getText().toString();
        String note = mNote.getText().toString();
        Log.i(TAG, title + " : " + note);

        try {


            JSONObject test = new JSONObject();
            test.put("array", jsonLocationArray);


            JSONObject obj = new JSONObject();
            obj.put("title", title);
            obj.put("note", note);
            obj.put("location", test);
            obj.put("userId", userId);

            String[] Request_Array = {"POST", obj.toString()};
            new Hit_API().execute(Request_Array);

        } catch (Exception e) {
            e.printStackTrace();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    class Hit_API extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String HTTP_Verb = params[0].toString();
            String HTTP_Data = params[1].toString();
            String HTTP_URL = "";

            if (HTTP_Verb == "POST") {
                HTTP_URL = "http://trackpath.herokuapp.com/newmap";
            }

            try {
                URL url = new URL(HTTP_URL);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod(HTTP_Verb);
                httpCon.setConnectTimeout(15000);
                httpCon.setRequestProperty("Content-type", "application/json");
                OutputStreamWriter out = new OutputStreamWriter( httpCon.getOutputStream() );
                out.write(HTTP_Data);
                out.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                System.out.println("********** RESPONSE:  " + sb.toString());

                httpCon.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }


    }

}

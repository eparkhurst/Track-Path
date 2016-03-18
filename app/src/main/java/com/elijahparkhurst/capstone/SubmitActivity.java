package com.elijahparkhurst.capstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SubmitActivity extends AppCompatActivity {

    private final String TAG="MapsActivity";
    public EditText mTitle;
    public EditText mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        mTitle = (EditText)findViewById(R.id.editTitle);
        mNote = (EditText)findViewById(R.id.editNote);
    }

    public void saveIt(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String title = mTitle.getText().toString();
        String note = mNote.getText().toString();
        Log.i(TAG, title +" : " + note);
        startActivity(intent);
    }
}

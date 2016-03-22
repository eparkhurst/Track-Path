package com.elijahparkhurst.capstone;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by elijahparkhurst on 3/21/16.
 */

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString

    }
}
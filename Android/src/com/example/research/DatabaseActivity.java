package com.example.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DatabaseActivity extends Activity implements MyResultReceiver.Receiver  {

	public MyResultReceiver mReceiver;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mReceiver = new MyResultReceiver(new Handler());
		mReceiver.setReceiver(mReceiver);
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
				QueryService.class);
		intent.putExtra("receiver", mReceiver);
		intent.putExtra("command", "query");
		startService(intent);
	}

	public void onPause() {
		mReceiver.setReceiver(null); // clear receiver so no leaks.
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case 0:
			// RUNNING
			break;
		case 1:
			List results = resultData.getParcelable("results");
			// do something interesting
			// hide progress
			// FINISHED
			break;
		case 2:
			// handle the error;
			break;
		}
	}
}

package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setTextSize(24);
		
//		 Button button = (Button) findViewById(R.id.button1);
//		 button.setOnClickListener(new View.OnClickListener() {
//		 public void onClick(View v) {
//           	Intent myIntent = new Intent(ResultActivity.this, MainActivity.class);
//           	ResultActivity.this.startActivity(myIntent);
//           }
//	        });
//
			Intent intent = getIntent();
			ArrayList<String> val = intent.getExtras().getStringArrayList("result");

			if(val.size() != 0)
				textView.setText(val.get(0));
			
			
	}

}
		        
		    


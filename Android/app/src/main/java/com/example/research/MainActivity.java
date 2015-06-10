package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    /*
    This class is effectively disabled because what this originally did can be done in settings.
     */

    /*
    public static int HIGH = 180;
    public static int LOW = 100;

    public static String PATIENTNAME = "AAAAA";
    public static String PHONENUMBER = "1234567890";
    */

    /*
    EditText patientName;
    EditText phoneNumber;
    ViewFlipper vf;
    NumberPicker highNp;
    NumberPicker lowNp;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_main);
        vf = (ViewFlipper) findViewById(R.id.vf);

        patientName = (EditText)findViewById(R.id.editText);
        phoneNumber = (EditText)findViewById(R.id.editText2);


        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PATIENTNAME = patientName.getText().toString();
                PHONENUMBER = phoneNumber.getText().toString();
                vf.showNext();
            }
        });

        lowNp = (NumberPicker) findViewById(R.id.lowNP);
        lowNp.setMaxValue(300);
        lowNp.setMinValue(0);
        lowNp.setValue(100);
        LOW = lowNp.getValue();

        highNp = (NumberPicker) findViewById(R.id.highNP);
        highNp.setMaxValue(300);
        highNp.setMinValue(0);
        highNp.setValue(180);
        HIGH = highNp.getValue();

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LOW = lowNp.getValue();
                HIGH = highNp.getValue(); */
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
        startActivity(intent);

//            }
//        });

        // We don't need MainActivity, so we call finish to dispose of it.
        finish();
    }

}

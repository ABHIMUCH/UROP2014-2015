package com.example.research;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.InputType;

/**
 * Created by Fishy on 6/9/2015.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        /*
        Set input to numbers only for:
        -------------------------------
        Low blood sugar limit
        High blood sugar limit
        Phone number
        Graph Y axis maximum
        Graph Y axis minimum
         */
        
        EditTextPreference lowBS = (EditTextPreference)findPreference("lowbs");
        lowBS.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        EditTextPreference highBS = (EditTextPreference)findPreference("highbs");
        highBS.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        EditTextPreference phoneno = (EditTextPreference)findPreference("phonenumber");
        phoneno.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        EditTextPreference yMax = (EditTextPreference)findPreference("ymax");
        yMax.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        EditTextPreference yMin = (EditTextPreference)findPreference("ymin");
        yMin.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

    }



}

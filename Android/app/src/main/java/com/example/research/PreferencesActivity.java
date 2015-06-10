package com.example.research;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Fishy on 6/9/2015.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

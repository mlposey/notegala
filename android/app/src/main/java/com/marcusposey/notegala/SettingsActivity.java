package com.marcusposey.notegala;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

/** Displays application settings and information */
public class SettingsActivity extends AppCompatActivity {

    /** Controls the list of preferences below the app bar */
    public static class Preferences extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            // The docs said I could start the activity from the preferences.xml file.
            // The docs lied.
            Preference oss = findPreference(getString(R.string.pref_key_oss));
            oss.setOnPreferenceClickListener(pref -> {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.pref_oss));
                startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
                return true;
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.settings_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_content, new Preferences())
                .commit();
    }
}

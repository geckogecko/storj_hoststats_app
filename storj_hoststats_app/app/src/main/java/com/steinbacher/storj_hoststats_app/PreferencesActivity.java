package com.steinbacher.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by georg on 23.09.17.
 */

public class PreferencesActivity extends AppCompatActivity {
    private static final String TAG = "PreferencesActivity";

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsPreferenceFragment()).commit();

        mContext = getApplicationContext();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(getString(R.string.pref_enable_notifications))) {
                switchPullNodeStatsAlarm(sharedPreferences.getBoolean(key, true));
            }
        }

        private void switchPullNodeStatsAlarm(Boolean enable) {
            AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
            int interval = 1;

            if(enable) {
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            } else {
                manager.cancel(pendingIntent);
            }
        }
    }
}

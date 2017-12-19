package com.steinbacher.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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

            boolean isEnabled = getPreferenceManager().getSharedPreferences().getBoolean("storj_dash_integration_enabled", false);
            EditTextPreference apiKey = (EditTextPreference) findPreference("api_key_edit_text");
            apiKey.setEnabled(isEnabled);
            apiKey.setSummary(getStorjDashAPIKey());

            apiKey.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());

                    boolean isEnabled = getPreferenceManager().getSharedPreferences().getBoolean("storj_dash_integration_enabled", false);

                    if(isEnabled) {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlarmReceiver alarmReceiver = new AlarmReceiver();
                                alarmReceiver.pullStorjDash(mContext);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlarmReceiver alarmReceiver = new AlarmReceiver();
                                        alarmReceiver.pullStorjNodesStats(mContext);

                                        AlarmReceiver alarm = new AlarmReceiver();
                                        alarm.scheduleAlarm(mContext);
                                    }
                                }, 10000);
                            }
                        }, 1000);
                    }

                    return true;
                }
            });
        }

        private String getStorjDashAPIKey() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String apiKey = prefs.getString("api_key_edit_text", "");

            return apiKey;
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
            if(key.equals("pref_enable_notifications")) {
                switchPullNodeStatsAlarm(sharedPreferences.getBoolean(key, true));
            } else if(key.equals("storj_dash_integration_enabled")) {
                boolean isEnabled = sharedPreferences.getBoolean("storj_dash_integration_enabled", false);
                EditTextPreference apiKey = (EditTextPreference) findPreference("api_key_edit_text");
                apiKey.setEnabled(isEnabled);
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

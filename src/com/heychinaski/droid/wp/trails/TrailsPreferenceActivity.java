package com.heychinaski.droid.wp.trails;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TrailsPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(
        TrailsWallpaperService.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.layout.preferences);
	}
}

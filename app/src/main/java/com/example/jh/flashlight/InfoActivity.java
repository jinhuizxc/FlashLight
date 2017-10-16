package com.example.jh.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class InfoActivity extends Activity {

	RelativeLayout info;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		info = (RelativeLayout) findViewById(R.id.info);

		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean skin_dark = preferences.getBoolean("skin_dark", false);

		if (skin_dark) {
			info.setBackgroundResource(R.drawable.skin_dark);
		} else {
			info.setBackgroundResource(R.drawable.skin_light);
		}

	}

}

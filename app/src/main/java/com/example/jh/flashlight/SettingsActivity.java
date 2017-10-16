package com.example.jh.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	RelativeLayout settings;
	CheckBox ledonBox;
	CheckBox soundBox;
	CheckBox skinBox;
	TextView textView_11;
	TextView textView_22;
	TextView textView_33;
	boolean switchon;
	boolean sound;
	boolean skin_dark;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		settings = (RelativeLayout) findViewById(R.id.settings);
		ledonBox = (CheckBox) findViewById(R.id.ledonBox);
		soundBox = (CheckBox) findViewById(R.id.soundBox);
		skinBox = (CheckBox) findViewById(R.id.skinBox);
		textView_11 = (TextView) findViewById(R.id.textView_11);
		textView_22 = (TextView) findViewById(R.id.textView_22);
		textView_33 = (TextView) findViewById(R.id.textView_33);

		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		switchon = preferences.getBoolean("ledon", false);
		sound = preferences.getBoolean("sound", false);
		skin_dark = preferences.getBoolean("skin_dark", false);

		ledonBox.setChecked(switchon);
		soundBox.setChecked(sound);
		skinBox.setChecked(skin_dark);

		if (switchon) {
			textView_11.setText(R.string.open);
		} else {
			textView_11.setText(R.string.close);
		}
		if (sound) {
			textView_22.setText(R.string.open);
		} else {
			textView_22.setText(R.string.close);
		}
		if (skin_dark) {
			textView_33.setText(R.string.open);
		} else {
			textView_33.setText(R.string.close);
		}

		if (skin_dark) {
			settings.setBackgroundResource(R.drawable.skin_dark);
		} else {
			settings.setBackgroundResource(R.drawable.skin_light);
		}

		CheckBoxListener listener = new CheckBoxListener();
		ledonBox.setOnCheckedChangeListener(listener);
		soundBox.setOnCheckedChangeListener(listener);
		skinBox.setOnCheckedChangeListener(listener);

	}

	class CheckBoxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Editor editor = preferences.edit();
			if (buttonView.getId() == R.id.ledonBox) {
				if (isChecked) {
					textView_11.setText(R.string.open);
					editor.putBoolean("ledon", true);
					editor.commit();

				} else {
					textView_11.setText(R.string.close);
					editor.putBoolean("ledon", false);
					editor.commit();
				}
			}

			else if (buttonView.getId() == R.id.soundBox) {
				if (isChecked) {
					textView_22.setText(R.string.open);
					editor.putBoolean("sound", true);
					editor.commit();

				} else {
					textView_22.setText(R.string.close);
					editor.putBoolean("sound", false);
					editor.commit();
				}
				sound = isChecked;
			}

			else if (buttonView.getId() == R.id.skinBox) {
				if (isChecked) {
					settings.setBackgroundResource(R.drawable.skin_dark);
					textView_33.setText(R.string.open);
					editor.putBoolean("skin_dark", true);
					editor.commit();

				} else {
					settings.setBackgroundResource(R.drawable.skin_light);
					textView_33.setText(R.string.close);
					editor.putBoolean("skin_dark", false);
					editor.commit();
				}
				skin_dark = isChecked;
			}

			Intent data = new Intent();
			data.putExtra("sound", sound);
			data.putExtra("skin_dark", skin_dark);
			setResult(1, data);

		}

	}

}

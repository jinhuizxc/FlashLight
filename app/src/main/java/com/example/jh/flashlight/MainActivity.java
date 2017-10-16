package com.example.jh.flashlight;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	RelativeLayout main;
	ImageView ledonoff;
	ImageButton switchonoff;
	Camera camera;
	Parameters parameters;
	ImageButton settings;
	ImageButton info;
	TextView tip;
	SharedPreferences preferences;
	MediaPlayer mediaPlayer;
	MediaPlayer alarmPlayer;
	AudioManager audioManager;
	boolean switchon;
	boolean switchon_2;
	boolean skin_dark;
	boolean sound;
	boolean showTip;
	int currentVolume;
	Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mediaPlayer = MediaPlayer.create(this, R.raw.sound);
		alarmPlayer = MediaPlayer.create(this, R.raw.alarm);

		main = (RelativeLayout) findViewById(R.id.main);
		ledonoff = (ImageView) findViewById(R.id.ledonoff);
		switchonoff = (ImageButton) findViewById(R.id.switchonoff);
		settings = (ImageButton) findViewById(R.id.settings);
		info = (ImageButton) findViewById(R.id.info);
		tip = (TextView) findViewById(R.id.tip);

		ButtonListener buttonListener = new ButtonListener();
		switchonoff.setOnClickListener(buttonListener);
		settings.setOnClickListener(buttonListener);
		info.setOnClickListener(buttonListener);

		LongListener longListener = new LongListener();
		switchonoff.setOnLongClickListener(longListener);

		TouchListener touchListener = new TouchListener();
		settings.setOnTouchListener(touchListener);
		info.setOnTouchListener(touchListener);

		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		switchon = preferences.getBoolean("ledon", false);
		switchon_2 = switchon;
		sound = preferences.getBoolean("sound", false);
		skin_dark = preferences.getBoolean("skin_dark", false);
		showTip = preferences.getBoolean("tips", true);

		Editor editor = preferences.edit();
		editor.putBoolean("tips", false);
		editor.commit();

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		thread = new LooperThread();

		if (switchon) {
			try {
				camera = Camera.open();
				parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameters);
			} catch (Exception ex) {
			}
			ledonoff.setImageDrawable(getResources().getDrawable(
					R.drawable.ledon));

			if (sound) {
				try {
					mediaPlayer.start();
				} catch (Exception e) {
				}
			}
		}

		if (skin_dark) {
			main.setBackgroundResource(R.drawable.skin_dark);
			settings.setImageDrawable(getResources().getDrawable(
					R.drawable.settings_dark));
			info.setImageDrawable(getResources().getDrawable(
					R.drawable.info_dark));

		}

		if (showTip) {
			tip.setText(R.string.longclick);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			sound = data.getExtras().getBoolean("sound");
			skin_dark = data.getExtras().getBoolean("skin_dark");

			if (skin_dark) {
				main.setBackgroundResource(R.drawable.skin_dark);
				settings.setImageDrawable(getResources().getDrawable(
						R.drawable.settings_dark));
				info.setImageDrawable(getResources().getDrawable(
						R.drawable.info_dark));
			} else {
				main.setBackgroundResource(R.drawable.skin_light);
				settings.setImageDrawable(getResources().getDrawable(
						R.drawable.settings_light));
				info.setImageDrawable(getResources().getDrawable(
						R.drawable.info_light));
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.switchonoff) {
				if (switchon) {
					try {
						parameters
								.setFlashMode(Parameters.FLASH_MODE_OFF);
						camera.setParameters(parameters);
						camera.release();
					} catch (Exception ex) {
					}

					switchon = false;

					ledonoff.setImageDrawable(getResources().getDrawable(
							R.drawable.ledoff));

				} else {
					try {
						camera = Camera.open();
						parameters = camera.getParameters();
						parameters
								.setFlashMode(Parameters.FLASH_MODE_TORCH);
						camera.setParameters(parameters);
					} catch (Exception ex) {
					}
					switchon = true;

					ledonoff.setImageDrawable(getResources().getDrawable(
							R.drawable.ledon));
				}

				if (sound) {
					try {
						mediaPlayer.start();
					} catch (Exception e) {
					}
				}

			} else if (v.getId() == R.id.settings) {
				Intent intent = new Intent(MainActivity.this,
						SettingsActivity.class);
				startActivityForResult(intent, 100);

			} else if (v.getId() == R.id.info) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, InfoActivity.class);
				MainActivity.this.startActivity(intent);
			}
		}
	}

	class LongListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			if (v.getId() == R.id.switchonoff) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle(R.string.tip);
				builder.setMessage(R.string.yesornot);
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {

								int max = audioManager
										.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
								audioManager.setStreamVolume(
										AudioManager.STREAM_MUSIC, max, 0);

								try {
									alarmPlayer.setVolume(1, 1);
									alarmPlayer.start();
									alarmPlayer.setLooping(true);
								} catch (Exception e) {
								}

								thread.start();

							}
						});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
				builder.create().show();

			}
			return true;
		}
	}

	class LooperThread extends Thread {
		public void run() {
			try {
				Timer timer = new Timer();
				TimerTask timerTask = new TimerTask() {
					public void run() {
						try {
							camera = Camera.open();
							parameters = camera.getParameters();
							parameters
									.setFlashMode(Parameters.FLASH_MODE_TORCH);
							camera.setParameters(parameters);
						} catch (Exception ex) {
						}
						try {
							parameters
									.setFlashMode(Parameters.FLASH_MODE_OFF);
							camera.setParameters(parameters);
							camera.release();
						} catch (Exception ex) {
						}
					}
				};
				timer.schedule(timerTask, new Date(), 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class TouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.settings) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					settings.setImageDrawable(getResources().getDrawable(
							R.drawable.settings_down));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (skin_dark) {
						settings.setImageDrawable(getResources().getDrawable(
								R.drawable.settings_dark));
					} else {
						settings.setImageDrawable(getResources().getDrawable(
								R.drawable.settings_light));
					}
				}
			} else if (v.getId() == R.id.info) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					info.setImageDrawable(getResources().getDrawable(
							R.drawable.info_down));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (skin_dark) {
						info.setImageDrawable(getResources().getDrawable(
								R.drawable.info_dark));
					} else {
						info.setImageDrawable(getResources().getDrawable(
								R.drawable.info_light));
					}
				}
			}
			return false;
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentVolume, 0);
			System.exit(0);
		}
		return false;
	}
}

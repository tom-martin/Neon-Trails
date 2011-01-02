package com.heychinaski.droid.wp.trails;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Wallpaper Service adapted from http://blog.androgames.net/58/android-live-wallpaper-tutorial/
 * @author tomm
 *
 */
public class TrailsWallpaperService extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "com.heychinaski.droid.wp.trails.NeonTrailsPrefs";
	
	@Override
	public Engine onCreateEngine() {
		ThreadDelegatingEngine threadDelegatingEngine = new ThreadDelegatingEngine();
		return threadDelegatingEngine;
	}

	private class ThreadDelegatingEngine extends Engine implements OnSharedPreferenceChangeListener {
		
		private DrawingThread drawingThread;

		public ThreadDelegatingEngine() {
			drawingThread = new TrailsDrawingThread(getSurfaceHolder(), getApplicationContext());
			
			SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, 0);
	    prefs.registerOnSharedPreferenceChangeListener(this);
	    
	    onSharedPreferenceChanged(prefs, null);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			// register listeners and callbacks here
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			drawingThread.stopPainting();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				// register listeners and callbacks here
				drawingThread.resumePainting();
			} else {
				// remove listeners and callbacks here
				drawingThread.pausePainting();
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, 
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			drawingThread.setSurfaceSize(getDesiredMinimumWidth(), getDesiredMinimumHeight());
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			// start painting
			drawingThread.start();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			boolean retry = true;
			drawingThread.stopPainting();
			while (retry) {
				try {
					drawingThread.join();
					retry = false;
				} catch (InterruptedException e) {}
			}
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, 
				float xStep, float yStep, int xPixels, int yPixels) {
			drawingThread.setOffset(xPixels);
		}
		
		@Override
		public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
			super.onDesiredSizeChanged(desiredWidth, desiredHeight);
			drawingThread.setSurfaceSize(getDesiredMinimumWidth(), getDesiredMinimumHeight());
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			drawingThread.doTouchEvent(event);
		}
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			int framesPerSecond = Integer.parseInt(prefs.getString("frames_per_second", "30"));
			drawingThread.setFramesPerSecond(framesPerSecond);
		}
	}
}


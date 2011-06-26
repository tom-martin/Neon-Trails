package com.heychinaski.droid.wp.trails;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.heychinaski.droid.wp.trails.TrailsDrawingThread.LineWidth;

/**
 * Wallpaper Service adapted from http://blog.androgames.net/58/android-live-wallpaper-tutorial/
 * @author tomm
 *
 */
public class TrailsWallpaperService extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "com.heychinaski.droid.wp.trails.NeonTrailsPrefs";
	
	public boolean startOnSurfaceChanged = false;
	
	@Override
	public Engine onCreateEngine() {
		ThreadDelegatingEngine threadDelegatingEngine = new ThreadDelegatingEngine();
		return threadDelegatingEngine;
	}

	private class ThreadDelegatingEngine extends Engine implements OnSharedPreferenceChangeListener {
		
		private TrailsDrawingThread drawingThread;
		private int desiredMinimumWidth;
		private int desiredMinimumHeight;

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
			desiredMinimumWidth = getDesiredMinimumWidth();
			desiredMinimumHeight = getDesiredMinimumHeight();
			
			/*
			 * If the lazy/incompetent Samsung developers won't tell us what dimensions their home screen
			 * desires, we'll just pick one (twice the width and the same height)
			 */
			desiredMinimumWidth = desiredMinimumWidth <= 0 ? width * 2 : desiredMinimumWidth;
			desiredMinimumHeight = desiredMinimumHeight <= 0 ? height : desiredMinimumHeight;
			
			drawingThread.setSurfaceSize(desiredMinimumWidth, desiredMinimumHeight);
			if(startOnSurfaceChanged && desiredMinimumWidth > 1 && desiredMinimumHeight > 1) {
				// start painting
				drawingThread.start();
				startOnSurfaceChanged = false;
			}
			
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			
			// Start once we get told about the surface size.
			startOnSurfaceChanged = true;
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
			
			/*
			 * Lazy Samsung developers/incompetent don't seem to be setting xPixels in their implementation of
			 * their home screen.  If it's not set then we'll try and mimick what the implementation
			 * might have been doing (I have worked this out from trial and error).
			 */
			if(xPixels <= 0 && xOffset > 0 && desiredMinimumWidth > 0 && desiredMinimumHeight > 0) {
				xPixels = -Math.round(desiredMinimumWidth * (xOffset / 2));
			}
			
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
			
			String lineLength = prefs.getString("line_length", "20");
			drawingThread.setLineLength(Integer.parseInt(lineLength));
			
			String lineWidth = prefs.getString("line_width", "MEDIUM");
			drawingThread.setLineWidth(LineWidth.valueOf(lineWidth));
			
			int trail_speed = Integer.parseInt(prefs.getString("trail_speed", "2000"));
			drawingThread.setAdvanceTime(trail_speed);
			
			String colorScheme = prefs.getString("color_scheme", "ANY");
			drawingThread.setColorGenerator(ColorScheme.valueOf(colorScheme).getColorGenerator());
		}
	}
}


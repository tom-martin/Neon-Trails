package com.heychinaski.droid.wp.trails;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Wallpaper Service adapted from http://blog.androgames.net/58/android-live-wallpaper-tutorial/
 * @author tomm
 *
 */
public class TrailsWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new ThreadDelegatingEngine();
	}

	private class ThreadDelegatingEngine extends Engine {
		private DrawingThread drawingThread;

		public ThreadDelegatingEngine() {
			drawingThread = new TrailsDrawingThread(getSurfaceHolder(), getApplicationContext());
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
	}

}


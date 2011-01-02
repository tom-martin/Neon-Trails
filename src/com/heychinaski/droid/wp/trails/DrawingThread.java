package com.heychinaski.droid.wp.trails;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Abstract thread that take care of drawing a wallpaper.
 * Adapted from http://blog.androgames.net/58/android-live-wallpaper-tutorial/
 * 
 * @author tomm
 *
 */
public abstract class DrawingThread extends Thread {

	private boolean run;
	private boolean wait;
	private SurfaceHolder surfaceHolder;
	private Context context;
	protected int width;
	protected int height;
	private long currentTime;
	private long previousTime;
	private Paint debugPaint;
	protected int offset;

	public DrawingThread(SurfaceHolder surfaceHolder, Context applicationContext) {
		// keep a reference of the context and the surface
		// the context is needed if you want to inflate
		// some resources from your livewallpaper .apk
		this.surfaceHolder = surfaceHolder;
		this.context = applicationContext;
		this.wait = true;


		debugPaint = new Paint();
		debugPaint.setARGB(255, 255, 0, 0);
	}

	public void stopPainting() {
		run = false;
		synchronized(this) {
			this.notify();
		}
	}

	/**
	 * Pauses the live wallpaper animation
	 */
	public void pausePainting() {
		this.wait = true;
		synchronized(this) {
			this.notify();
		}
	}


	public void resumePainting() {
		this.wait = false;
		synchronized(this) {
			this.notify();
		}
	}

	public void setSurfaceSize(int width, int height) {
		this.width = width;
		this.height = height;
		synchronized(this) {
			this.notify();
		}
	}

	public void doTouchEvent(MotionEvent event) {
		// handle the event here
		// if there is something to animate
		// then wake up
		this.wait = false;
		synchronized(this) {
			notify();
		}
	}

	@Override
	public void run() {
		this.run = true;
		Canvas c = null;
		while (run) {
			try {
				c = this.surfaceHolder.lockCanvas(null);
				synchronized (this.surfaceHolder) {
					currentTime = System.currentTimeMillis();

					updatePhysicis(previousTime, currentTime);
					draw(c, previousTime, currentTime);

					previousTime = currentTime;
				}
			} finally {
				if (c != null) {
					this.surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			// pause if no need to animate
			synchronized (this) {
				if (wait) {
					try {
						wait();
					} catch (Exception e) {}
				}
			}
		}
	}

	public abstract void updatePhysicis(long previousTime2, long currentTime2);
	
	public abstract void draw(Canvas c, long previousTime, long currentTime);

	/**
	 * Currently only dealing with x offset.
	 * @param xPixels
	 */
	public void setOffset(int xPixels) {
		this.offset = xPixels;
	}
}

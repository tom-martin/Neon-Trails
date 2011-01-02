package com.heychinaski.droid.wp.trails;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
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

	private static final int DEFAULT_FRAMES_PER_SECOND = 30;
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
	
	private int framesPerSecond = DEFAULT_FRAMES_PER_SECOND;

//	private FrameRateCalculator fpsCalculator = new FrameRateCalculator();
	
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
			long thisTick = 0;
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
				} else {
					try {
						thisTick = System.currentTimeMillis() - currentTime;
						wait(Math.max(1, (1000 / framesPerSecond) - thisTick));
						// uncomment to get a log of the frames per second
						fpsCalculator.calculateFPS();
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
	
	/**
	 * Adapted from http://mycodelog.com/2010/04/16/fp
	 * @author tomm
	 *
	 */
	public static class FrameRateCalculator {
		private int frameCount;
		private float fps;
		private long previousTime;
		private long currentTime;

			//-------------------------------------------------------------------------
			// Calculates the frames per second
			//-------------------------------------------------------------------------
			public void calculateFPS() {
			    //  Increase frame count
			    frameCount++;
		
			    //  Get the number of milliseconds since glutInit called
			    //  (or first call to glutGet(GLUT ELAPSED TIME)).
			    currentTime = System.currentTimeMillis();
		
			    //  Calculate time passed
			    long timeInterval = currentTime - previousTime;
		
			    if(timeInterval > 1000) {
			        //  calculate the number of frames per second
			        fps = frameCount / (timeInterval / 1000.0f);
		
			        //  Set time
			        previousTime = currentTime;
		
			        //  Reset frame count
			        frameCount = 0;
			        
			        Log.d("Frame rate:", "fps: " + fps);
			    }
			}
		}

	public void setFramesPerSecond(int framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}
}

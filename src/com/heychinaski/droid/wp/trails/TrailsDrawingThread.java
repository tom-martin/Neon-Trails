package com.heychinaski.droid.wp.trails;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Implementation of DrawingThread that draws the trails.
 * @author tomm
 *
 */
public class TrailsDrawingThread extends DrawingThread implements RenderContext {
	
	private final static int ADVANCE_TIME = 1000;
	private static final int MAX_TRAILS = 200;
	private Grid grid = null;
	private int newTrailsRequired = 10;
	
	private static final int LINE_LENGTH = 20;
	
	private List<Trail> trails = new ArrayList<Trail>();
	private List<TrailView> trailViews = new ArrayList<TrailView>();

	/** If true we should redraw the old (terminated) trails cache next time we render*/
	private boolean recreateCache = true;
	/** Terminated trails don't move so we don't need to draw them on every frame.  We 
	 * draw them into a Bitmap buffer and only update that when necessary */
	private OldTrailsImageCache oldTrailsImageCache = new OldTrailsImageCache();
	private Paint bitmapPaint = new Paint();
	
	public TrailsDrawingThread(SurfaceHolder surfaceHolder,
			Context applicationContext) {
		super(surfaceHolder, applicationContext);
	}
	
	@Override
	public void updatePhysicis(long previousTime, long currentTime) {
		if(grid == null) {
			return;
		}
		
		if(newTrailsRequired > 0) {
			if(trails.size() > MAX_TRAILS) {
				if(recycleTrail()) {
					newTrailsRequired --;
				}
			} else {
				if(createNewTrail()) {
					newTrailsRequired --;
				}
			}
		}
		
		for(int i = 0; i < trails.size(); i++) {
			Trail trail = trails.get(i);
			if(currentTime - trail.getLastAdvanceTime() > ADVANCE_TIME) {
				if(!trail.isTerminated()) {
					trail.advance();

					if(trail.isTerminated()) {
						recreateCache  = true;
						newTrailsRequired ++;
					} else {
						trail.setLastAdvanceTime(currentTime);
					}
				}
			}
		}
	}

	private boolean recycleTrail() {
		Trail oldTrail = trails.get(0);
		TrailView trailView = trailViews.get(0);
		
		if(trailView.getTrail() != oldTrail) {
			throw new RuntimeException("Wrong trail view");
		}
		
		oldTrail.reset();
		
		trails.remove(oldTrail);
		trailViews.remove(trailView);
		
		trails.add(oldTrail);
		trailViews.add(trailView);
		
		recreateCache = true;
		return !oldTrail.isTerminated();
	}

	private boolean createNewTrail() {
		Trail trail = new Trail(grid);
		TrailView newTrailView = new TrailView(this, trail);
		trail.advance();
		
		trails.add(trail);
		trailViews.add(newTrailView);
		
		return !trail.isTerminated();
	}

	@Override
	public void draw(Canvas c, long previousTime, long currentTime) {
		c.save();
		c.translate(offset, 0);
		
		c.drawColor(0xFF000000);

		if(recreateCache) {
			oldTrailsImageCache.recreateCache();
		}
		recreateCache = false;
		
		Bitmap cacheImage = oldTrailsImageCache.getCache();
		if(cacheImage != null) {
			c.drawBitmap(cacheImage, 0, 0, bitmapPaint);
		}
			
		for(int i = 0; i < trailViews.size(); i++) {
			TrailView trailView = trailViews.get(i);
			
			if(!trailView.getTrail().isTerminated()) {
				trailView.draw(c, previousTime, currentTime);
			}
		}
		
		c.restore();
	}

	@Override
	public void convertGridPointToCanvasSpace(Point gridPoint) {
		gridPoint.x *= LINE_LENGTH;
		gridPoint.y *= LINE_LENGTH;
		
		gridPoint.x += LINE_LENGTH;
		gridPoint.y += LINE_LENGTH;
	}
	
	@Override
	public void setSurfaceSize(int width, int height) {
		super.setSurfaceSize(width, height);
		
		final int gridWidth = (width / LINE_LENGTH) - 1;
		final int gridHeight = (height / LINE_LENGTH) - 1;
		
		if(grid == null) {
			grid = new Grid(gridWidth, gridHeight);
		} else {
			grid.setWidth(gridWidth);
			grid.setHeight(gridHeight);
		}
	}

	@Override
	public int getAdvanceTime() {
		return ADVANCE_TIME;
	}
	
	@Override
	public int getLineLength() {
		return LINE_LENGTH;
	}
	
	@Override
	public int getLineWidth() {
		return LINE_LENGTH / 2;
	}
	
	private class OldTrailsImageCache extends ImageCache {
		private Canvas cacheCanvas;

		@Override
		public void recreateCache() {
			Log.d("Cache", "Recreating cache");
			Bitmap bitmap = null;
			if(cache != null) {
				cache.get();
			}
			
			if(bitmap == null) {
				Log.d("Cache", "Reallocating bitmap");
				bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
				cacheCanvas = new Canvas(bitmap);
			}
			
			for(int i = 0; i < trailViews.size(); i++) {
				TrailView trailView = trailViews.get(i);
				
				if(trailView.getTrail().isTerminated()) {
					trailView.draw(cacheCanvas, -1, -1);
				}
			}
			
			cache = new SoftReference<Bitmap>(bitmap);
		}

	}
}

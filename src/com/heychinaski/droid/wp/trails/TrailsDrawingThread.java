package com.heychinaski.droid.wp.trails;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.view.SurfaceHolder;

import com.heychinaski.droid.wp.trails.color.ColorGenerator;
import com.heychinaski.droid.wp.trails.color.RandomColorGenerator;

/**
 * Implementation of DrawingThread that draws the trails.
 * @author tomm
 *
 */
public class TrailsDrawingThread extends DrawingThread implements RenderContext {
	
	public enum LineWidth {
		THIN, MEDIUM, THICK
	}
	
	private final static int DEFAULT_ADVANCE_TIME = 2000;
	private static final int MAX_TRAILS = 200;
	private Grid grid = null;
	private int newTrailsRequired = 5;
	
	private static final int DEFAULT_LINE_LENGTH = 20;
	
	private LineWidth lineWidth = LineWidth.THIN;
	
	private int advanceTime = DEFAULT_ADVANCE_TIME;
	
	private int lineLength = DEFAULT_LINE_LENGTH;
	
	private List<Trail> trails = new ArrayList<Trail>();
	private List<TrailView> trailViews = new ArrayList<TrailView>();

	/** If true we should redraw the old (terminated) trails cache next time we render*/
	private boolean recreateCache = true;
	/** Terminated trails don't move so we don't need to draw them on every frame.  We 
	 * draw them into a Bitmap buffer and only update that when necessary */
	private OldTrailsImageCache oldTrailsImageCache = new OldTrailsImageCache();
	private Paint bitmapPaint = new Paint();
	
	private Rect cacheSrc = new Rect();
	private Rect cacheDest = new Rect();
	
	private ColorGenerator colorGenerator = new RandomColorGenerator();
	
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
			if(currentTime - trail.getLastAdvanceTime() > advanceTime) {
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

		if(recreateCache) {
			oldTrailsImageCache.recreateCache();
		}
		recreateCache = false;
		
		Bitmap cacheImage = oldTrailsImageCache.getCache();
		if(cacheImage != null) {
			cacheSrc.set(-offset, 0, (-offset) + c.getWidth(), c.getHeight());
			cacheDest.set(0, 0, c.getWidth(), c.getHeight());
			c.drawBitmap(cacheImage, cacheSrc, cacheDest, bitmapPaint);
		}
		
		c.translate(offset, 0);
		
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
		gridPoint.x *= lineLength;
		gridPoint.y *= lineLength;
		
		gridPoint.x += lineLength;
		gridPoint.y += lineLength;
	}
	
	@Override
	public void setSurfaceSize(int width, int height) {
		super.setSurfaceSize(width, height);
		
		final int gridWidth = (width / lineLength) - 1;
		final int gridHeight = (height / lineLength) - 1;
		
		if(grid == null) {
			grid = new Grid(gridWidth, gridHeight);
		} else {
			grid.setWidth(gridWidth);
			grid.setHeight(gridHeight);
		}
	}

	@Override
	public int getAdvanceTime() {
		return advanceTime;
	}
	
	@Override
	public int getLineLength() {
		return lineLength;
	}
	
	@Override
	public int getLineWidthInPixels() {
		if(lineWidth == LineWidth.THIN) {
			return lineLength / 4;
		} else if(lineWidth == LineWidth.THICK) {
			return lineLength - 2;
		} else {
			return lineLength/ 2;
		}
	}
	
	public void setLineWidth(LineWidth lineWidth) {
		if(lineWidth != this.lineWidth) {
			this.lineWidth = lineWidth;
			recreateCache = true;
		}
	}
	
	private class OldTrailsImageCache extends ImageCache {
		private Canvas cacheCanvas;

		@Override
		public void recreateCache() {
//			Log.d("Cache", "Recreating cache");
			Bitmap bitmap = null;
			if(cache != null) {
				bitmap = cache.get();
			}
			
			if(bitmap == null) {
//				Log.d("Cache", "Reallocating bitmap");
				bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
				cacheCanvas = new Canvas(bitmap);
			}
			
			cacheCanvas.drawColor(0xFF202020);
			
			for(int i = 0; i < trailViews.size(); i++) {
				TrailView trailView = trailViews.get(i);
				
				if(trailView.getTrail().isTerminated()) {
					trailView.draw(cacheCanvas, -1, -1);
				}
			}
			
			cache = new SoftReference<Bitmap>(bitmap);
		}

	}

	public void setAdvanceTime(int advanceTime) {
		this.advanceTime = advanceTime;
	}

	public void setLineLength(int lineLength) {
		if(lineLength != this.lineLength) {
			recreateCache = true;
			this.lineLength = lineLength;
			setSurfaceSize(width, height);
		}
	}

	@Override
	public ColorGenerator getColorGenerator() {
		return colorGenerator;
	}

	public void setColorGenerator(ColorGenerator colorGenerator) {
		if(colorGenerator != this.colorGenerator) {
			this.colorGenerator = colorGenerator;
			recreateCache = true;
		}
	}
}

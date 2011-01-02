package com.heychinaski.droid.wp.trails;

import java.util.List;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

/**
 * Draws one Trail.  Not an Android "View"
 * @author tomm
 *
 */
public class TrailView {
	private static final int GRADIENT_HEAD_START = 20;

	private final Trail trail;
	
	private Point pointA = new Point();
	private Point pointB = new Point();
	
	private Paint paint;
	
	private final RenderContext renderContext;
	
	private final int startColor;
	private final int endColor;
	
	private BlurMaskFilter blurs[] = new BlurMaskFilter[] {
																			new BlurMaskFilter(5, Blur.SOLID),
																			new BlurMaskFilter(10, Blur.SOLID),
																			new BlurMaskFilter(15, Blur.SOLID),
																			new BlurMaskFilter(20, Blur.SOLID), 
																			null};

	public TrailView(RenderContext renderContext, Trail trail) {
		super();
		this.trail = trail;
		this.renderContext = renderContext;
		
		paint = new Paint();
		paint.setStrokeWidth(Math.max(1, renderContext.getLineWidth()));
		paint.setARGB(255, 255, 0, 0);
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeCap(Cap.ROUND);
		
		startColor = Color.rgb(10 + (int)(Math.random() * 255), 10 + (int)(Math.random() * 255), 10 + (int)(Math.random() * 255));
		endColor = Color.rgb(10 + (int)(Math.random() * 255), 10 + (int)(Math.random() * 255), 10 + (int)(Math.random() * 255));
	}
	
	public void draw(Canvas c, long previousTime, long currentTime) {
		List<Point> tail = trail.getTail();
		
		int size = tail.size();
		for(int i = 1; i < size - 1; i++) {
			
			assignColorForSegment(size, i);
		
			
			Point previousPoint = tail.get(i - 1);
			Point currentPoint = tail.get(i);

			drawSegment(c, previousPoint, currentPoint);
		}
		
		renderLastSegment(c, currentTime, tail);
	}

	private void assignColorForSegment(int numberOfSegments, int segmentIndex) {
		if(numberOfSegments > GRADIENT_HEAD_START) {
			paint.setColor(interpColors(startColor, endColor, 255, ((float)segmentIndex) / numberOfSegments));
		} else {
			paint.setColor(interpColors(startColor, endColor, 255, (1f - (((float)numberOfSegments) / 20)) + (((float)segmentIndex) / 20)));
		}
	}

	private void renderLastSegment(Canvas c, long currentTime, List<Point> tail) {
		if(tail.size() > 1) {
			int size = tail.size();
			assignColorForSegment(size , size - 1);
			
			
			
			Point startPoint = tail.get(tail.size() - 2);
			Point endPoint = tail.get(tail.size() - 1);
			
			pointA.set(startPoint.x, startPoint.y);
			pointB.set(endPoint.x, endPoint.y);
			
			renderContext.convertGridPointToCanvasSpace(pointA);
			renderContext.convertGridPointToCanvasSpace(pointB);

			if(!trail.isTerminated()) {
				float timeSinceLastAdvance = Math.min(renderContext.getAdvanceTime(), (float)(currentTime - trail.getLastAdvanceTime()));
				float progressUntilNextAdvanceTime = timeSinceLastAdvance / renderContext.getAdvanceTime();
				pointB.set((int)interp(pointA.x, pointB.x, progressUntilNextAdvanceTime),
						(int)interp(pointA.y, pointB.y, progressUntilNextAdvanceTime));
			}
			
			c.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paint);
			
			if(!trail.isTerminated()) {
				paint.setColor(endColor);
				for(int i = 0; i < blurs.length; i++) {
					paint.setMaskFilter(blurs[i]);
					c.drawCircle(pointB.x, pointB.y, renderContext.getLineWidth(), paint);	
				}
				paint.setMaskFilter(null);
			}
		}
	}

	private void drawSegment(Canvas c, Point previousPoint, Point currentPoint) {
		pointA.set(previousPoint.x, previousPoint.y);
		pointB.set(currentPoint.x, currentPoint.y);
		
		renderContext.convertGridPointToCanvasSpace(pointA);
		renderContext.convertGridPointToCanvasSpace(pointB);
		
		c.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paint);
	}
	
	public int interpColors(int startColor, int endColor, int alpha, float interp) {
		return Color.argb(alpha, 
					(int)interp(Color.red(startColor), Color.red(endColor), interp), 
					(int)interp(Color.green(startColor), Color.green(endColor), interp), 
					(int)interp(Color.blue(startColor), Color.blue(endColor), interp));
	}
	
	public float interp(float start, float end, float interp) {
		return start + ((end - start) * interp);
	}

	public Trail getTrail() {
		return trail;
	}
	
	/**
	 * TODO cache the blurred circle that we draw at the head of the trail?
	 * @author tomm
	 *
	 */
	private class HeadImageCache extends ImageCache {

		@Override
		public void recreateCache() {
		}
		
	}
}

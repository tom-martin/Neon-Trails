package com.heychinaski.droid.wp.trails;

import android.graphics.Point;

import com.heychinaski.droid.wp.trails.color.ColorGenerator;

/**
 * Describes all the high level settings of the wallpaper
 * @author tomm
 *
 */
public interface RenderContext {

	void convertGridPointToCanvasSpace(Point gridPoint);

	int getAdvanceTime();

	int getLineLength();

	int getLineWidthInPixels();
	
	ColorGenerator getColorGenerator();

}

package com.heychinaski.droid.wp.trails;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;

/**
 * Cache of an image.  Uses a soft reference so the cache 
 * may be cleared at any time.
 * @author tomm
 *
 */
public abstract class ImageCache {
	protected SoftReference<Bitmap> cache;
	
	public Bitmap getCache() {
		Bitmap hardReference = null;
		if(cache != null) {
			hardReference = cache.get();
		} else {
			return null;
		}
		
		// Do we need to recreate the cache?
		while(hardReference == null) {
			recreateCache();
			if(cache != null) {
				hardReference = cache.get();
			} else {
				return null;
			}
		}
		
		return hardReference;
	}
	
	public abstract void recreateCache();
	
	public void clearCache() {
		cache.clear();
	}
}

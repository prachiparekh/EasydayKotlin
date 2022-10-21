package com.passiondroid.imageeditorlib.views.imagezoom.graphic;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

/**
 * Fast bitmap drawable. Does not support states. it only
 * support alpha and colormatrix
 * @author alessandro
 *
 */
public class FastBitmapDrawable extends Drawable implements IBitmapDrawable {

	protected Bitmap mBitmap;
	protected Paint mPaint;

	public FastBitmapDrawable( Bitmap b ) {
		mBitmap = b;
		mPaint = new Paint();
		mPaint.setDither( true );
		mPaint.setFilterBitmap( true );
	}
	
	public FastBitmapDrawable(Resources res, InputStream is ){
		this(BitmapFactory.decodeStream(is));
	}

	@Override
	public void draw( Canvas canvas ) {
		canvas.drawBitmap( mBitmap, 0.0f, 0.0f, mPaint );
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha( int alpha ) {
		mPaint.setAlpha( alpha );
	}

	@Override
	public void setColorFilter( ColorFilter cf ) {
		mPaint.setColorFilter( cf );
	}

	@Override
	public int getIntrinsicWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public int getMinimumWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getMinimumHeight() {
		return mBitmap.getHeight();
	}
	
	public void setAntiAlias( boolean value ){
		mPaint.setAntiAlias( value );
		invalidateSelf();
	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}
}
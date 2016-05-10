package com.purplelight.redstar.task;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.purplelight.redstar.R;

import java.lang.ref.WeakReference;

public class DownloadedDrawable extends BitmapDrawable {

	private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

	public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Resources resources, Bitmap placeHolder){
		super(resources, placeHolder);
		bitmapDownloaderTaskReference = new WeakReference<>(bitmapDownloaderTask);
	}

	public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Resources resources){
		super(resources, BitmapFactory.decodeResource(resources, R.drawable.cc_bg_default_image));
		bitmapDownloaderTaskReference = new WeakReference<>(bitmapDownloaderTask);
	}

	public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Resources resources, int placeHoleder){
		super(resources, BitmapFactory.decodeResource(resources, placeHoleder));
		bitmapDownloaderTaskReference = new WeakReference<>(bitmapDownloaderTask);
	}
	
	public BitmapDownloaderTask getBitmapDownloaderTask(){
		return bitmapDownloaderTaskReference.get();
	}
}

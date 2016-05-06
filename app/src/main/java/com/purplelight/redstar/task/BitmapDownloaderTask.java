package com.purplelight.redstar.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.purplelight.redstar.util.ImageHelper;

import java.lang.ref.WeakReference;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	private String url;
	private final WeakReference<ImageView> imageViewReference;
	private BitmapDownloadedListener onBitmapDownloaded;
	
	public String getUrl(){
		return url;
	}
	
	public BitmapDownloaderTask(ImageView imageView){
		imageViewReference = new WeakReference<>(imageView);
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		url = params[0];
		Bitmap bitmap = ImageHelper.getBitmapFromCache(url);
		if (bitmap == null){
			bitmap = ImageHelper.loadBitmapFromNet(url);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap){
		if (isCancelled()){
			bitmap = null;
		}
		ImageView imageView = imageViewReference.get();
		if (imageView != null){
			BitmapDownloaderTask bitmapDownloaderTask = ImageHelper.getBitmapDownloaderTask(imageView);
			if (bitmapDownloaderTask == this){
				if(bitmap != null){
					ImageHelper.addBitmapToCache(url, bitmap);
					if (onBitmapDownloaded != null){
						onBitmapDownloaded.onBitmapDownloaded(bitmap);
					}else{
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		}
	}

	public void setOnBitmapDownloaded(BitmapDownloadedListener onBitmapDownloaded) {
		this.onBitmapDownloaded = onBitmapDownloaded;
	}
}

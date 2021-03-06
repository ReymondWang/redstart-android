package com.purplelight.redstar.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.fastdfs.ClientGlobal;
import com.purplelight.redstar.fastdfs.StorageClient;
import com.purplelight.redstar.fastdfs.StorageServer;
import com.purplelight.redstar.fastdfs.TrackerClient;
import com.purplelight.redstar.fastdfs.TrackerServer;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageHelper {
	private static final String TAG = "ImageHelper";
	private static final int DISK_MAX_SIZE = 512 * 1024 * 1024;// SD 512MB
	public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mcommunity/";

	private final static LruCache<String, Bitmap> mMemoryCache;
	private static SimpleDiskLruCache mDiskCache;
	static{
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap bitmap){
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	public static void initDiskCache(){
		File cacheDir = new File(CACHE_PATH);
		mDiskCache = SimpleDiskLruCache.openCache(cacheDir, DISK_MAX_SIZE);
	}

	public static void clear(){
		mDiskCache.clearCache();
	}

	public static void addBitmapToCache(String key, Bitmap bitmap){
		addBitmapToCache(key, bitmap, Configuration.Image.PNG, false);
	}

	public static void addBitmapToCache(String key, Bitmap bitmap, String format, boolean forceReplaced){
		if (bitmap != null){
			synchronized(mMemoryCache){
				if(forceReplaced){
					mMemoryCache.remove(key);
				}
				Bitmap bmp = mMemoryCache.get(key);
				if (bmp == null || bmp.isRecycled()) {
					Log.i(TAG, "Set bitmap into key:" + key);
					mMemoryCache.put(key, bitmap);
				}
			}
			synchronized (mDiskCache) {
				if (!mDiskCache.containsKey(key) || forceReplaced) {
					Log.i(TAG, "put bitmap into SD key = " + key);
					mDiskCache.put(key, bitmap, format);
				}
			}
		}
	}

	public static void addBitmapToCache(String key, Bitmap bitmap, String format){
		addBitmapToCache(key, bitmap, format, false);
	}

	public static void removeBitmapFromCache(String key){
		synchronized(mMemoryCache){
			mMemoryCache.remove(key);
		}
		synchronized (mDiskCache) {
			if (mDiskCache.containsKey(key)) {
				Log.i(TAG, "remove bitmap into SD key = " + key);
				mDiskCache.remove(key);
			}
		}
	}

	public static String getFormat(String key){
		synchronized (mDiskCache) {
			if (mDiskCache.containsKey(key)) {
				return mDiskCache.getFormat(key);
			}
		}
		return Configuration.Image.PNG;
	}

	public static Bitmap getBitmapFromCache(String key){
		if (key == null){
			return null;
		}

		synchronized (mMemoryCache) {
			Bitmap bitmap = mMemoryCache.get(key);
			if (bitmap != null && !bitmap.isRecycled()) {
				Log.i(TAG, "Get bitmap from key:" + key);
				return bitmap;
			}
		}

		synchronized (mDiskCache){
			Bitmap bitmap = mDiskCache.get(key);
			if (bitmap != null){
				Log.i(TAG, "Get bitmap from SD key:" + key);
				return bitmap;
			}
		}

		return null;
	}

	/**
	 * 生成随机文件名
	 * @return 文件名
     */
	public static String generateRandomFileName(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 根据原始文件明生成缩略图的文件名
	 * @param originalFileName 原始文件名
	 * @return                 缩略图文件名
     */
	public static String generateThumbFileName(String originalFileName){
		return originalFileName + "_thumb";
	}

	/**
	 * 上传图片到服务器，并返回文件名。
	 * @param bitmap        图片
	 * @return             fdfs的文件名
	 * @throws Exception   异常信息
	 */
	public static String upload(Bitmap bitmap, String format) throws Exception{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);
		bitmap.recycle();
		byte[] bytes = output.toByteArray();
		try{
			output.close();
		} catch (Exception ex){
			Log.e(TAG, ex.getMessage());
		}

		ClientGlobal.init();
		long fileLength = bytes.length;

		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer, storageServer);

		NameValuePair[] metaList = new NameValuePair[2];
		metaList[0] = new NameValuePair("fileExtName", format);
		metaList[1] = new NameValuePair("fileLength", String.valueOf(fileLength));

		String[] result = client.upload_file(bytes, format, metaList);
		return result[1];
	}

	public static List<String> updateFromCache(List<String> keyList) throws Exception{
		List<String> fileNameList = new ArrayList<>();

		if (keyList != null && keyList.size() > 0){
			for(String key : keyList){
				fileNameList.add(RedStarApplication.IMAGE + upload(getBitmapFromCache(key), getFormat(key)));
			}
		}

		return fileNameList;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqHeight){
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqHeight){
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapResource(Resources res, int resId, int reqWidth, int reqHeight){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap loadBitmapFromNet(String imageUrlStr) {
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(imageUrlStr);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + imageUrlStr);
				return null;
			}
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					byte[] bytes = getBytes(inputStream);
					return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				} catch (Exception ex){
					Log.e(TAG, ex.getMessage());
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			Log.w(TAG, "Error while retrieving bitmap from " + imageUrlStr + "; " + e.toString());
		}
		return null;
	}

	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // 用数据装
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		// 关闭流一定要记得。
		return outstream.toByteArray();
	}

	public static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView){
		if (imageView != null){
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable){
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	public static boolean cancelPotentialDownload(String url, ImageView imageView){
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
		if (bitmapDownloaderTask != null){
			String bitmapUrl = bitmapDownloaderTask.getUrl();
			if (Validation.IsNullOrEmpty(bitmapUrl)){
				bitmapDownloaderTask.cancel(true);
			}else{
				return false;
			}
		}
		return true;
	}

	public static Bitmap CompressImageToSize(Bitmap image, int height, int width) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inJustDecodeBounds = false;
		int w = image.getWidth();
		int h = image.getHeight();
		int be = 1;
		if (w > h && w > width) {
			be = w / width;
		} else if (w < h && h > height) {
			be = h / height;
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return CompressImage(bitmap);
	}

	public static Bitmap CompressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100 && options <= 0) {
			int i = baos.toByteArray().length / 1024;
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	public static byte[] GetBytesFromBitmap(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	public static void SaveBitmapToFile(Bitmap bitmap, String path, String fileName) throws IOException{
		File dictionary = new File(path);
		if (!dictionary.exists()){
			dictionary.mkdir();
		}
		File file = new File(path, fileName);
		if (file.exists()){
			file.delete();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public static void DeleteFiles(List<String> fileNames){
		if (fileNames != null && fileNames.size() > 0){
			for(String fileName : fileNames){
				ImageHelper.removeBitmapFromCache(fileName);
			}
		}
	}
}

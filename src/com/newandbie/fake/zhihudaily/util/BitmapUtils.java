package com.newandbie.fake.zhihudaily.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;

/**
 * 位图相关的工具类
 * 
 * @author ray
 * 
 */
public class BitmapUtils {
	/**
	 * 从字节数组解码位图，如果必要，则采样
	 * 
	 * @param bitmapData
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] bitmapData,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, opts);

		opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);

		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length,
				opts);
	}

	public static Bitmap decodeSampledBitmapFromFile(File bitmapFile,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opts);

		opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);

		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opts);
	}

	public static Bitmap decodeSampledBitmapFromStream(InputStream bitmapInput,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		// BufferedInputStream 是支持mark的
		// 4.4之后就不需要这么做了
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
				&& !bitmapInput.markSupported()) {
			bitmapInput = new BufferedInputStream(bitmapInput);
		}

		BitmapFactory.decodeStream(bitmapInput, null, opts);

		try {
			bitmapInput.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

		opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);

		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeStream(bitmapInput, null, opts);
	}

	/**
	 * 计算图片采样参数
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		if (reqWidth == 0 || reqHeight == 0) {
			return 1;
		}

		// 图像的原始宽度和高度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// 计算需要的宽高比
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// 选择较小的采样比例，保证最终采样后的图像在两个纬度上都不会太小
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 将位图保存为文件
	 * 
	 * @param bmSrc
	 * @param savePath
	 * @return
	 */
	public static boolean saveBitmapToFile(Bitmap bmSrc, File savePath) {
		if (savePath == null) {
			return false;
		}

		if (!savePath.isFile() || !savePath.canWrite()) {
			return false;
		}
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(savePath));
			// 因为PNG是无损格式，所以忽略质量参数
			bmSrc.compress(CompressFormat.PNG, 0, out);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}

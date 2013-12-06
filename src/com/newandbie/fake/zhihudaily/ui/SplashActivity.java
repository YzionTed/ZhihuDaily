package com.newandbie.fake.zhihudaily.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.ZhihuConsts;
import com.newandbie.fake.zhihudaily.ZhihuService;
import com.newandbie.fake.zhihudaily.provider.ZhihuContract;

/**
 * 欢迎界面
 * 
 * 全屏显示一张图片，并且开启服务，加载最近新闻
 * 
 * @author ray
 * 
 */
public class SplashActivity extends Activity {
	private ImageView mImgSplash;
	private ScaleAnimation mScaleAnim;
	private Bitmap mSplashBitmap;
	private ContentResolver mContentResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		startService(new Intent(ZhihuService.ACTION_UPDATE_LATEST));

		mImgSplash = (ImageView) findViewById(R.id.img_splash);

		mScaleAnim = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		mScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
		mScaleAnim.setDuration(ZhihuConsts.SPLASH_ANIM_DURATION);
		mScaleAnim.setFillAfter(true);
		mScaleAnim.setAnimationListener(listener);

		mContentResolver = getContentResolver();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mSplashBitmap = ZhihuContract.Splash.getSplash(mContentResolver,
				dm.widthPixels, dm.heightPixels);
		mImgSplash.setImageBitmap(mSplashBitmap);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mImgSplash.startAnimation(mScaleAnim);
	}

	@Override
	protected void onDestroy() {
		mImgSplash.setImageDrawable(null);
		if (mSplashBitmap != null) {
			mSplashBitmap.recycle();
			mSplashBitmap = null;
		}
		mContentResolver = null;
		super.onDestroy();
	}

	private final AnimationListener listener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			startActivity(new Intent(SplashActivity.this, LatestActivity.class));
			finish();
			overridePendingTransition(0, android.R.anim.fade_out);
		}
	};
}

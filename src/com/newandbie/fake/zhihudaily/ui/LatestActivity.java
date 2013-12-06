package com.newandbie.fake.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.ZhihuService;
import com.newandbie.fake.zhihudaily.ui.fragment.LatestContentFragment;
import com.newandbie.fake.zhihudaily.ui.fragment.SettingsMenuFragment;

public class LatestActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;

	private FragmentManager fragMgr;

	public static final int CONTENT_ID = R.id.latest_content;
	public static final int RIGHT_ID = R.id.latest_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_latest);

		fragMgr = getSupportFragmentManager();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.latest_drawer);

		fragMgr.beginTransaction().add(CONTENT_ID, new LatestContentFragment())
				.add(RIGHT_ID, new SettingsMenuFragment()).commit();
	}

	@Override
	protected void onDestroy() {
		startService(new Intent(ZhihuService.ACTION_ACTIVITY_DESTROY));
		super.onDestroy();
	}
}

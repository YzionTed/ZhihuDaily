package com.newandbie.fake.zhihudaily.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.SimpleAdapter;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.ZhihuConsts;

/**
 * 侧面可滑动设置菜单
 * 
 * @author ray
 * 
 */
public class SettingsMenuFragment extends ListFragment {

	private SimpleAdapter mSettingsMenuAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mSettingsMenuAdapter = new SimpleAdapter(getActivity(), data,
		// R.layout.item_settings, from, new int[] { R.id.img_settings,
		// R.id.text_settings });
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setBackgroundColor(ZhihuConsts.SETTINGS_MENU_BG_COLOR);

		setListShown(true);
	}

	private void prepareUserStateView() {

	}

}

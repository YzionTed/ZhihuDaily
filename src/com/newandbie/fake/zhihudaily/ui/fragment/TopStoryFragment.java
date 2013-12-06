package com.newandbie.fake.zhihudaily.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.model.TopStoryEntry;

public class TopStoryFragment extends Fragment {
	private TextView mTextTitle;
	private ImageView mImgImage;

	private TopStoryEntry mEntry;

	public static final String DATA_KEY_TOP_STORY = "topstoryentry";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		if (data != null) {
			mEntry = data.getParcelable(DATA_KEY_TOP_STORY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.item_topstory, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTextTitle = (TextView) view.findViewById(R.id.text_top_story_title);
		mImgImage = (ImageView) view.findViewById(R.id.img_top_story);

		if (mEntry != null) {
			mTextTitle.setText(mEntry.getTitle());
			// TODO 设置图片
			mImgImage.setBackgroundColor(Color.BLACK);
		}
	}
}

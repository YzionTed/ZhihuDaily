package com.newandbie.fake.zhihudaily.ui.fragment;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.db.ZhihuDb;
import com.newandbie.fake.zhihudaily.model.NewsEntry;
import com.newandbie.fake.zhihudaily.model.TopStoryEntry;
import com.newandbie.fake.zhihudaily.provider.ZhihuContract;
import com.newandbie.fake.zhihudaily.ui.LatestActivity;
import com.newandbie.fake.zhihudaily.util.DateUtils;

/**
 * 显示最近新闻的页面
 * 
 * 顶部有一个ViewPager
 * 
 * @author ray
 * 
 */
public class LatestContentFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener {
	private LoaderManager mLoaderMgr;
	private final int mContentLoaderId = 0;
	private final int mTopStoriesLoaderId = 1;
	private ViewPager mTopStoriesViewPager;
	// ViewPager的id
	// ViewPager必须有一个id才可以正常工作
	private final int VIEWPAGER_ID = 0x00000001;
	private int mTopStoriesHeight;

	// 负责维护列表数据
	private ContentAdapter mContentAdapter;
	private TopSotryAdapter mTopStoryAdapter;

	private Cursor mCurrentCursor = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopStoriesViewPager = new ViewPager(getActivity()
				.getApplicationContext());
		mTopStoriesViewPager.setId(VIEWPAGER_ID);

		mTopStoriesHeight = Math.round(getResources().getDimension(
				R.dimen.top_story_height));

		// 默认布局参数
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, mTopStoriesHeight, 0);
		mTopStoriesViewPager.setLayoutParams(lp);

		mContentAdapter = new ContentAdapter(getActivity());
		mTopStoryAdapter = new TopSotryAdapter(null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FIXME 硬编码
		setEmptyText("加载数据失败。。。");
		if (mContentAdapter.getCount() <= 0) {
			setListShown(false);
		}

		mLoaderMgr = getLoaderManager();
		// KitKat以下的版本还是要求addHeaderView()在setAdapter()之前
		getListView().addHeaderView(mTopStoriesViewPager, null, false);
		getListView().setAdapter(mContentAdapter);
		getListView().setOnItemClickListener(this);

		mTopStoriesViewPager.setAdapter(mTopStoryAdapter);

		if (mCurrentCursor == null) {
			mLoaderMgr.initLoader(mContentLoaderId, null, this);
		}
		mLoaderMgr.initLoader(mTopStoriesLoaderId, null, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLoaderMgr != null) {
			mLoaderMgr.destroyLoader(mContentLoaderId);
			mLoaderMgr.destroyLoader(mTopStoriesLoaderId);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		if (id == mContentLoaderId) {
			return new CursorLoader(getActivity().getApplicationContext(),
					ZhihuContract.News.CONTENT_LATEST_URI, null,
					ZhihuDb.News.DATE + "=?",
					new String[] { DateUtils.getTodayDate() }, null);
		} else if (id == mTopStoriesLoaderId) {
			return new CursorLoader(getActivity().getApplicationContext(),
					ZhihuContract.TopStory.CONTENT_GET_URI, null, null, null,
					null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		final int id = loader.getId();
		if (id == mContentLoaderId) {
			Cursor oldCursor = mContentAdapter.swapCursor(c);
			if (oldCursor != null) {
				oldCursor.close();
			}
			if (isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
			mCurrentCursor = c;
		} else if (id == mTopStoriesLoaderId) {
			Cursor oldCursor = mTopStoryAdapter.swapCursor(c);
			if (oldCursor != null) {
				oldCursor.close();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		final int id = loader.getId();
		if (id == mContentLoaderId) {
			Cursor oldCursor = mContentAdapter.swapCursor(null);
			if (oldCursor != null) {
				oldCursor.close();
			}
		} else if (id == mTopStoriesLoaderId) {
			Cursor oldCursor = mTopStoryAdapter.swapCursor(null);
			if (oldCursor != null) {
				oldCursor.close();
			}
		}
	}

	private class ContentAdapter extends SimpleCursorAdapter {
		public ContentAdapter(Context context) {
			super(context, R.layout.item_content, mCurrentCursor,
					new String[] { ZhihuDb.News.TITLE },
					new int[] { R.id.content_item }, 0);
		}
	}

	// 因为每一个Fragment都显示图片数据，所以用内存消耗较小的FragmentStatePagerAdapter
	private class TopSotryAdapter extends FragmentStatePagerAdapter {

		private Cursor mCursor;
		private Cursor mOldCursor;
		private List<TopStoryEntry> mTopStories;

		public TopSotryAdapter(Cursor c) {
			// 因为ViewPager是放在Fragment里面的
			// 所以用ChildFragmentManager；
			super(getChildFragmentManager());
			mCursor = c;
			mTopStories = convertCursorToEntry(c);
		}

		@Override
		public Fragment getItem(int position) {
			TopStoryEntry entry = mTopStories.get(position);
			TopStoryFragment tsf = new TopStoryFragment();
			Bundle data = new Bundle();
			data.putParcelable(TopStoryFragment.DATA_KEY_TOP_STORY, entry);
			tsf.setArguments(data);
			return tsf;
		}

		@Override
		public int getCount() {
			return mTopStories == null ? 0 : mTopStories.size();
		}

		public Cursor swapCursor(Cursor c) {
			mOldCursor = mCursor;
			mCursor = c;

			if (mTopStories != null) {
				mTopStories.clear();
			}

			mTopStories = convertCursorToEntry(c);
			notifyDataSetChanged();

			return mOldCursor;
		}

		private List<TopStoryEntry> convertCursorToEntry(Cursor c) {
			return TopStoryEntry.fromCursor(c);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 由于存在Header，所以position"增大"了
		final int headerCount = getListView().getHeaderViewsCount();
		final int correctPosition = position - headerCount;
		Cursor c = (Cursor) mContentAdapter.getItem(correctPosition);
		NewsEntry entry = NewsEntry.fromCursor(c);
		if (entry == null) {
			// FIXME 硬编码
			Toast.makeText(getActivity(), "新闻数据无效，无法传递", Toast.LENGTH_SHORT)
					.show();
		}
		Bundle data = new Bundle();
		data.putParcelable(NewsFragment.DATA_KEY_NEWS, entry);
		NewsFragment nf = new NewsFragment();
		nf.setArguments(data);
		// TODO 现在先用Fragment来实现，将来测试一下用Activity实现跳转会不会性能更好
		getFragmentManager().beginTransaction()
				.replace(LatestActivity.CONTENT_ID, nf).addToBackStack(null)
				.commit();
	}
}

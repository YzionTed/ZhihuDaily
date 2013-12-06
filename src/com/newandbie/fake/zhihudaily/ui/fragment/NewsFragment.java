package com.newandbie.fake.zhihudaily.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.newandbie.fake.zhihudaily.R;
import com.newandbie.fake.zhihudaily.ZhihuService;
import com.newandbie.fake.zhihudaily.db.ZhihuDb;
import com.newandbie.fake.zhihudaily.model.NewsEntry;
import com.newandbie.fake.zhihudaily.provider.ZhihuContract;

/**
 * 显示新闻的页面
 * 
 * @author ray
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class NewsFragment extends Fragment implements LoaderCallbacks<Cursor>,
		OnClickListener {
	public static final String DATA_KEY_NEWS = "newskey";
	private ImageView mImgNewsImage;
	private WebView mWvNews;

	private NewsEntry mEntry;
	private String mCss;

	private final int mContentLoaderId = 1;
	private LoaderManager loaderMgr;

	private Button btnShare;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		if (data != null) {
			mEntry = data.getParcelable(DATA_KEY_NEWS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_news, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mImgNewsImage = (ImageView) view.findViewById(R.id.img_news_image);
		mWvNews = (WebView) view.findViewById(R.id.web_news);
		btnShare = (Button) view.findViewById(R.id.btn_share);
		btnShare.setOnClickListener(this);

		// 设置WebView
		mWvNews.getSettings().setJavaScriptEnabled(true);
		mWvNews.getSettings().setAllowFileAccess(true);
		mWvNews.getSettings().setDomStorageEnabled(true);
		mWvNews.getSettings().setAppCacheEnabled(true);
		mWvNews.getSettings().setAppCachePath(
				getActivity().getCacheDir().getAbsolutePath());
		mWvNews.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		// TODO 设置图片
		mImgNewsImage.setBackgroundColor(Color.BLACK);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loaderMgr = getLoaderManager();
		loaderMgr.initLoader(mContentLoaderId, null, this);
	}

	@Override
	public void onStop() {
		if (loaderMgr != null) {
			loaderMgr.destroyLoader(mContentLoaderId);
		}
		super.onStop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		if (id == mContentLoaderId) {
			return new CursorLoader(getActivity(),
					ZhihuContract.News.getQueryNewsUri(mEntry.getId()), null,
					null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		final int id = loader.getId();
		if (id == mContentLoaderId) {
			if (c != null && !c.isClosed() && c.getCount() > 0) {
				if (c.moveToFirst()) {
					final int indexBody = c
							.getColumnIndex(ZhihuDb.NewsContent.BODY);
					final int indexId = c
							.getColumnIndex(ZhihuDb.NewsContent.ID);
					final String body = c.getString(indexBody);
					final int newsId = c.getInt(indexId);
					if (TextUtils.isEmpty(mCss)) {
						// TODO
						mCss = ZhihuContract.Css.getCss(getActivity()
								.getContentResolver());
					}
					mWvNews.loadDataWithBaseURL("file:///android_asset/",
							prepareHtml(true, true, newsId, body), "text/html",
							"utf-8", null);
				}
			} else {
				if (mEntry != null) {
					Intent intent = new Intent(ZhihuService.ACTION_FETCH_NEWS);
					intent.putExtra(ZhihuService.START_KEY_FETCH_NEWS_URL,
							mEntry.getUrl());
					getActivity().startService(intent);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
	}

	/**
	 * 格式化新闻数据
	 * 
	 * @param large
	 * @param night
	 * @param newsId
	 * @param body
	 * @return
	 */
	private String prepareHtml(boolean large, boolean night, int newsId,
			String body) {
		final String line1 = "<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,user-scalable=no\"><link href=\"news_qa.min.css\" rel=\"stylesheet\"><style>.headline .img-place-holder{height:0}</style><script src=\"img_replace.js\"></script></head><body className=\"%s\">";
		final String line2 = "<script src=\"large-font.js\"></script>";
		final String line3 = "<script src=\"night.js\"></script>";
		final String line4 = "<script>window.news_id=%s;</script><script src=\"http://daily.zhihu.com/js/zepto.min.js\"></script><script src=\"http://news-at.zhihu.com/js/hot-comments.ios.3.js\"></script>";
		final String line5 = "</body></html>";

		StringBuilder arguments = new StringBuilder("");
		if (large) {
			arguments.append("large ");
		}
		if (night) {
			arguments.append("night ");
		}

		StringBuilder id = new StringBuilder();
		id.append(String.format(line4, String.valueOf(newsId)));

		StringBuilder html = new StringBuilder(2048);
		html.append(String.format(line1, arguments.toString()));// line1
		html.append(body);
		if (large) {
			html.append(line2);
		}
		if (night) {
			html.append(line3);
		}
		html.append(id);// line4
		html.append(line5);

		return html.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share:
			// TODO 可以用第三方分享组件
			Intent dataIntent = new Intent(Intent.ACTION_SEND);
			dataIntent.setType("text/plain");
			dataIntent.putExtra(Intent.EXTRA_TEXT, mEntry.getUrl());
			dataIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Intent shareIntent = Intent.createChooser(dataIntent, null);
			getActivity().startActivity(shareIntent);
			break;
		}
	}
}

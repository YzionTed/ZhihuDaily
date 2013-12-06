package com.newandbie.fake.zhihudaily.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.newandbie.fake.zhihudaily.ZhihuConsts;
import com.newandbie.fake.zhihudaily.db.ZhihuDb;
import com.newandbie.fake.zhihudaily.db.ZhihuDbOpenHelper;

/**
 * 从数据库里获取数据
 * 
 * @author ray
 * 
 */
public class ZhihuProvider extends ContentProvider {

	// path structure
	// 4.3之后才可以在路径前加/
	// 1 关于new的操作
	public static final String NEWS_PATH = ZhihuDb.News.TABLE_NAME;

	public static final String NEWS_LATEST = NEWS_PATH + "/"
			+ ZhihuContract.News.LATEST;
	public static final int NEWS_LATEST_CODE = 1;

	public static final String NEWS_BULK_INSERT = NEWS_PATH + "/"
			+ ZhihuContract.News.BULK_INSERT;
	public static final int NEWS_BULK_INSERT_CODE = 2;

	public static final String NEWS_GET = NEWS_PATH + "/"
			+ ZhihuContract.News.GET + "/#";
	public static final int NEWS_GET_CODE = 3;

	public static final String NEWS_INSERT = NEWS_PATH + "/"
			+ ZhihuContract.News.INSERT;
	public static final int NEWS_INSERT_CODE = 4;

	// 2 关于Splash的操作
	public static final String SPLASH_PATH = "splash";

	public static final String SPLASH_GET = SPLASH_PATH + "/"
			+ ZhihuContract.Splash.GET;
	public static final int SPLASH_GET_CODE = 11;

	private static final UriMatcher sUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	// content://com.example.demo/path1/table1/#
	// content://com.example.demo/path1/table1/*
	// content://com.example.demo/path1/table1

	// 3 关于推荐新闻的操作
	public static final String TOP_STORY_PATH = ZhihuDb.TopStories.TABLE_NAME;

	public static final String TOP_STORY_UPDATE = TOP_STORY_PATH + "/"
			+ ZhihuContract.TopStory.UPDATE;
	public static final int TOP_STORY_UPDATE_CODE = 21;

	public static final String TOP_STORY_GET = TOP_STORY_PATH + "/"
			+ ZhihuContract.TopStory.GET;
	public static final int TOP_STORY_GET_CODE = 22;

	// 4 关于css的操作
	public static final String CSS_PATH = "css";

	public static final String CSS_GET = CSS_PATH + "/" + ZhihuContract.Css.GET;
	public static final int CSS_GET_CODE = 31;

	// TODO
	public static final String CSS_UPDATE = CSS_PATH + "/"
			+ ZhihuContract.Css.UPDATE;
	public static final int CSS_UPDATE_CODE = 32;

	static {
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, NEWS_LATEST,
				NEWS_LATEST_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, NEWS_BULK_INSERT,
				NEWS_BULK_INSERT_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, NEWS_GET, NEWS_GET_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, NEWS_INSERT,
				NEWS_INSERT_CODE);
		sUriMatcher
				.addURI(ZhihuContract.AUTHORITY, SPLASH_GET, SPLASH_GET_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, TOP_STORY_UPDATE,
				TOP_STORY_UPDATE_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, TOP_STORY_GET,
				TOP_STORY_GET_CODE);
		sUriMatcher.addURI(ZhihuContract.AUTHORITY, CSS_GET, CSS_GET_CODE);
		sUriMatcher
				.addURI(ZhihuContract.AUTHORITY, CSS_UPDATE, CSS_UPDATE_CODE);
	}

	private ZhihuDbOpenHelper mDbHelper;

	/** TODO 欢迎界面的图片是否处于更新中 */
	private boolean isSplashUpdating = false;

	/** TODO css文件是否处于更新中 */
	private boolean isCssUpdating = false;

	@Override
	public boolean onCreate() {
		mDbHelper = new ZhihuDbOpenHelper(getContext());
		return true;
	}

	// 批量插入，提高应用性能
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int rows = 0;
		SQLiteDatabase wdb = mDbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case NEWS_BULK_INSERT_CODE:
			wdb.beginTransaction();
			try {
				// 因为News的id列是唯一的，所以replace可能被当成update
				for (ContentValues cv : values) {
					wdb.replace(ZhihuDb.News.TABLE_NAME, null, cv);
					rows++;
				}
				wdb.setTransactionSuccessful();
			} finally {
				wdb.endTransaction();
			}
			break;
		case TOP_STORY_UPDATE_CODE:
			wdb.beginTransaction();
			try {
				wdb.delete(ZhihuDb.TopStories.TABLE_NAME, null, null);
				for (ContentValues cv : values) {
					wdb.insert(ZhihuDb.TopStories.TABLE_NAME, null, cv);
					rows++;
				}
				wdb.setTransactionSuccessful();
			} finally {
				wdb.endTransaction();
			}
			break;
		default:
			rows = (int) super.bulkInsert(uri, values);
			break;
		}

		wdb = null;
		return rows;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		SQLiteDatabase rdb = mDbHelper.getReadableDatabase();
		switch (sUriMatcher.match(uri)) {
		case NEWS_LATEST_CODE:
			c = rdb.query(ZhihuDb.News.TABLE_NAME, projection, selection,
					selectionArgs, null, null, null);
			break;
		case TOP_STORY_GET_CODE:
			c = rdb.query(ZhihuDb.TopStories.TABLE_NAME, projection, selection,
					selectionArgs, null, null, null);
			break;
		case NEWS_GET_CODE:
			final long newsId = ContentUris.parseId(uri);
			if (newsId != -1) {
				c = rdb.query(ZhihuDb.NewsContent.TABLE_NAME, null,
						ZhihuDb.NewsContent.ID + "=?",
						new String[] { String.valueOf(newsId) }, null, null,
						null);
				if (c.getCount() == 0) {
					// TODO 只注册某一指定id
					c.setNotificationUri(getContext().getContentResolver(),
							ZhihuContract.News.CONTENT_INSERT_URI);
				}
			}
			break;
		}
		// TODO
		rdb = null;
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case SPLASH_GET_CODE:
			// 欢迎页面的图片类型
			return ZhihuContract.Splash.CONTENT_TYPE;
		case NEWS_GET_CODE:
			return ZhihuContract.News.CONTENT_ITEM_TYPE;
		case NEWS_LATEST_CODE:
			return ZhihuContract.News.CONTENT_TYPE;
		default:
			// TODO
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase wdb = mDbHelper.getWritableDatabase();
		Uri ret = null;
		switch (sUriMatcher.match(uri)) {
		case NEWS_INSERT_CODE:
			wdb.beginTransaction();
			try {
				long rowId = wdb.replace(ZhihuDb.NewsContent.TABLE_NAME, null,
						values);
				if ((int) rowId != -1) {
					ret = Uri.withAppendedPath(
							ZhihuContract.News.CONTENT_GET_URI,
							String.valueOf(rowId));
				}
				wdb.setTransactionSuccessful();
			} finally {
				wdb.endTransaction();
			}
			break;
		}
		wdb = null;
		getContext().getContentResolver().notifyChange(uri, null, false);
		return ret;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		ParcelFileDescriptor pfd = null;
		switch (sUriMatcher.match(uri)) {
		case SPLASH_GET_CODE:
			File updatedSplash = new File(getContext().getCacheDir(),
					ZhihuConsts.SPLASH_FILE);
			// 这里只有可能处理缓存中的splash
			if (updatedSplash.isFile()) {
				pfd = ParcelFileDescriptor.open(updatedSplash,
						ParcelFileDescriptor.MODE_READ_ONLY);
			}
			break;
		// TODO case SPLASH_UPDATE_CODE:
		case CSS_GET_CODE:
			File updatedCss = new File(getContext().getCacheDir(),
					ZhihuConsts.CSS_FILE);
			if (updatedCss.isFile()) {
				pfd = ParcelFileDescriptor.open(updatedCss,
						ParcelFileDescriptor.MODE_READ_ONLY);
			}
			break;
		default:
			// 未定义操作
		}
		return pfd;
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
			throws FileNotFoundException {
		switch (sUriMatcher.match(uri)) {
		case SPLASH_GET_CODE:
			// 两种情况：
			// 1 没有更新的图片，返回资源数据里的
			// 2 有更新的图片
			// 2a 图片正在更新 ---> 1
			// 2b 图片已更新，返回新图片
			File updatedSplash = new File(getContext().getCacheDir(),
					ZhihuConsts.SPLASH_FILE);
			if (updatedSplash.isFile()) {
				// 情况2
				if (!isSplashUpdating) {
					// 2b
					// 因为文件不在assets里了，所以委托给openFile()
					break;
				}
			}
			// 没有更新的图片
			// 2a
			// 情况1
			try {
				return getContext().getAssets().openFd(ZhihuConsts.SPLASH_FILE);
			} catch (IOException ignored) {
				// 难道是apk文件损坏？
			}
			break;
		case CSS_GET_CODE:
			File updatedCss = new File(getContext().getCacheDir(),
					ZhihuConsts.CSS_FILE);
			if (updatedCss.isFile()) {
				if (!isCssUpdating) {
					break;
				}
			}
			try {
				return getContext().getAssets().openFd(ZhihuConsts.CSS_FILE);
			} catch (IOException ignored) {
			}
			break;
		default:
			// TODO 暂时只有欢迎画面的图片用到这个方法
		}
		return super.openAssetFile(uri, mode);
	}
}

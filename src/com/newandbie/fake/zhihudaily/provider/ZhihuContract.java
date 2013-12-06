package com.newandbie.fake.zhihudaily.provider;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import com.newandbie.fake.zhihudaily.db.ZhihuDb;
import com.newandbie.fake.zhihudaily.model.NewsContentEntry;
import com.newandbie.fake.zhihudaily.model.NewsEntry;
import com.newandbie.fake.zhihudaily.model.TopStoryEntry;
import com.newandbie.fake.zhihudaily.util.BitmapUtils;
import com.newandbie.fake.zhihudaily.util.DateUtils;

public final class ZhihuContract {
	public static final String AUTHORITY = "com.newandbie.fake.zhihudaily.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	/**
	 * 新闻合约类
	 * 
	 * @author ray
	 * 
	 */
	public static final class News {
		/**
		 * 不能实例化
		 */
		private News() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, ZhihuDb.News.TABLE_NAME);

		/** 查询今日新闻 */
		public static final String LATEST = "latest";
		public static final Uri CONTENT_LATEST_URI = Uri.withAppendedPath(
				CONTENT_URI, LATEST);

		/** 批量插入新闻 */
		public static final String BULK_INSERT = "bulk";
		public static final Uri CONTENT_BULK_INSERT_URI = Uri.withAppendedPath(
				CONTENT_URI, BULK_INSERT);

		/** 获取某一条新闻 */
		public static final String GET = "get";
		public static final Uri CONTENT_GET_URI = Uri.withAppendedPath(
				CONTENT_URI, GET);

		/** 插入一条新闻 */
		public static final String INSERT = "insert";
		public static final Uri CONTENT_INSERT_URI = Uri.withAppendedPath(
				CONTENT_URI, INSERT);

		/** 查询多条新闻结果的类型 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ AUTHORITY + ".news";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ AUTHORITY + ".news";

		/**
		 * 查询今日新闻
		 * 
		 * @param cr
		 * @return
		 */
		public static Cursor getLatest(ContentResolver cr) {
			return cr.query(CONTENT_LATEST_URI, null, ZhihuDb.News.DATE + "=?",
					new String[] { DateUtils.getTodayDate() }, null);
		}

		/**
		 * 批量插入新闻，如果遇到之前已经插入的就更新
		 * 
		 * @param cr
		 * @param newsList
		 * @param date
		 * @return
		 */
		public static int bulkInsertNews(ContentResolver cr,
				List<NewsEntry> newsList, String date) {
			ContentValues[] values = new ContentValues[newsList.size()];
			for (int i = 0, len = newsList.size(); i < len; i++) {
				ContentValues cv = new ContentValues();
				NewsEntry entry = newsList.get(i);
				cv.put(ZhihuDb.News.IMAGE_SOURCE, entry.getImage_source());
				cv.put(ZhihuDb.News.TITLE, entry.getTitle());
				cv.put(ZhihuDb.News.URL, entry.getUrl());
				cv.put(ZhihuDb.News.IMAGE, entry.getImage());
				cv.put(ZhihuDb.News.SHARE_URL, entry.getShare_url());
				cv.put(ZhihuDb.News.ID, entry.getId());
				cv.put(ZhihuDb.News.GA_PREFIX, entry.getGa_prefix());
				cv.put(ZhihuDb.News.THUMBNAIL, entry.getThumbnail());
				cv.put(ZhihuDb.News.DATE, date);

				values[i] = cv;
			}
			return cr.bulkInsert(CONTENT_BULK_INSERT_URI, values);
		}

		/**
		 * 插入一条新闻
		 * 
		 * @param cr
		 * @param entry
		 * @return
		 */
		public static Uri insertNews(ContentResolver cr, NewsContentEntry entry) {
			ContentValues cv = new ContentValues();
			cv.put(ZhihuDb.NewsContent.BODY, entry.getBody());
			cv.put(ZhihuDb.NewsContent.ID, entry.getId());
			return cr.insert(CONTENT_INSERT_URI, cv);
		}

		/**
		 * 查询一条指定id的新闻
		 * 
		 * @param cr
		 * @param newsId
		 * @return
		 */
		public static Cursor getNews(ContentResolver cr, int newsId) {
			return cr.query(getQueryNewsUri(newsId), null, null, null, null);
		}

		public static Uri getQueryNewsUri(int newsId) {
			return Uri
					.withAppendedPath(CONTENT_GET_URI, String.valueOf(newsId));
		}
	}

	public static final class TopStory {
		private TopStory() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, ZhihuDb.TopStories.TABLE_NAME);

		/** 更新推荐新闻 */
		public static final String UPDATE = "update";
		public static final Uri CONTENT_UPDATE_URI = Uri.withAppendedPath(
				CONTENT_URI, UPDATE);

		/** 查询推荐新闻 */
		public static final String GET = "get";
		public static final Uri CONTENT_GET_URI = Uri.withAppendedPath(
				CONTENT_URI, GET);

		public static int bulkUpdateTopStories(ContentResolver cr,
				List<TopStoryEntry> topStories) {
			ContentValues[] values = new ContentValues[topStories.size()];
			for (int i = 0, len = topStories.size(); i < len; i++) {
				ContentValues cv = new ContentValues();
				TopStoryEntry entry = topStories.get(i);
				cv.put(ZhihuDb.TopStories.IMAGE_SOURCE, entry.getImage_source());
				cv.put(ZhihuDb.TopStories.TITLE, entry.getTitle());
				cv.put(ZhihuDb.TopStories.URL, entry.getTitle());
				cv.put(ZhihuDb.TopStories.IMAGE, entry.getImage());
				cv.put(ZhihuDb.TopStories.SHARE_URL, entry.getShare_url());
				cv.put(ZhihuDb.TopStories.GA_PREFIX, entry.getGa_prefix());
				cv.put(ZhihuDb.TopStories.ID, entry.getId());

				values[i] = cv;
			}
			return cr.bulkInsert(CONTENT_UPDATE_URI, values);
		}
	}

	public static final class Splash {
		private Splash() {

		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "splash");

		public static final String GET = "get";
		public static final Uri CONTENT_GET_URI = Uri.withAppendedPath(
				CONTENT_URI, GET);

		public static final String UPDATE = "update";
		public static final Uri CONTENT_UPDATE_URI = Uri.withAppendedPath(
				CONTENT_URI, UPDATE);

		public static final String CONTENT_TYPE = "image/png";

		/**
		 * 获取Splash图片
		 * 
		 * 如果缓存里没有更新的Splash，就返回随应用程序发布的
		 * 
		 * @param cr
		 * @param reqWidth
		 * @param reqHeight
		 * @return
		 */
		public static Bitmap getSplash(ContentResolver cr, int reqWidth,
				int reqHeight) {
			AssetFileDescriptor afd = null;
			FileInputStream fin = null;
			try {
				afd = cr.openAssetFileDescriptor(CONTENT_GET_URI, "r");
				if (afd != null) {
					fin = afd.createInputStream();
					return BitmapUtils.decodeSampledBitmapFromStream(fin,
							reqWidth, reqHeight);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (afd != null) {
					try {
						afd.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}

	public static final class Css {
		private Css() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "css");

		/** 获取css */
		public static final String GET = "get";
		public static final Uri CONTENT_GET_URI = Uri.withAppendedPath(
				CONTENT_URI, GET);

		/** 更新css */
		public static final String UPDATE = "update";
		public static final Uri CONTENT_UPDATE_URI = Uri.withAppendedPath(
				CONTENT_URI, UPDATE);

		/**
		 * 获取css文件
		 * 
		 * @param cr
		 * @return
		 */
		public static String getCss(ContentResolver cr) {
			AssetFileDescriptor afd = null;
			FileInputStream fin = null;
			ByteArrayOutputStream out = null;
			try {
				afd = cr.openAssetFileDescriptor(CONTENT_GET_URI, "r");
				if (afd != null) {
					fin = afd.createInputStream();
					byte[] buf = new byte[2048];
					int len = -1;
					out = new ByteArrayOutputStream();
					while ((len = fin.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					return new String(out.toByteArray());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (afd != null) {
					try {
						afd.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}
}

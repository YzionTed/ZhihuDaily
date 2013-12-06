package com.newandbie.fake.zhihudaily.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ZhihuDbOpenHelper extends SQLiteOpenHelper {

	public ZhihuDbOpenHelper(Context context) {
		super(context, ZhihuDb.DB_NAME, null, ZhihuDb.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建顶部推荐文章表
		final String topStoriesDDL = ZhihuDb.TopStories.ddl();
		// 新闻表
		final String newsDDL = ZhihuDb.News.ddl();
		// 新闻内容表
		final String contentDDL = ZhihuDb.NewsContent.ddl();

		db.execSQL(newsDDL);
		db.execSQL(topStoriesDDL);
		db.execSQL(contentDDL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}

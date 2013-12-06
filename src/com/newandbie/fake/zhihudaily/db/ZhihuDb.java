package com.newandbie.fake.zhihudaily.db;

import android.provider.BaseColumns;

public final class ZhihuDb {
	/** 当前数据库版本 */
	public static final int DB_VERSION = 1;
	/** 数据库名 */
	/* default */static final String DB_NAME = "zhihu";

	// 空格
	private static final String SPACE_BLANK = " ";
	// 逗号
	private static final String COMMA = ",";

	/**
	 * 新闻表
	 * 
	 * @author ray
	 * 
	 */
	public static final class News implements BaseColumns {
		/** 表名 */
		public static final String TABLE_NAME = "news";
		/** 图片来源 */
		public static final String IMAGE_SOURCE = "image_source";
		/** 标题 */
		public static final String TITLE = "title";
		/** 文章链接 */
		public static final String URL = "url";
		/** 文章内标题大图链接 */
		public static final String IMAGE = "image";
		/** 分享文章链接 */
		public static final String SHARE_URL = "share_url";
		/** 文章id UNIQUE */
		public static final String ID = "id";
		/** 谷歌分析前缀 */
		public static final String GA_PREFIX = "ga_prefix";
		/** 缩略图链接 */
		public static final String THUMBNAIL = "thumbnail";
		/** 是否已读 */
		public static final String READ = "read";
		/** 新闻发布的日期 yyyymmdd */
		public static final String DATE = "date";

		/**
		 * 获取定义表结构的SQL语句
		 * 
		 * @param dbName
		 *            数据库名，可以为null
		 * @param tableName
		 *            表名，不能为空
		 * @throws IllegalArgumentException
		 *             当表名为null或空字符串时，抛异常
		 */
		public static String ddl() {
			StringBuilder b = new StringBuilder(256);
			b.append("CREATE TABLE IF NOT EXISTS ");
			b.append(TABLE_NAME).append(SPACE_BLANK);
			b.append("(");
			// 主键
			b.append(_ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("PRIMARY KEY AUTOINCREMENT")
					.append(COMMA);
			// 图片来源
			b.append(IMAGE_SOURCE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 标题
			b.append(TITLE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章链接
			b.append(URL).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章内标题大图链接
			b.append(IMAGE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 分享文章链接
			b.append(SHARE_URL).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章id
			b.append(ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("UNIQUE").append(COMMA);
			// 谷歌分析前缀
			b.append(GA_PREFIX).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 缩略图链接
			b.append(THUMBNAIL).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 新闻发布的日期
			b.append(DATE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 是否已读
			b.append(READ).append(SPACE_BLANK).append("BOOLEAN")
					.append(SPACE_BLANK).append("DEFAULT").append(SPACE_BLANK)
					.append("FALSE");
			b.append(");");
			return b.toString();
		}
	}

	/**
	 * 推荐文章表
	 * 
	 * @author ray
	 * 
	 */
	public static class TopStories implements BaseColumns {
		/** 图片来源 */
		public static final String IMAGE_SOURCE = "image_source";
		/** 文章标题 */
		public static final String TITLE = "title";
		/** 文章链接 */
		public static final String URL = "url";
		/** 文章内标题大图链接 */
		public static final String IMAGE = "image";
		/** 分享文章链接 */
		public static final String SHARE_URL = "share_url";
		/** 谷歌分析前缀 */
		public static final String GA_PREFIX = "ga_prefix";
		/** 文章id */
		public static final String ID = "id";
		/** 表名 */
		public static final String TABLE_NAME = "top_stories";

		public static String ddl() {
			StringBuilder b = new StringBuilder(256);
			b.append("CREATE TABLE IF NOT EXISTS ");
			b.append(TABLE_NAME).append(SPACE_BLANK);
			b.append("(");
			// 主键
			b.append(_ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("PRIMARY KEY AUTOINCREMENT")
					.append(COMMA);
			// 图片来源
			b.append(IMAGE_SOURCE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 标题
			b.append(TITLE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章链接
			b.append(URL).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章内标题大图链接
			b.append(IMAGE).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 分享文章链接
			b.append(SHARE_URL).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章id
			b.append(ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 谷歌分析前缀
			b.append(GA_PREFIX).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL");
			b.append(");");
			return b.toString();
		}
	}

	/**
	 * 新闻内容表
	 * 
	 * @author ray
	 * 
	 */
	public static final class NewsContent implements BaseColumns {
		/** 表名 */
		public static final String TABLE_NAME = "content";
		/** 新闻正文 */
		public static final String BODY = "body";
		/** 新闻ID */
		public static final String ID = "id";

		public static String ddl() {
			StringBuilder b = new StringBuilder(128);
			b.append("CREATE TABLE IF NOT EXISTS ");
			b.append(TABLE_NAME).append(SPACE_BLANK);
			b.append("(");
			// 主键
			b.append(_ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("PRIMARY KEY AUTOINCREMENT")
					.append(COMMA);
			// 正文
			b.append(BODY).append(SPACE_BLANK).append("TEXT")
					.append(SPACE_BLANK).append("NOT NULL").append(COMMA);
			// 文章id
			b.append(ID).append(SPACE_BLANK).append("INTEGER")
					.append(SPACE_BLANK).append("UNIQUE");
			b.append(");");
			return b.toString();
		}
	}
}

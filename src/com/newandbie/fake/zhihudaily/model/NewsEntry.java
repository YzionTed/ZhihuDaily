package com.newandbie.fake.zhihudaily.model;

import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.newandbie.fake.zhihudaily.db.ZhihuDb;

public class NewsEntry implements Parcelable {
	private String image_source;
	private String title;
	private String url;
	private String image;
	private String share_url;
	private int id;
	private String ga_prefix;
	private String thumbnail;

	public String getImage_source() {
		return image_source;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getImage() {
		return image;
	}

	public String getShare_url() {
		return share_url;
	}

	public int getId() {
		return id;
	}

	public String getGa_prefix() {
		return ga_prefix;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	private NewsEntry(String image_source, String title, String url,
			String image, String share_url, int id, String ga_prefix,
			String thumbnail) {
		this.image_source = image_source;
		this.title = title;
		this.url = url;
		this.image = image;
		this.share_url = share_url;
		this.id = id;
		this.ga_prefix = ga_prefix;
		this.thumbnail = thumbnail;
	}

	public static NewsEntry fromJson(JSONObject json) {
		final String image_source = json.optString("image_source");
		final String title = json.optString("title");
		final String url = json.optString("url");
		final String image = json.optString("image");
		final String share_url = json.optString("share_url");
		final int id = json.optInt("id");
		final String ga_prefix = json.optString("ga_prefix");
		final String thumbnail = json.optString("thumbnail");

		return new NewsEntry(image_source, title, url, image, share_url, id,
				ga_prefix, thumbnail);
	}

	/**
	 * 从Cursor对象的当前位置构造一个NewsEntry对象
	 * 
	 * @param c
	 * @return
	 */
	public static NewsEntry fromCursor(Cursor c) {
		if (c.getPosition() >= 0) {
			final int indexImageSource = c
					.getColumnIndex(ZhihuDb.News.IMAGE_SOURCE);
			final int indexTitle = c.getColumnIndex(ZhihuDb.News.TITLE);
			final int indexUrl = c.getColumnIndex(ZhihuDb.News.URL);
			final int indexImage = c.getColumnIndex(ZhihuDb.News.IMAGE);
			final int indexShareUrl = c.getColumnIndex(ZhihuDb.News.SHARE_URL);
			final int indexId = c.getColumnIndex(ZhihuDb.News.ID);
			final int indexGaPrefix = c.getColumnIndex(ZhihuDb.News.GA_PREFIX);
			final int indexThumbnail = c.getColumnIndex(ZhihuDb.News.THUMBNAIL);

			return new NewsEntry(c.getString(indexImageSource),
					c.getString(indexTitle), c.getString(indexUrl),
					c.getString(indexImage), c.getString(indexShareUrl),
					c.getInt(indexId), c.getString(indexGaPrefix),
					c.getString(indexThumbnail));
		}
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image_source);
		dest.writeString(title);
		dest.writeString(url);
		dest.writeString(image);
		dest.writeString(share_url);
		dest.writeInt(id);
		dest.writeString(ga_prefix);
		dest.writeString(thumbnail);
	}

	public static final Parcelable.Creator<NewsEntry> CREATOR = new Parcelable.Creator<NewsEntry>() {
		public NewsEntry createFromParcel(Parcel in) {
			return new NewsEntry(in);
		}

		public NewsEntry[] newArray(int size) {
			return new NewsEntry[size];
		}
	};

	private NewsEntry(Parcel in) {
		image_source = in.readString();
		title = in.readString();
		url = in.readString();
		image = in.readString();
		share_url = in.readString();
		id = in.readInt();
		ga_prefix = in.readString();
		thumbnail = in.readString();
	}

}

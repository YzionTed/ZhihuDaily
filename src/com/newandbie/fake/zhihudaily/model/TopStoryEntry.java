package com.newandbie.fake.zhihudaily.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.newandbie.fake.zhihudaily.db.ZhihuDb;

public class TopStoryEntry implements Parcelable {
	private String image_source;
	private String title;
	private String url;
	private String image;
	private String share_url;
	private String ga_prefix;
	private int id;

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

	public String getGa_prefix() {
		return ga_prefix;
	}

	public int getId() {
		return id;
	}

	private TopStoryEntry(String image_source, String title, String url,
			String image, String share_url, String ga_prefix, int id) {
		this.image_source = image_source;
		this.title = title;
		this.url = url;
		this.image = image;
		this.share_url = share_url;
		this.ga_prefix = ga_prefix;
		this.id = id;
	}

	public static TopStoryEntry fromJson(JSONObject json) {
		final String image_source = json.optString("image_source");
		final String title = json.optString("title");
		final String url = json.optString("url");
		final String image = json.optString("image");
		final String share_url = json.optString("share_url");
		final String ga_prefix = json.optString("ga_prefix");
		final int id = json.optInt("id");

		return new TopStoryEntry(image_source, title, url, image, share_url,
				ga_prefix, id);
	}

	public static List<TopStoryEntry> fromCursor(Cursor c) {
		if (c != null && !c.isClosed() && c.getCount() > 0) {
			final int indexImageSource = c
					.getColumnIndex(ZhihuDb.TopStories.IMAGE_SOURCE);
			final int indexTitle = c.getColumnIndex(ZhihuDb.TopStories.TITLE);
			final int indexUrl = c.getColumnIndex(ZhihuDb.TopStories.URL);
			final int indexImage = c.getColumnIndex(ZhihuDb.TopStories.IMAGE);
			final int indexShareUrl = c
					.getColumnIndex(ZhihuDb.TopStories.SHARE_URL);
			final int indexGaPrefix = c
					.getColumnIndex(ZhihuDb.TopStories.GA_PREFIX);
			final int indexId = c.getColumnIndex(ZhihuDb.TopStories.ID);
			ArrayList<TopStoryEntry> topStories = new ArrayList<TopStoryEntry>(
					c.getCount());
			while (c.moveToNext()) {
				topStories.add(new TopStoryEntry(c.getString(indexImageSource),
						c.getString(indexTitle), c.getString(indexUrl), c
								.getString(indexImage), c
								.getString(indexShareUrl), c
								.getString(indexGaPrefix), c.getInt(indexId)));
			}
			return topStories;
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
		dest.writeString(ga_prefix);
		dest.writeInt(id);
	}

	public static final Parcelable.Creator<TopStoryEntry> CREATOR = new Parcelable.Creator<TopStoryEntry>() {
		public TopStoryEntry createFromParcel(Parcel in) {
			return new TopStoryEntry(in);
		}

		public TopStoryEntry[] newArray(int size) {
			return new TopStoryEntry[size];
		}
	};

	private TopStoryEntry(Parcel in) {
		image_source = in.readString();
		title = in.readString();
		url = in.readString();
		image = in.readString();
		share_url = in.readString();
		ga_prefix = in.readString();
		id = in.readInt();
	}

}

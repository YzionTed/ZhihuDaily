package com.newandbie.fake.zhihudaily.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Latest {
	private String date;
	private List<NewsEntry> news;
	private boolean is_today;
	private List<TopStoryEntry> top_stories;
	private String display_date;

	public String getDate() {
		return date;
	}

	public List<NewsEntry> getNews() {
		return news;
	}

	public boolean isIs_today() {
		return is_today;
	}

	public List<TopStoryEntry> getTop_stories() {
		return top_stories;
	}

	public String getDisplay_date() {
		return display_date;
	}

	private Latest(String date, List<NewsEntry> news, boolean is_today,
			List<TopStoryEntry> top_stories, String display_date) {
		this.date = date;
		this.news = news;
		this.is_today = is_today;
		this.top_stories = top_stories;
		this.display_date = display_date;
	}

	public static Latest fromJson(JSONObject json) {
		final String date = json.optString("date");
		final boolean is_today = json.optBoolean("is_today");
		final String display_date = json.optString("display_date");

		// 开始解析两个数组
		final JSONArray news = json.optJSONArray("news");
		if (news == null) {
			throw new IllegalArgumentException(
					"Latest - fromJson() - 无法正常解析news");
		}
		final ArrayList<NewsEntry> newsList = new ArrayList<NewsEntry>(
				news.length());
		for (int i = 0, len = news.length(); i < len; i++) {
			newsList.add(NewsEntry.fromJson(news.optJSONObject(i)));
		}

		final JSONArray top_stories = json.optJSONArray("top_stories");
		if (top_stories == null) {
			throw new IllegalArgumentException(
					"Latest - fromJson() - 无法正常解析top_stories");
		}
		final ArrayList<TopStoryEntry> topStoriesList = new ArrayList<TopStoryEntry>(
				top_stories.length());
		for (int i = 0, len = top_stories.length(); i < len; i++) {
			topStoriesList.add(TopStoryEntry.fromJson(top_stories
					.optJSONObject(i)));
		}

		return new Latest(date, newsList, is_today, topStoriesList,
				display_date);
	}
}

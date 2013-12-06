package com.newandbie.fake.zhihudaily.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsContentEntry {
	private String body;
	private String image_source;
	private String title;
	private String url;
	private String image;
	private String share_url;
	private int id;
	private String ga_prefix;
	private List<Object> js;
	private String thumbnail;
	private List<String> css;

	public String getBody() {
		return body;
	}

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

	public List<Object> getJs() {
		return js;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public List<String> getCss() {
		return css;
	}

	private NewsContentEntry(String body, String image_source, String title,
			String url, String image, String share_url, int id,
			String ga_prefix, List<Object> js, String thumbnail,
			List<String> css) {
		this.body = body;
		this.image_source = image_source;
		this.title = title;
		this.url = url;
		this.image = image;
		this.share_url = share_url;
		this.id = id;
		this.ga_prefix = ga_prefix;
		this.js = js;
		this.thumbnail = thumbnail;
		this.css = css;
	}

	public static NewsContentEntry fromJson(JSONObject json) {
		final String body = json.optString("body");
		final String image_source = json.optString("image_source");
		final String title = json.optString("title");
		final String url = json.optString("url");
		final String image = json.optString("image");
		final String share_url = json.optString("share_url");
		final int id = json.optInt("id");
		final String ga_prefix = json.optString("ga_prefix");
		final String thumbnail = json.optString("thumbnail");

		final JSONArray jsArray = json.optJSONArray("js");
		final ArrayList<Object> js = new ArrayList<Object>(jsArray.length());
		for (int i = 0, len = jsArray.length(); i < len; i++) {
			js.add(jsArray.optJSONObject(i));
		}

		final JSONArray cssArray = json.optJSONArray("css");
		final ArrayList<String> css = new ArrayList<String>(cssArray.length());
		for (int i = 0, len = cssArray.length(); i < len; i++) {
			css.add(cssArray.optString(i));
		}

		return new NewsContentEntry(body, image_source, title, url, image,
				share_url, id, ga_prefix, js, thumbnail, css);
	}
}

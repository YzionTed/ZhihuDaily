package com.newandbie.fake.zhihudaily;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newandbie.fake.zhihudaily.model.Latest;
import com.newandbie.fake.zhihudaily.model.NewsContentEntry;
import com.newandbie.fake.zhihudaily.provider.ZhihuContract;
import com.newandbie.fake.zhihudaily.util.NetworkUtils;

public class ZhihuService extends Service {
	private static final String TAG = "ZhihuService";
	// 网络工具类
	private RequestQueue mQueue;
	// 任务标记，用于取消任务
	private final Object TASK_TAG = new Object();
	// TODO 后台更新是否开启
	private boolean mIsAutoUpdateEnable = false;
	// 标识当前设备有没有可用的网络
	private boolean mIsNetworkAvailable = false;

	// 主线程上的Handler
	private Handler mainHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
		};
	};

	/** 更新最近新闻 */
	public static final String ACTION_UPDATE_LATEST = "com.newandbie.fake.zhihudaily.update_latest";
	/** TODO 更新Splash图片 */
	public static final String ACTION_UPDATE_SPLASH = "com.newandbie.fake.zhihudaily.update_splash";
	/** 前台Activity销毁 */
	public static final String ACTION_ACTIVITY_DESTROY = "com.newandbie.fake.zhihudaily.activity_destroy";
	/** 断网的标识 */
	public static final String ACTION_BROADCAST_DISCONNECTED = "com.newandbie.fake.zhihudaily.disconnected";
	/** 有网的标识 */
	public static final String ACTION_BROADCAST_CONNECTED = "com.newandbie.fake.zhihudaily.connected";
	/** 拉取新闻数据 */
	public static final String ACTION_FETCH_NEWS = "com.newandbie.fake.zhihudaily.fetch_news";

	/** 拉取新闻数据命令所携带的url地址信息所在的键 */
	public static final String START_KEY_FETCH_NEWS_URL = "start_key_fetch_news";

	// 负责json解析的工作队列
	private BlockingQueue<Runnable> mJsonJobQueue;
	private static final int JSON_JOB_QUEUE_SIZE = 4;
	private JsonWorker mJsonWorker;

	// 负责操作数据库的工作队列
	private BlockingQueue<Runnable> mDbJobQueue;
	private static final int DB_JOB_QUEUE_SIZE = 4;
	private DbWorker mDbWorker;

	private ZhihuNetworkStateBroadcastReceiver mNetStateReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		mQueue = Volley.newRequestQueue(getApplicationContext());
		mJsonJobQueue = new ArrayBlockingQueue<Runnable>(JSON_JOB_QUEUE_SIZE);
		mJsonWorker = new JsonWorker(mJsonJobQueue);
		mJsonWorker.start();
		mDbJobQueue = new ArrayBlockingQueue<Runnable>(DB_JOB_QUEUE_SIZE);
		mDbWorker = new DbWorker(mDbJobQueue);
		mDbWorker.start();

		mNetStateReceiver = new ZhihuNetworkStateBroadcastReceiver();
		registerReceiver(mNetStateReceiver,
				ZhihuNetworkStateBroadcastReceiver.NET_STATE_FILTER);

		// 服务启动时判断一次
		mIsNetworkAvailable = NetworkUtils
				.isNetworkAvailable(getApplicationContext());

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 服务再重启后，intent可能为空
		if (intent != null) {
			final String action = intent.getAction();
			if (!TextUtils.isEmpty(action)) {
				if (ACTION_UPDATE_LATEST.equals(action)) {
					updateLatest();
				} else if (ACTION_ACTIVITY_DESTROY.equals(action)) {
					if (shouldQuit()) {
						stopSelf();
					}
				} else if (ACTION_BROADCAST_DISCONNECTED.equals(action)) {
					mIsNetworkAvailable = false;
				} else if (ACTION_BROADCAST_CONNECTED.equals(action)) {
					mIsNetworkAvailable = true;
				} else if (ACTION_FETCH_NEWS.equals(action)) {
					final String fetchNewsUrl = intent
							.getStringExtra(START_KEY_FETCH_NEWS_URL);
					if (!TextUtils.isEmpty(fetchNewsUrl)) {
						fetchNews(fetchNewsUrl);
					}
				}
				// TODO 更多功能
			}
		}
		return mIsAutoUpdateEnable ? START_STICKY : START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mNetStateReceiver);
		if (mQueue != null) {
			mQueue.cancelAll(TASK_TAG);
			mQueue.stop();
			mQueue = null;
		}
		if (mJsonWorker != null) {
			mJsonWorker.quit();
			mJsonWorker = null;
		}
		if (mDbWorker != null) {
			mDbWorker.quit();
			mDbWorker = null;
		}
		super.onDestroy();
	}

	/**
	 * 更新最近新闻
	 */
	private void updateLatest() {
		// 这个方法不是我想写成这样的
		final JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				IZhihuApi.UPDATE_LATEST, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(final JSONObject response) {
						// 现在拿到了JSON数据，将数据送到子线程进行解析
						final Runnable parseAndSaveJsonJob = new Runnable() {

							@Override
							public void run() {
								final Latest latest = Latest.fromJson(response);
								Runnable saveLatest = new Runnable() {
									@Override
									public void run() {
										// TODO 保存到数据库
										// 保存今日新闻
										ZhihuContract.News.bulkInsertNews(
												getContentResolver(),
												latest.getNews(),
												latest.getDate());
										// 保存推荐新闻
										ZhihuContract.TopStory
												.bulkUpdateTopStories(
														getContentResolver(),
														latest.getTop_stories());
									}
								};
								// 这里是子线程，所以可以用offer，卡就卡吧
								mDbJobQueue.offer(saveLatest);
							}
						};
						// 不用offer()方法是因为可能造成阻塞
						// 现在再主线程，不能阻塞
						try {
							mJsonJobQueue.add(parseAndSaveJsonJob);
						} catch (IllegalStateException queueFull) {
							Log.d(TAG, "updateLatest() - json解析队列已满");
							mainHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										mJsonJobQueue.add(parseAndSaveJsonJob);
									} catch (IllegalStateException ignored) {
										Log.e(TAG,
												"updateLatest() - 推迟json也失败了，要不要考虑扩大工作队列的容量?");
									}
								}
							}, 1000);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "updateLatest() - " + error.getMessage());
					}
				});
		addRequestToQueue(req);
	}

	/**
	 * 判断服务应该退出吗
	 * 
	 * @return
	 */
	private boolean shouldQuit() {
		return !mIsAutoUpdateEnable;
	}

	private void fetchNews(String url) {
		JsonObjectRequest req = new JsonObjectRequest(Method.GET, url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(final JSONObject response) {
						final Runnable parseAndSaveJsonJob = new Runnable() {
							@Override
							public void run() {
								final NewsContentEntry entry = NewsContentEntry
										.fromJson(response);
								final Runnable saveNewsContent = new Runnable() {
									@Override
									public void run() {
										// TODO 插入后通知Loader刷新数据
										ZhihuContract.News.insertNews(
												getContentResolver(), entry);
									}
								};
								mDbJobQueue.offer(saveNewsContent);
							}
						};
						try {
							mJsonJobQueue.add(parseAndSaveJsonJob);
						} catch (IllegalStateException queueFull) {
							Log.d(TAG, "updateLatest() - json解析队列已满");
							mainHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										mJsonJobQueue.add(parseAndSaveJsonJob);
									} catch (IllegalStateException ignored) {
										Log.e(TAG,
												"fetchNews() - 推迟json也失败了，要不要考虑扩大工作队列的容量?");
									}
								}
							}, 1000);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "fetchNews() - " + error.getMessage());
					}
				});
		addRequestToQueue(req);
	}

	/**
	 * 解析JSON的工作线程
	 * 
	 * @author ray
	 * 
	 */
	private class JsonWorker extends Thread {
		private final BlockingQueue<Runnable> mQueue;
		private boolean quited = false;

		public JsonWorker(BlockingQueue<Runnable> queue) {
			super("JSON Worker");
			mQueue = queue;
			setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		}

		@Override
		public void run() {
			super.run();
			while (true) {
				Runnable job = null;
				try {
					job = mQueue.take();
					if (job != null) {
						job.run();
					}
				} catch (InterruptedException ignored) {
					if (!quited) {
						continue;
					} else {
						return;
					}
				}

			}
		}

		/**
		 * 退出工作线程
		 */
		public void quit() {
			quited = true;
			mQueue.clear();
			interrupt();
		}
	}

	private class DbWorker extends Thread {
		private final BlockingQueue<Runnable> mQueue;
		private boolean quited = false;;

		public DbWorker(BlockingQueue<Runnable> queue) {
			super("DB Worker");
			mQueue = queue;
			setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		}

		@Override
		public void run() {
			super.run();
			while (true) {
				Runnable job = null;
				try {
					job = mQueue.take();
					if (job != null) {
						job.run();
					}
				} catch (InterruptedException ignored) {
					if (!quited) {
						continue;
					} else {
						return;
					}
				}
			}
		}

		public void quit() {
			quited = true;
			mQueue.clear();
			interrupt();
		}
	}

	private void addRequestToQueue(Request<?> req) {
		if (mIsNetworkAvailable) {
			req.setTag(TASK_TAG);
			mQueue.add(req);
		} else {
			// FIXME 硬编码
			Toast.makeText(getApplicationContext(), "没有可用的网络，请检查您的网络设置后重试。",
					Toast.LENGTH_LONG).show();
		}
	}
}

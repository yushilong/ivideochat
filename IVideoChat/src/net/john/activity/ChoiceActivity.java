package net.john.activity;

import net.john.R;
import net.john.data.User;
import net.john.util.RTMPConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Constant;
import com.bean.Stream;
import com.bean.VideoRoom;
import com.google.gson.Gson;
import com.http.AsyncClient;
import com.http.AsyncClient.Method;
import com.http.IAsyncClientImpl;

public class ChoiceActivity extends Activity {

	private Button button_video;
	private Button button_upload;
	private Button button_logout, bt_creatRoom;
	AsyncClient creatRoom, pullStream, pushStream;
	private int roomId;
	private boolean roomCreateSuccessed;
	protected VideoRoom videoRoom;
	protected Stream push;
	protected Stream pull;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_choice);

		button_video = (Button) this.findViewById(R.id.button_video);
		button_upload = (Button) this.findViewById(R.id.button_upload);
		button_logout = (Button) this.findViewById(R.id.button_logout);
		bt_creatRoom = (Button) findViewById(R.id.button_creatroom);

		bt_creatRoom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				roomId = getSharedPreferences(Constant.SHARE_NAME, MODE_PRIVATE)
						.getInt(Constant.SHARE_KEY_ROOMID, -1);
				if (roomId == -1) {
					roomId = 1;
				}
				roomId = roomId + 1;
				getSharedPreferences(Constant.SHARE_NAME, MODE_PRIVATE).edit()
						.putInt(Constant.SHARE_KEY_ROOMID, roomId).commit();

				if (creatRoom == null) {
					initCreatRoomRequest();
				}
				creatRoom
						.setUrl(Constant.url_createRoom + "yushilong" + roomId);
				creatRoom.excute();
			}
		});

		button_video.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!roomCreateSuccessed) {
					Toast.makeText(ChoiceActivity.this, "请先初始化房间!",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(ChoiceActivity.this,
						VideoActivity.class);
				intent.putExtra("pull", pull);
				startActivity(intent);
			}
		});

		button_upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChoiceActivity.this,
						ShootActivity.class);
				startActivity(intent);
			}
		});

		button_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences remember = getSharedPreferences("userinfo",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = remember.edit();
				editor.putInt("id", -1);
				editor.putString("phone", null);
				editor.putString("name", null);
				editor.commit();
				User.id = -1;
				User.phone = null;
				User.realname = null;
				RTMPConnectionUtil.connection.close();
				finish();
			}
		});
	}

	private void initCreatRoomRequest() {
		JSONObject creatRoomObject = new JSONObject();
		try {
			creatRoomObject.put("streamQuota", 0);
			creatRoomObject.put("videoDelay", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		creatRoom = new AsyncClient(Constant.url_createRoom + "yushilong"
				+ roomId, findViewById(R.id.pb), new IAsyncClientImpl() {
			public void success(JSONObject successObject) {
				super.success(successObject);
				videoRoom = new Gson().fromJson(successObject.toString(),
						VideoRoom.class);
				Toast.makeText(ChoiceActivity.this, "房间创建成功!",
						Toast.LENGTH_LONG).show();
				roomCreateSuccessed = true;
				if (pushStream == null) {
					initPushStream();
				}
				pushStream.setUrl(Constant.url_createRoom + videoRoom.name
						+ Constant.url_pushStream);
				pushStream.excute();
			};
		});
		creatRoom.setParamObject(creatRoomObject);
	}

	protected void initPullStream() {
		// TODO Auto-generated method stub
		JSONObject pullObject = new JSONObject();
		try {
			pullObject.put("streamType", "1080p");
			pullObject.put("streamLease", "2016-01-01 00:00:00");
			pullObject.put("appId", "appId");
			pullObject.put("clientIP", getIp());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pullStream = new AsyncClient(Constant.url_createRoom + videoRoom.name
				+ Constant.url_pullStream, findViewById(R.id.pb),
				new IAsyncClientImpl() {
					@Override
					public void success(JSONObject successObject) {
						// TODO Auto-generated method stub
						super.success(successObject);
						pull = new Gson().fromJson(successObject.toString(),
								Stream.class);
						TextView tv_pullTextView = ((TextView) findViewById(R.id.tv_pull));
						tv_pullTextView.setText("拉流地址：" + pull.link);
						tv_pullTextView.getPaint().setFlags(
								Paint.UNDERLINE_TEXT_FLAG);
						tv_pullTextView
								.setOnLongClickListener(new OnLongClickListener() {

									@Override
									public boolean onLongClick(View v) {
										// TODO Auto-generated method stub
										ClipboardManager clipboardManager = (ClipboardManager) ChoiceActivity.this
												.getSystemService(Context.CLIPBOARD_SERVICE);
										ClipData clip = ClipData.newPlainText(
												"simpleText", pull.link);
										clipboardManager.setPrimaryClip(clip);
										Toast.makeText(ChoiceActivity.this,
												"已复制", Toast.LENGTH_SHORT)
												.show();
										return false;
									}
								});
					}
				});
		pullStream.setMethod(Method.PUT);
		pullStream.setParamObject(pullObject);
	}

	protected void initPushStream() {
		// TODO Auto-generated method stub
		JSONObject pushObject = new JSONObject();
		try {
			pushObject.put("streamType", "1080p");
			pushObject.put("streamLease", "2016-01-01 00:00:00");
			pushObject.put("appId", "appId");
			pushObject.put("clientIP", getIp());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pushStream = new AsyncClient(Constant.url_createRoom + videoRoom.name
				+ Constant.url_pushStream, findViewById(R.id.pb),
				new IAsyncClientImpl() {
					@Override
					public void success(JSONObject successObject) {
						// TODO Auto-generated method stub
						super.success(successObject);
						push = new Gson().fromJson(successObject.toString(),
								Stream.class);
//						final String pushStreamAddr = push.cdnType == 2 ? push.link + "/"+ push.streamName : push.link;
						final String pushStreamAddr = "rtmp://push1.arenazb.hupu.com/test/336699";
						((TextView) findViewById(R.id.tv_push)).setText("推流地址："+pushStreamAddr);
						new Thread() {
							public void run() {
								RTMPConnectionUtil.ConnectRed5(
										ChoiceActivity.this, pushStreamAddr);
							}
						}.start();
						if (pullStream == null) {
							initPullStream();
						}
						pullStream.setUrl(Constant.url_createRoom
								+ videoRoom.name + Constant.url_pullStream);
						pullStream.excute();
					}
				});
		pushStream.setMethod(Method.PUT);
		pushStream.setParamObject(pushObject);
	}

	public String getIp() {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
}

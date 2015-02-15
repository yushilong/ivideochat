package net.john.activity;

import net.john.R;
import net.john.util.HttpConnectUtil;
import net.john.util.RTMPConnectionUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

public class ChatActivity extends Activity {
	
	//private int toUserId;
	
	private AlertDialog dialog;
	private ProgressDialog progressDialog;

	public static Handler handler;
	private static String who;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//the window without title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_chat);
		
		progressDialog = new ProgressDialog(this);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == 0) { // receiver reject
					progressDialog.dismiss();
					dialog = new AlertDialog.Builder(ChatActivity.this)
					.setMessage(who + getString(R.string.dialog_reject))
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							finish();
						}
					}).show();
				} else if (msg.arg1 == 1) { //add meeting
					progressDialog.dismiss();
					Intent intent = new Intent(ChatActivity.this, VideoActivity.class);
					startActivity(intent);
					finish();
				} else if (msg.arg1 == 2) { //receiver is not login
					progressDialog.dismiss();
					dialog = new AlertDialog.Builder(ChatActivity.this)
					.setMessage(who + getString(R.string.dialog_nologin))
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							finish();
						}
					}).show();
				} else if (msg.arg1 == 3) {
					progressDialog.dismiss();
					dialog = new AlertDialog.Builder(ChatActivity.this)
					.setMessage(who + getString(R.string.dialog_nologin))
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							finish();
						}
					}).show();
				}
			}
		};
		
		String state = this.getIntent().getStringExtra("state");
		if (state != null && state.equals("callyou")) {
			who = this.getIntent().getStringExtra("who");
			
			dialog = new AlertDialog.Builder(ChatActivity.this)
			.setMessage(who + getString(R.string.dialog_callyou))
			.setCancelable(false)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new Thread() {
						public void run() {
							RTMPConnectionUtil.invokeEnterMeetingMethod();
						}
					}.start();
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new Thread() {
						public void run() {
							RTMPConnectionUtil.invokeRejectMethod();
						}
					}.start();
					dialog.dismiss();
					finish();
				}
			}).show();
			
		} else if (state != null && state.equals("call")) {
			who = this.getIntent().getStringExtra("who");
			
			progressDialog.setMessage(getString(R.string.dialog_calling) + who);
			progressDialog.show();
			
			new Thread() {
				public void run() {
					String url = "/VideoCall/checkUserState";
					final String phone = who;
					int toUserId = HttpConnectUtil.getToUserIdByPost(url, phone);
					if (toUserId != -1) {
						RTMPConnectionUtil.invokeMethodFormRed5(toUserId + "");
					} else {
						Toast.makeText(ChatActivity.this, getString(R.string.toast_nouser),
								Toast.LENGTH_LONG).show();
					}
				}
			}.start();
		}
	}
}

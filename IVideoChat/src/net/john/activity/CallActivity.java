package net.john.activity;

import net.john.R;
import net.john.data.User;
import net.john.util.HttpConnectUtil;
import net.john.util.NetworkConnectUtil;
import net.john.util.RTMPConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CallActivity extends Activity {

	private Button button_call;
	private Button button_edit;
	private static TextView textview_name;
	private EditText edittext_receiver;
	
	public static Handler handler;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_call);
		
		button_call = (Button)this.findViewById(R.id.button_call);
		button_edit = (Button)this.findViewById(R.id.button_edit);
		textview_name = (TextView)this.findViewById(R.id.textView_nickname);
		edittext_receiver = (EditText)this.findViewById(R.id.editText_receiver);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == 1) {
					progressDialog.dismiss();
				}
			}
		};
		progressDialog = new ProgressDialog(this);
		
		textview_name.setText(User.realname);

		button_call.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkInfo()) {
					if (RTMPConnectionUtil.connection.connected()) {
						String phone = edittext_receiver.getText().toString();
						Intent intent = new Intent(CallActivity.this, ChatActivity.class);
						intent.putExtra("state","call");
						intent.putExtra("who",phone);
						startActivity(intent);
					} else {
			        	Toast.makeText(CallActivity.this, getString(R.string.toast_red5error), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		button_edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View view = layoutInflater.inflate(R.layout.layout_editdialog, null);
				AlertDialog alertDialog = new AlertDialog.Builder(CallActivity.this)
				.setTitle(getResources().getString(R.string.dialog_title))
				.setView(view)
				.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText mEditText = (EditText)view.findViewById(R.id.editText_editname);
						final String newName = mEditText.getText().toString();
						if (!newName.equals("")) {
							
							progressDialog.setTitle(getResources().getString(R.string.edit_update));
							progressDialog.setMessage(getResources().getString(R.string.dialog_wait));
							progressDialog.show();
							
							new Thread() { //update real name to server
								public void run () {
									int code = 0;
									String url = "/VideoCall/clientUpdate";
									try {
										JSONObject jsonResult = HttpConnectUtil.updateRealnameByPost(url, User.phone, newName);
										code = jsonResult.getInt("code");
										if (code == 1) User.realname = jsonResult.getString("realName");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (code == 1) { //success
										handler.post(new Runnable() {
											public void run() {
												progressDialog.dismiss();
												setRealName(User.realname);
												
												SharedPreferences remember = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
												SharedPreferences.Editor editor = remember.edit();
												editor.putString("name", User.realname);
												editor.commit();
												
												Toast.makeText(CallActivity.this, R.string.textview_update_success, Toast.LENGTH_LONG).show();
											}
										});
									} else if (code == 0){ //fail
										handler.post(new Runnable() {
											public void run() {
												progressDialog.dismiss();
												Toast.makeText(CallActivity.this, R.string.textview_update_fail, Toast.LENGTH_LONG).show();
											}
										});
									} else if (code == 2) { //server issue
										handler.post(new Runnable() {
											public void run() {
												progressDialog.dismiss();
												Toast.makeText(CallActivity.this, R.string.server_issue, Toast.LENGTH_LONG).show();
											}
										});
									}
								}
							}.start();
						}
					}
				}).setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).create();
				
				alertDialog.show();
			}
		});
	}
	
	private boolean checkInfo() {
		//check network state
		NetworkConnectUtil networkstate = new NetworkConnectUtil(CallActivity.this);
		if (networkstate.checkGprsState() == false && networkstate.checkWifiState() == false) {
			new AlertDialog.Builder(CallActivity.this)
			.setMessage(R.string.textview_network_error)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
			return false;
		}
		
		//check input value
		String strReceiver = edittext_receiver.getText().toString();
		if (strReceiver.equals("")) {
			new AlertDialog.Builder(CallActivity.this)
			.setMessage(R.string.textview_empty)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
				@Override
				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
			return false;
		}
		return true;
	}
	
	public static void setRealName(String realname) {
		textview_name.setText(realname);
	}
}

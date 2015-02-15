package net.john.activity;

import net.john.R;
import net.john.data.User;
import net.john.util.HttpConnectUtil;
import net.john.util.NetworkConnectUtil;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SigninActivity extends Activity {
	
	private Button button_login;
	private Button button_register;
	private EditText editText_phonenumber;
	private EditText editText_password;
	
	private ProgressDialog progressDialog;
	Handler handler = new Handler();
	
	Thread loginThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_signin);
		
		button_login = (Button)this.findViewById(R.id.button_login);
		button_register = (Button)this.findViewById(R.id.button_register);
		editText_phonenumber = (EditText)this.findViewById(R.id.editText_phonenum);

		editText_password = (EditText)this.findViewById(R.id.editText_password);
		progressDialog = new ProgressDialog(this);
		
		 SharedPreferences remember = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		 int id = remember.getInt("id", -1);
	     String phone = remember.getString("phone", null);
	     String realname = remember.getString("name", null);
	     if (id != -1 && phone != null && realname != null) {
	    	 User.id = id;
	    	 User.phone = phone;
	    	 User.realname = realname;
	    	 Intent intent = new Intent(SigninActivity.this, ChoiceActivity.class);
	    	 startActivity(intent);
	     }
		
		button_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if (checkInfo()) {
					
					progressDialog.setTitle(getResources().getString(R.string.button_login));
					progressDialog.setMessage(getResources().getString(R.string.dialog_wait));
					progressDialog.show();
					
					new Thread() { //check user information when login
						public void run() {
							String phone = editText_phonenumber.getText().toString();
							String password = editText_password.getText().toString();
							int code = 0;
							String url = "/VideoCall/clientLogin";
							try {
								JSONObject jsonResult = HttpConnectUtil.getResponseByPost(url, phone, password);
								code = jsonResult.getInt("code");
								User.id = jsonResult.getInt("id");
								User.realname = jsonResult.getString("realName");
								User.phone = jsonResult.getString("phone");
							} catch (JSONException e) {
								Log.d("DEBUG", e.getMessage());
							}
							if (code == 1) { //success
								handler.post(new Runnable() {
									public void run() {
										//put username into sharedpreferences
										SharedPreferences remember = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = remember.edit();
										editor.putInt("id", User.id);
										editor.putString("phone", User.phone);
										editor.putString("name", User.realname);
										editor.commit();
										
										progressDialog.dismiss();
										Intent intent = new Intent(SigninActivity.this, ChoiceActivity.class);
										startActivity(intent);
									}
								});
							} else if (code == 0){ //fail
								handler.post(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.textview_error)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							} else if (code ==2 ) { //server issue
								handler.post(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.server_issue)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							}
							else {
								handler.post(new Runnable() { //also server issue
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.server_issue)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							}
						}
					}.start();
				}*/
				Intent intent = new Intent(SigninActivity.this, ChoiceActivity.class);
				startActivity(intent);
			}
		});
		
		button_register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkInfo()) {
					
					progressDialog.setTitle(getResources().getString(R.string.button_register));
					progressDialog.setMessage(getResources().getString(R.string.dialog_wait));
					progressDialog.show();
					
					new Thread() { //register user information to server
						public void run() {
							String phone = editText_phonenumber.getText().toString();
							String password = editText_password.getText().toString();
							int code = 0;
							String url = "/VideoCall/clientRegister";
							try {
								JSONObject jsonResult = HttpConnectUtil.getResponseByPost(url, phone, password);
								code = jsonResult.getInt("code");
								User.id = jsonResult.getInt("id");
								User.realname = jsonResult.getString("realName");
								User.phone = jsonResult.getString("phone");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (code == 1) { //success
								handler.post(new Runnable() {
									public void run() {
										//put username into sharedpreferences
										SharedPreferences remember = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = remember.edit();
										editor.putInt("id", User.id);
										editor.putString("phone", User.phone);
										editor.putString("name", User.realname);
										editor.commit();
										
										progressDialog.dismiss();
										Intent intent = new Intent(SigninActivity.this, ChoiceActivity.class);
										startActivity(intent);
									}
								});
							} else if (code == 0) { //fail
								handler.post(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.textview_exist)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							} else if (code == 2) { //server issue
								handler.post(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.server_issue)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							}
							else {
								handler.post(new Runnable() { //also server issue
									public void run() {
										progressDialog.dismiss();
										new AlertDialog.Builder(SigninActivity.this)
										.setMessage(R.string.server_issue)
										.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
												dialog.dismiss();
											}
										}).show();
									}
								});
							}
						}
					}.start();
				}
			}
		});
	}
	
	private boolean checkInfo() {
		
		//check network state
		NetworkConnectUtil networkstate = new NetworkConnectUtil(SigninActivity.this);
		if (networkstate.checkGprsState() == false && networkstate.checkWifiState() == false) {
			new AlertDialog.Builder(SigninActivity.this)
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
		String login = editText_phonenumber.getText().toString();
		String password = editText_password.getText().toString();
		if (login.equals("") || password.equals("")) {
			new AlertDialog.Builder(SigninActivity.this)
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
}

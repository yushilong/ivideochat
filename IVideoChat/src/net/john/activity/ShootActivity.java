package net.john.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.john.R;
import net.john.util.HttpConnectUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ShootActivity extends Activity implements SurfaceHolder.Callback{
	
	private Button button_start;
	private Button button_stop;
	private Button button_back;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private File storageDir;
	private File tempFile;
	private MediaRecorder mediaRecorder;
	private Camera camera;
	private Spinner spinner;
	private int width;
	private int height;
	private Handler handler;
	private TextView textView_time;
	private Handler dialog_handler;
	private Runnable uploadRunnable;
	private ProgressDialog dialogProgress;
	private Thread uploadThread;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//the window without title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//the display orientation
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.layout_shoot);
        
        button_start = (Button)this.findViewById(R.id.button_start);
        button_stop = (Button)this.findViewById(R.id.button_stop);
        button_back = (Button)this.findViewById(R.id.button_back);
        surfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(ShootActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        spinner = (Spinner)this.findViewById(R.id.spinner_size);
        width = 480;
        height = 320;
        handler = new Handler();
        textView_time = (TextView)this.findViewById(R.id.textView_time);
        dialogProgress = new ProgressDialog(ShootActivity.this);
        
        dialog_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == 0) { //cancel
					uploadThread.interrupt();
					Toast.makeText(ShootActivity.this, getString(R.string.toast_cancel_upload), Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == 1) { //success
					dialogProgress.dismiss();
					Toast.makeText(ShootActivity.this, getString(R.string.toast_success_upload), Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == 2) { //fail
					dialogProgress.dismiss();
					Toast.makeText(ShootActivity.this, getString(R.string.toast_fail_upload), Toast.LENGTH_LONG).show();
				}
			}
		};
		
		uploadRunnable = new Runnable() {
			
			@Override
			public void run () {
				try {
					Thread.sleep(10000);
					String url = "/VideoCall/clientUpload";
					int code = 0;
					
					try {
						JSONObject jsonResult = HttpConnectUtil.uploadFileByPost(url, tempFile);
						code = jsonResult.getInt("code");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg = dialog_handler.obtainMessage();
					if (code == 1) { //success
						msg.arg1 = 1;
						msg.sendToTarget();
					} else if (code == 0) { //fail
						msg.arg1 = 2;
						msg.sendToTarget();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
        
        button_start.setOnClickListener(btnStartListener);
        button_stop.setOnClickListener(btnStopListener);
        button_back.setOnClickListener(btnBackListener);
        spinner.setOnItemSelectedListener(spinnerListener);
        
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
        	storageDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "IVC" + File.separator);
        	if (!storageDir.exists()) {
        		storageDir.mkdir();
        	}
        	button_stop.setEnabled(false);
        } else {
        	button_start.setEnabled(false);
        	Toast.makeText(ShootActivity.this, getString(R.string.toast_nosdcard), Toast.LENGTH_LONG).show();
        }
	}
	
	private View.OnClickListener btnStartListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			button_start.setEnabled(false);
			button_stop.setEnabled(true);
			spinner.setVisibility(View.GONE);
			
			handler.postDelayed(refreshTime, 1000);
			
			try {
				tempFile = File.createTempFile("IVC", ".3gp", storageDir);
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setCamera(camera);
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
				mediaRecorder.setVideoSize(width, height);
				mediaRecorder.setVideoFrameRate(16);
				mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
				mediaRecorder.setOutputFile(tempFile.getAbsolutePath());
				mediaRecorder.prepare();
				mediaRecorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}; 
	
	private View.OnClickListener btnStopListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			button_start.setEnabled(true);
			button_stop.setEnabled(false);
			spinner.setVisibility(View.VISIBLE);
			
			handler.removeCallbacks(refreshTime);
			
			if (mediaRecorder != null) {
				try {
				mediaRecorder.stop();
				mediaRecorder.release();
				Log.e("DEBUG", tempFile.getAbsolutePath());
				} catch(RuntimeException e) {
					Log.e("DEBUG", e.getMessage());
				}
			}
			new AlertDialog.Builder(ShootActivity.this)
			.setMessage(R.string.dialog_upload)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) { 
					// TODO Auto-generated method stub
					dialogProgress.setMessage(getString(R.string.dialog_uploading));
					Message msg = dialog_handler.obtainMessage();	
					msg.arg1 = 0;
					dialogProgress.setCancelMessage(msg);
					dialogProgress.show();
					
					uploadThread = new Thread(uploadRunnable);
					uploadThread.start(); //upload Thread start
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
		}
	};
	
	private View.OnClickListener btnBackListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private Runnable refreshTime = new Runnable() {
		
		int sec = 0;
		int min = 0;
		int hou = 0;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sec++;
			handler.postDelayed(refreshTime, 1000);
			if (sec >= 60) {
				sec = sec % 60;
				min++;
			}
			if (min >= 60) {
				min = min % 60;
				hou++;
			}
			textView_time.setText(timeFormat(hou) + ":" + timeFormat(min) + ":" + timeFormat(sec));
		}
	};
	
	private String timeFormat(int t) {
		if (t / 10 == 0) {
			return "0" + t;
		} else {
			return t + "";
		}
	}
	
	private OnItemSelectedListener spinnerListener = new Spinner.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Spinner spinner = (Spinner)arg0;
			String wh[] = spinner.getSelectedItem().toString().split("\\*");
			width = Integer.parseInt(wh[0]);
			height = Integer.parseInt(wh[1]);
			try {
				camera.lock();
				camera.stopPreview();
				Camera.Parameters para = camera.getParameters();
				para.setPreviewSize(width, height);
				camera.setParameters(para);
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				Log.d("DEBUG", camera.getParameters().getPreviewSize().width + "*" 
				+ camera.getParameters().getPreviewSize().height);
				camera.unlock();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//try {
			camera = Camera.open();
			List<Size> supportedSize = camera.getParameters().getSupportedPreviewSizes();
			if (supportedSize != null) {
				List<String> list = new ArrayList<String>();
				for (Size s: supportedSize) {
					list.add(s.width + "*" + s.height);
				}
				list.remove(0);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShootActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
				spinner.setAdapter(adapter);
			}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.release();
	}
}

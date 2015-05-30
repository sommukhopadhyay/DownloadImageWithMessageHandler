package com.somitsolutions.android.training.downloadimageusingmessagehandler;


import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener{
	Button mStartDownloadButton, mDisplayImageButton;
	EditText mURL;
	ImageView mImageView; 
	ProgressDialog mProgressDialog;
	Context context;
	Bitmap mBitmap;
	Runnable mDownloadRunnable;
	MessageHandler messageHandler; 
	String mUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mStartDownloadButton = (Button)findViewById(R.id.buttonDownloadImage);
		mDisplayImageButton = (Button)findViewById(R.id.buttonDisplayImage);
		mURL = (EditText)findViewById(R.id.editTextURL);
		mImageView = (ImageView)findViewById(R.id.imageView1);
		mImageView.setVisibility(View.INVISIBLE);
		mStartDownloadButton.setOnClickListener(this);
		mDisplayImageButton.setOnClickListener(this);
		
		context = this;
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("On Progress...");
		mProgressDialog.setCancelable(true);
		
		mDownloadRunnable = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//android.os.Debug.waitForDebugger();
				Message msg1 = messageHandler.obtainMessage(MessageHandler.SHOW_PROGRESS_DIALOG);
				messageHandler.sendMessage(msg1);
				mBitmap = downloadBitmap(mUrl);
				Message msg2 = messageHandler.obtainMessage(MessageHandler.DISMISS_PROGRESS_DIALOG);
				messageHandler.sendMessage(msg2);
			}
		};
		messageHandler = new MessageHandler();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(mStartDownloadButton)){
			mUrl = mURL.getText().toString();
			mUrl = mUrl.replace(" ", "");
			
			if(mUrl != null && !mUrl.isEmpty()){
				mStartDownloadButton.setEnabled(false);
				Thread downloadThread = new Thread(mDownloadRunnable);
				downloadThread.start();
			}
		}
		if(v.equals(mDisplayImageButton)){
			mImageView.setImageBitmap(mBitmap);
			mImageView.setVisibility(View.VISIBLE);
		}
	}
	
private Bitmap downloadBitmap(String url) { 
		
		final DefaultHttpClient client = new DefaultHttpClient(); 
		
		final HttpGet getRequest = new HttpGet(url); 
		
		try { 
			HttpResponse response = client.execute(getRequest); 
			final int statusCode = response.getStatusLine().getStatusCode(); 
			if (statusCode != HttpStatus.SC_OK) {
				return null; 
				} 
			
			final HttpEntity entity = response.getEntity();
			
			if (entity != null) { 
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					Options optionSample = new BitmapFactory.Options();
					optionSample.inSampleSize = 4; // Or 8 for smaller image
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,optionSample);
					return bitmap;
					
				} finally { 
					if (inputStream != null) { 
						inputStream.close(); 
						} 
					//entity.consumeContent(); 
					} 
				} 
			} catch (Exception e) {
				getRequest.abort();
				Log.e("ImageDownload", e.toString());
				} 
		return null;
	}

private class MessageHandler extends Handler {
   
    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    public void handleMessage(Message msg) {
            
        switch (msg.what) {
        case SHOW_PROGRESS_DIALOG:
        	if(mProgressDialog == null){
			 	mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setMessage("On Progress...");
				mProgressDialog.setCancelable(true);
			 }
			 mProgressDialog.show(); 
            break;

        case DISMISS_PROGRESS_DIALOG:
        	mProgressDialog.dismiss();
			mProgressDialog = null;
			mStartDownloadButton.setEnabled(true);
            break;
        	}
    	}
	}
}

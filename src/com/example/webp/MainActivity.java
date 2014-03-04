package com.example.webp;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.backport.webp.WebPFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {

	private int[] filelengths = new int[] { 33, 138, 500 };

	private String dir = "/sdcard/webp_test/";

	private TextView textView;
	
	private File resultFile = new File(dir,"result.txt");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.count);
		Bitmap bitmap = WebPFactory.decodeFile(dir+"/webp/50/33.webp");
//		Bitmap bitmap = BitmapFactory.decodeFile(dir+"/webp/50/33.webp");
		((ImageView)findViewById(R.id.img)).setImageBitmap(bitmap);
		testStart();
	}
	
	private void testStart(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				FileUtils.deleteQuietly(resultFile);
				String result = "nature:"
						+ new JSONObject(testNature()).toString();
				result += "\nwebp"
						+ new JSONObject(new HashMap<String, HashMap>() {
							{
								put("50", testWebp(50));
								put("80", testWebp(80));
								put("100", testWebp(100));
							}
						}).toString();
				try {
					FileUtils.writeStringToFile(resultFile,result);
					setCount("done");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	private void setCount(final String count){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				textView.setText(count);
				
			}
		});
	}
	
	private long testWebp(String file) {
		long time = new Date().getTime();
		try {
			Bitmap bitmap = WebPFactory.decodeFile(file);
			time = new Date().getTime() - time;
			bitmap.recycle();
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
			finish();
		}

		return time;
	}
	
	private long testImage(String file) {
		long time = new Date().getTime();
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(file);
			time = new Date().getTime() - time;
			bitmap.recycle();
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
			finish();
		}

		return time;
	}

	private HashMap<String, String> testNature() {
		String dir = this.dir + "nature/";
		HashMap<String, String> map = new HashMap<String, String>();
		for (int length : filelengths) {
			long total = 0, count = 100;
			for (int i = 0; i < count; i++) {
				total += testImage(dir + length + ".jpg");
			}
			map.put("" + length, "" + total / count);
		}
		return map;
	}

	private HashMap<String, String> testWebp(int quality) {
		String dir = this.dir + "webp/" + quality + "/";
		HashMap<String, String> map = new HashMap<String, String>();
		for (int length : filelengths) {
			long total = 0, count = 100;
			for (int i = 0; i < count; i++) {
				total += testWebp(dir + length + ".webp");
			}
			map.put("" + length, "" + total / count);
		}
		return map;
	}

}

package android.backport.webp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Factory to encode and decode WebP images into Android Bitmap
 * @author Alexey Pelykh
 */
public final class WebPFactory {
	
	// Load library at class loading
	static {
		System.loadLibrary("webpbackport");
	};
	
	private static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
	}
	
	public static Bitmap decodeFile(String file){
		InputStream rawImageStream = null;
		Bitmap webpBitmap = null;
		try {
			rawImageStream = new FileInputStream(new File(file));
			byte[] data = streamToBytes(rawImageStream);
			webpBitmap = WebPFactory.nativeDecodeByteArray(
					data, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rawImageStream != null){
				try {
					rawImageStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		return webpBitmap;
		
	}
	
	/**
	 * Decodes byte array to bitmap 
	 * @param data Byte array with WebP bitmap data
	 * @param opts Options to control decoding. Accepts null
	 * @return Decoded bitmap
	 */
	public static native Bitmap nativeDecodeByteArray(byte[] data, BitmapFactory.Options options);
	
	/**
	 * Encodes bitmap into byte array
	 * @param bitmap Bitmap
	 * @param quality Quality, should be between 0 and 100
	 * @return Encoded byte array
	 */
	public static native byte[] nativeEncodeBitmap(Bitmap bitmap, int quality);
	
}

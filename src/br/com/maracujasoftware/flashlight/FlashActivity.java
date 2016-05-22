package br.com.maracujasoftware.flashlight;

import java.io.IOException;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FlashActivity extends Activity {
	
	Integer oriBrightnessValue;
	Boolean flashlightStatus = false; // false = off, true = on
	Camera mCamera = null;
	Parameters parameters;
	//LinearLayout lflashlightcontrol;
	SurfaceView preview;
	SurfaceHolder mHolder;
	private Button bt_toggle_flashlight;
	 
	private AdView adView_1;
	private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		super.onCreate(savedInstanceState);
		
		// Retrieve the brightness value for future use
		try {
			oriBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
		setContentView(R.layout.activity_flash);
		
		//lflashlightcontrol = (LinearLayout) findViewById(R.id.flashlightcontrol);
		preview = (SurfaceView) findViewById(R.id.Spreview);
		mHolder = preview.getHolder();
		
		bt_toggle_flashlight = (Button)findViewById(R.id.bt_toggle_flashlight);
		bt_toggle_flashlight.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				if (ContextCompat.checkSelfPermission(FlashActivity.this,
		                Manifest.permission.CAMERA)
		        != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(FlashActivity.this,
			                new String[]{Manifest.permission.CAMERA},
			                14);
					
				}else{
				toggleFlashLight();
				}
				// Turn off the cam if it is on
				/*turnOffFlashLight();
				if (mCamera != null) {
					mCamera.release();
					mCamera = null;
				}*/
				
			}
		});
		
		adView_1 = (AdView)this.findViewById(R.id.adViewFlashlight);
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .addTestDevice("F0777154C5F794B0B7A1EF4120502169")
	    .build();
	    adView_1.loadAd(adRequest);
	    
	    interstitial = new InterstitialAd(this);
 		interstitial.setAdUnitId("ca-app-pub-7040951679419231/5504911508");
 		interstitial.loadAd(adRequest);
 		interstitial.setAdListener(new AdListener(){
 	          public void onAdLoaded(){
 	        	 interstitial.show();
 	          }
 	});
		
	}
	
	/**
	 * Revert to original brightness
	 * Also turn off the flashlight if api level < 14
	 * And turn off the cam if we're not using it
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		// Revert to original brightness
		setBrightness(oriBrightnessValue);
		
		// Turn off the flashlight if api level < 14 as leaving it on would result in a FC
		if (Integer.valueOf(android.os.Build.VERSION.SDK) < 14 || flashlightStatus == false) {
			turnOffFlashLight();
			
			// Turn off the cam if it is on
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
		adView_1.pause();
	}
	
	/**
	 * Check if the device has a flashlight
	 * @return True if the device has a flashlight, false if not
	 */
	public Boolean deviceHasFlashlight() {
		Context context = this;
		PackageManager packageManager = context.getPackageManager();
		
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Set brightness to a desired value
	 * @param brightness
	 */
	private void setBrightness(int brightness) {
	    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
	    layoutParams.screenBrightness = brightness/100.0f;
	    getWindow().setAttributes(layoutParams);
	}
	

	/**
	 * Toggle the flashlight on/off status
	 */
	public void toggleFlashLight() {
		if (flashlightStatus == false) { // Off, turn it on
			turnOnFlashLight();
		} else { // On, turn it off
			turnOffFlashLight();
		}
	}
	

	/**
	 * Turn on the flashlight if the device has one.
	 * Also set the background colour to white and brightness to max.
	 */
	public void turnOnFlashLight() {
		// Safety measure if it's already on
		turnOffFlashLight();
		
		// Turn on the flash if the device has one
		if (deviceHasFlashlight()) {
			
			// Switch on the cam for app's life
			if (mCamera == null) {
				// Turn on Cam
				mCamera = Camera.open();
				try {
					mCamera.setPreviewDisplay(mHolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCamera.startPreview();
			}
	
			// Turn on LED
			parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameters);
			//ibFlashLight.setImageResource(R.drawable.buttonon);
		}
		
		// Set background color
	//	lflashlightcontrol.setBackgroundColor(Color.WHITE);
		
		// Set brightness to max
		//setBrightness(100);
		
		// Self awareness
		flashlightStatus = true;
	}
	
	/**
	 * Turn off the flashlight if we find it to be on.
	 * Also set the background to black and revert to original brightness
	 */
	public void turnOffFlashLight() {
		// Turn off flashlight
		if (mCamera != null) {
			parameters = mCamera.getParameters();
			if (parameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
				//ibFlashLight.setImageResource(R.drawable.buttonoff);
			}
		}
		
		// Set background color
		//lflashlightcontrol.setBackgroundColor(Color.BLACK);
		
		// Revert to original brightness
		//setBrightness(oriBrightnessValue);
		
		// Self awareness
		flashlightStatus = false;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		adView_1.resume();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		adView_1.destroy();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
	        String permissions[], int[] grantResults) {
	    switch (requestCode) {
	        case 14: {
	            // If request is cancelled, the result arrays are empty.
	            if (grantResults.length > 0
	                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

	                // permission was granted, yay! Do the
	                // contacts-related task you need to do.
	            	toggleFlashLight();

	            } else {

	                // permission denied, boo! Disable the
	                // functionality that depends on this permission.
	            	Toast.makeText(this,(R.string.givePermission), Toast.LENGTH_LONG).show();
	            	
	            }
	            return;
	        }

	        // other 'case' lines to check for other
	        // permissions this app might request
	    }
	}
}

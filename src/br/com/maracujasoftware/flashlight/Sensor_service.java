package br.com.maracujasoftware.flashlight;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.widget.Toast;

public class Sensor_service extends Service implements SensorEventListener{
	SensorManager sensorManager;
	int count=0;
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	private static final int MIN_FORCE = 10;
	private static final int MIN_DIRECTION_CHANGE = 3;
	private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
	private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;
	private long mFirstDirectionChangeTime = 0;
	private long mLastDirectionChangeTime;
	private int mDirectionChangeCount = 0;
	
	Boolean flashlightStatus = false; // false = off, true = on
	Camera mCamera = null;
	Parameters parameters;

	@Override
	public void onSensorChanged(SensorEvent event) {
		getAccelerometer(event);
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onDestroy() 
	{
	    super.onDestroy();
	    sensorManager.unregisterListener(this);
	    
	 // Turn off the flashlight if api level < 14 as leaving it on would result in a FC
	 		if (Integer.valueOf(android.os.Build.VERSION.SDK) < 14 || flashlightStatus == false) {
	 			turnOffFlashLight();
	 			
	 			// Turn off the cam if it is on
	 			if (mCamera != null) {
	 				mCamera.release();
	 				mCamera = null;
	 			}
	 		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    sensorManager.registerListener(this,sensorManager
	            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);

	    return START_STICKY;
	}
	
	public void onStart(Intent intent, int startId)
	{
	    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    sensorManager.registerListener(this,sensorManager
	            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
	}
	@SuppressWarnings("deprecation")
	private void getAccelerometer(SensorEvent event) 
	{
	    //Toast.makeText(getApplicationContext(),"Shaked",Toast.LENGTH_LONG).show();

	    float x = event.values[SensorManager.DATA_X];
	    float y = event.values[SensorManager.DATA_Y];
	    float z = event.values[SensorManager.DATA_Z];

	   // float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

	    float xMovement = Math.abs(x - lastX);
	    float yMovement = Math.abs(y - lastY);
	    float zMovement = Math.abs(z - lastZ);

	    
	    
	   // if (totalMovement <= MIN_FORCE)	    {
	 //  if ((xMovement > MIN_FORCE) || (yMovement > MIN_FORCE) || (zMovement > MIN_FORCE)) {
	    if ((xMovement > MIN_FORCE) ) {
		    
	        long now = System.currentTimeMillis();
	        if (mFirstDirectionChangeTime == 0) 
	        {
	            mFirstDirectionChangeTime = now;
	            mLastDirectionChangeTime = now;
	        }

	        long lastChangeWasAgo = now - mLastDirectionChangeTime;

	        if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) 
	        {
	            mLastDirectionChangeTime = now;
	            mDirectionChangeCount++;

	            lastX = x;
	            lastY = y;
	            lastZ = z;

	            if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) 
	            {
	                long totalDuration = now - mFirstDirectionChangeTime;
	                if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) 
	                {
	                    
	                    resetShakeParameters();
	                    Toast.makeText(getApplicationContext(),"Light!",Toast.LENGTH_SHORT).show();
	                    toggleFlashLight();
	                }
	            }

	        }
	        else 
	        {
	            resetShakeParameters();
	        }
	    }
	    
	    
	}
	
	protected void onResume() 
	{
	    sensorManager.registerListener(this,sensorManager
	            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
	}
	
	protected void onPause()
	{
	    sensorManager.unregisterListener(this);
	}

	private void resetShakeParameters() 
	{
	    mFirstDirectionChangeTime = 0;
	    mDirectionChangeCount = 0;
	    mLastDirectionChangeTime = 0;
	    lastX = 0;
	    lastY = 0;
	    lastZ = 0;
	}
	
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
				/*try {
					mCamera.setPreviewDisplay(mHolder);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				mCamera.startPreview();
			}
	
			// Turn on LED
			parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameters);
		}
		
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
			}
		}
		
		// Self awareness
		flashlightStatus = false;
	}
	public Boolean deviceHasFlashlight() {
		Context context = this;
		PackageManager packageManager = context.getPackageManager();
		
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		} else {
			return false;
		}
	}
	
}

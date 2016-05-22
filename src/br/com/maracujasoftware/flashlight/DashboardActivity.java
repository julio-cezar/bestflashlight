package br.com.maracujasoftware.flashlight;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DashboardActivity extends Activity {
	private AdView adView_1;
	private LinearLayout linearlayout;
	private boolean serviceIniciado = false;
	public static final String MyPREFERENCES = "MyPrefs" ;
	SharedPreferences sharedpref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		/*linearlayout = (LinearLayout) findViewById(R.id.LinearLayoutDash);
		
		adView_1 = new AdView(this);
		adView_1.setAdUnitId("ca-app-pub-7040951679419231/2112623107");
		adView_1.setAdSize(AdSize.BANNER);
		linearlayout.addView(adView_1);
		
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		.addTestDevice("F0777154C5F794B0B7A1EF4120502169")
		.build();	
		adView_1.loadAd(adRequest);*/
		
		if (ContextCompat.checkSelfPermission(DashboardActivity.this,
                Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
			
			ActivityCompat.requestPermissions(DashboardActivity.this,
	                new String[]{Manifest.permission.CAMERA},
	                14);
			
		}
		
		adView_1 = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .addTestDevice("F0777154C5F794B0B7A1EF4120502169")
	    .build();
	    adView_1.loadAd(adRequest);
	    
	    sharedpref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
	    
	    SharedPreferences.Editor editor = sharedpref.edit();
	    editor.putBoolean("serviceIniciado", false);
	    editor.commit();
		
	}
	
	public void selecionarOpcao(View view) {
		Intent i;
		switch (view.getId()) {		
		case R.id.bt_flash:
			i = new Intent(DashboardActivity.this, FlashActivity.class);
			startActivity(i);
			break;
		case R.id.bt_screen:
			 i = new Intent(DashboardActivity.this, ScreenActivity.class);
			startActivity(i);
			break;
		case R.id.bt_color:
			 i = new Intent(DashboardActivity.this, ColorDashboardActivity.class);
			startActivity(i);
			break;
		case R.id.bt_doacao:
			 i = new Intent(DashboardActivity.this, DonationActivity.class);
			startActivity(i);
			break;
		}
	}
	
	public void toggleSensor(View view) {
		
		SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE); 
		serviceIniciado = prefs.getBoolean("serviceIniciado", false);
		
		if(serviceIniciado==false){
		Intent i = new Intent(this, Sensor_service.class);
		startService(i);
		
		SharedPreferences.Editor editor = sharedpref.edit();
	    editor.putBoolean("serviceIniciado", true);
	    editor.commit();
	    Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.shakeenabled),Toast.LENGTH_SHORT).show();
		} else {
			stopService(new Intent(this, Sensor_service.class));
			
			SharedPreferences.Editor editor = sharedpref.edit();
		    editor.putBoolean("serviceIniciado", false);
		    editor.commit();		  
		    Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.shakedesabled),Toast.LENGTH_SHORT).show();
		}
		//Intent i = new Intent();
		////i.setClassName("br.com.maracujasoftware.flashlight.service", "br.com.maracujasoftware.flashlight.service.Sensor_service");
		//String pkg = "br.com.maracujasoftware.flashlight.service";
		//String cls = "br.com.maracujasoftware.flashlight.service.Sensor_service";
		//i.setComponent(new ComponentName(pkg, cls));
	
    
    	
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
		adView_1.resume();
	}
	
	
	@Override
	protected void onStop(){
		super.onStop();
		adView_1.pause();
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		adView_1.destroy();
	}
}

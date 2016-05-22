package br.com.maracujasoftware.flashlight;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ColorDashboardActivity extends Activity {
	private AdView adView_1;
	private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_dashboard);
		
		adView_1 = (AdView)this.findViewById(R.id.adViewColorDashboard);
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .addTestDevice("F0777154C5F794B0B7A1EF4120502169")
	    .build();
	    adView_1.loadAd(adRequest);
	    
	    interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-7040951679419231/4167779103");
		interstitial.loadAd(adRequest);
	}
	
	public void selecionarOpcao(View view) {
		Intent i;
		switch (view.getId()) {		
		case R.id.colorred:
			i = new Intent(ColorDashboardActivity.this, ColorScreenActivity.class);
			i.putExtra("cor", "RED");
			startActivity(i);
			break;
		case R.id.colorblue:
			 i = new Intent(ColorDashboardActivity.this, ColorScreenActivity.class);
			 i.putExtra("cor", "BLUE");
			startActivity(i);
			break;
		case R.id.colorgreen:
			 i = new Intent(ColorDashboardActivity.this, ColorScreenActivity.class);
			 i.putExtra("cor", "GREEN");
			startActivity(i);
			break;
		case R.id.coloryellow:
			 i = new Intent(ColorDashboardActivity.this, ColorScreenActivity.class);
			 i.putExtra("cor", "YELLOW");
			startActivity(i);
			break;
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		interstitial.show();
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

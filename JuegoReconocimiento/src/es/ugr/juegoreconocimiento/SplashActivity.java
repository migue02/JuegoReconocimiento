package es.ugr.juegoreconocimiento;

import es.ugr.juegoreconocimiento.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;



/**
 * Clase inicial puramente estetica. Muestra un splash durante 2 segundos
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class SplashActivity extends Activity {
	//Tiempo de splash en milisegundos
	private int splashTime = 2000;
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.splash);
		getApplicationContext();
		 new Handler().postDelayed(new Runnable(){
	        	public void run(){
					/*Pasados los dos segundos inicia la activity "activityApp"*/
	        		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
	        		startActivity(intent);
					/*Destruye esta*/
	        		finish();
	        	};
	 
	        }, splashTime);

	}
	
	

}

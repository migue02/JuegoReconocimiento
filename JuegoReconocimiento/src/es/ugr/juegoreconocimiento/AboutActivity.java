package es.ugr.juegoreconocimiento;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Clase para la actividad el "Acerca De"
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */




public class AboutActivity extends Activity{
	//TODO:Sustituya la url del promotor. Para a朼dir mas promotores (si los hay) siga el esquema definido
	private final String URL_PROMOTOR1 = "http://cevug.ugr.es/";
	//TODO:Sustituya el email del promotor. Para a朼dir mas promotores (si los hay) siga el esquema definido
	private final String MAIL_PROMOTOR1 = "responsableinstitucional@ugr.es";
	//URL AppsUGR
	private final String URL_APPS_UGR = "http://apps.ugr.es";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_activity_layout);
		
	}
	
	//Acci贸n al hacer click en la imagen del promotor1. Abre la web.
	public void onClickPromotor1(View v){
		Intent webBrowser = new Intent(Intent.ACTION_VIEW,Uri.parse(URL_PROMOTOR1));
		startActivity(webBrowser);
		
	}
	
	//Acci贸n al pulsar el bot贸n de contacto del promotor1. Abre un cuadro de di谩logo para enviar un email
	public void onClickBotonContacto1(View v){
		Intent enviarEmail = new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:"+MAIL_PROMOTOR1));
		startActivity(enviarEmail);
		
		
		
	}
	
	
	//Acci贸n al hacer click en el boton "Ir a Apps UGR"
	public void onClickAppsUgr(View v){
		Intent webBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_APPS_UGR));
		startActivity(webBrowser);
	}
	

	

}

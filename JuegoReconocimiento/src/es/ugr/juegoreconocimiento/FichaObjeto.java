package es.ugr.juegoreconocimiento;

import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.objetos.Objeto;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FichaObjeto extends Activity {
	
	private Objeto oObjeto;
	private int idObjeto=-1; 
	private ObjetoDataSource objetoDS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ficha_objeto);
		objetoDS = new ObjetoDataSource(this);
		objetoDS.open();
		Bundle extras = null;
		if (savedInstanceState == null) {
		    extras = getIntent().getExtras();
		    if(extras == null) {
		    	oObjeto = new Objeto();
		    } else {
		        idObjeto = (int)extras.getLong("Objeto");
		    }
		} else {
			idObjeto =(int) extras.getLong("Objeto");
		}
		
		if (idObjeto != -1){
			oObjeto = objetoDS.getObjeto(idObjeto);
			setTitle(oObjeto.getNombre());
			((EditText) findViewById(R.id.edtNombreObjeto)).setText(oObjeto.getNombre());
			((TextView) findViewById(R.id.edtTamanioObjeto)).setText(oObjeto.getCols()+"x"+oObjeto.getRows());
		}else if (oObjeto == null)
			oObjeto = new Objeto();
		
		if (oObjeto.getImagen() != null){
			((ImageView) findViewById(R.id.imgObjeto)).setImageBitmap(oObjeto.getImagen());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ficha_objeto, menu);
		return true;
	}
	
	@Override
	public void onPause() {
		objetoDS.close();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		objetoDS.open();
		super.onResume();
	}
	
	public void onModificaObjetoClick(View v){
		Toast.makeText(this, "Ya se mirará...", Toast.LENGTH_SHORT).show();
	}
	
	public void onGuardarClick(View v){
		boolean result=false;
		oObjeto.setNombre(((EditText) findViewById(R.id.edtNombreObjeto)).getText().toString());
		if (oObjeto.getId() == -1)
			objetoDS.createObjeto(oObjeto);
		else
			result = objetoDS.modificaObjeto(oObjeto);
		if (result)
			Toast.makeText(this, "Objeto modificado...", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Vuelve a guardar...", Toast.LENGTH_SHORT).show();
		
	}
	
	public void onCancelarClick(View v){
		finish();
	}
	

}

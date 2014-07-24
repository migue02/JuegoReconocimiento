package es.ugr.gestion_resultados;


import es.ugr.juegoreconocimiento.R;
import es.ugr.lista_navegacion.ListaNavegacionActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class ResultadosActivity extends Activity {
	
	private ResultadosLibrary resultados = new ResultadosLibrary();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultados2);
		resultados.onCreate(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					ListaNavegacionActivity.class));
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		resultados.onDestroy();
	}
	
}

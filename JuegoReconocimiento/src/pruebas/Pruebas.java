package pruebas;

import es.ugr.juegoreconocimiento.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Pruebas extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pruebas);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pruebas, menu);
		return true;
	}

}

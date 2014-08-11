package es.ugr.reconocimiento;

import java.util.ArrayList;
import java.util.List;

import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Resultado;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ResultadoSerie extends Activity {

	private List<Resultado> lResultados;
	private Resultado oResultado;

	private FrameLayout layoutGrafica;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultado_serie);

		Intent intent = getIntent();
		int[] lIdResultados = intent.getIntArrayExtra("Resultados");

		if (lIdResultados.length > 0) {

			lResultados = new ArrayList<Resultado>();

			ResultadoDataSource dsResultado = new ResultadoDataSource(this);
			dsResultado.open();

			for (int id : lIdResultados) {
				lResultados.add(dsResultado.getResultado(id));
			}

			dsResultado.close();

			oResultado = lResultados.get(0);

			layoutGrafica = (FrameLayout) findViewById(R.id.layoutGrafica);

			((TextView) findViewById(R.id.edtAlumnoSerie)).setText(intent
					.getStringExtra("Alumno")
					+ " "
					+ intent.getStringExtra("Serie"));
			((TextView) findViewById(R.id.edtTiempoTotal)).setText(String
					.valueOf(oResultado.getDuracion()));
			((TextView) findViewById(R.id.edtPuntuacion)).setText(String
					.valueOf(oResultado.getPuntuacion()));

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.resultado_serie, menu);
		return true;
	}
	
	public void onAceptarClick(View v){
		finish();
	}

}

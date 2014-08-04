package es.ugr.reconocimiento;

import java.util.List;
import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.SerieEjercicios;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EmpezarJuego extends Activity {

	private Boolean ciclico = false;
	private AlumnoDataSource alumnoDS;
	private SerieEjerciciosDataSource serieDS;
	private List<Alumno> listaAlumnos;
	private List<SerieEjercicios> listaSeries;
	private Alumno alumnoSeleccionado;
	private SerieEjercicios serieSeleccionada;
	private Spinner spinnerAl, spinnerSer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empezar_juego);

		alumnoSeleccionado = new Alumno();
		serieSeleccionada = new SerieEjercicios();
		alumnoDS = new AlumnoDataSource(this);
		serieDS = new SerieEjerciciosDataSource(this);
		alumnoDS.open();
		serieDS.open();

		listaSeries = serieDS.getAllSeriesEjercicios();
		spinnerSer = (Spinner) findViewById(R.id.spinnerSerie);

		ArrayAdapter<SerieEjercicios> spinner_adapter_ser = new ArrayAdapter<SerieEjercicios>(
				this, R.layout.spinner_layout, listaSeries);
		spinner_adapter_ser.setDropDownViewResource(R.layout.spinner_layout);
		spinnerSer.setAdapter(spinner_adapter_ser);
		spinnerSer
				.setOnItemSelectedListener(new MyOnItemSelectedListenerSerie());

		listaAlumnos = alumnoDS.getAllAlumnos();
		spinnerAl = (Spinner) findViewById(R.id.spinnerAlumno);

		ArrayAdapter<Alumno> spinner_adapter_al = new ArrayAdapter<Alumno>(
				this, R.layout.spinner_layout, listaAlumnos);
		spinner_adapter_al.setDropDownViewResource(R.layout.spinner_layout);
		spinnerAl.setAdapter(spinner_adapter_al);
		spinnerAl
				.setOnItemSelectedListener(new MyOnItemSelectedListenerAlumno());
	}

	public void setCiclico(View v) {
		v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha));
		ImageView btnCiclico = (ImageView) findViewById(R.id.btnCiclico);
		TextView textoCiclico = (TextView) findViewById(R.id.textoCiclico);
		ciclico = !ciclico;
		if (ciclico) {
			btnCiclico.setImageResource(R.drawable.ciclico_pulsado);
			textoCiclico.setText("Modo Cíclico: ON");
		} else {
			btnCiclico.setImageResource(R.drawable.ciclico);
			textoCiclico.setText("Modo Cíclico: OFF");
		}
	}

	public void empezarJuego(View v) {
		if (alumnoSeleccionado.getIdAlumno() != -1
				&& serieSeleccionada.getIdSerie() != -1) {
			v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha));
			Intent myIntent = new Intent(EmpezarJuego.this, Juego.class);
			myIntent.putExtra("Alumno", alumnoSeleccionado);
			myIntent.putExtra("Serie", serieSeleccionada);
			myIntent.putExtra("Ciclico", ciclico);
			finish();
			startActivity(myIntent);
		} else {
			Toast.makeText(getApplicationContext(),
					"Debe elegir un alumno y una serie para poder jugar",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onResume() {
		alumnoDS.open();
		serieDS.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		alumnoDS.close();
		serieDS.close();
		super.onPause();
	}

	public class MyOnItemSelectedListenerAlumno implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (parent.getId() == R.id.spinnerAlumno) {
				alumnoSeleccionado = ((Alumno) parent.getItemAtPosition(pos));
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	public class MyOnItemSelectedListenerSerie implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (parent.getId() == R.id.spinnerSerie) {
				serieSeleccionada = ((SerieEjercicios) parent
						.getItemAtPosition(pos));
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}

}

package es.ugr.reconocimiento;

import java.util.List;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.SerieEjercicios;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
		((ImageView) findViewById(R.id.btnCiclico)).setAlpha(80);
		// ((ImageView) findViewById(R.id.btnCiclico)).setImageAlpha(80);

	}

	public void setCiclico(View v) {
		ImageView btnCiclico = (ImageView) findViewById(R.id.btnCiclico);
		TextView textoCiclico = (TextView) findViewById(R.id.textoCiclico);
		ciclico = !ciclico;
		if (ciclico) {
			btnCiclico.setAlpha(255);
			textoCiclico.setText("Modo Cíclico: ON");
		} else {
			btnCiclico.setAlpha(80);
			textoCiclico.setText("Modo Cíclico: OFF");
		}
	}

	public void empezarJuego(View v) {
		if (alumnoSeleccionado.getIdAlumno() != -1
				&& serieSeleccionada.getIdSerie() != -1) {
			if (serieSeleccionada.getEjercicios().size() > 0) {
				Animation animation = AnimationUtils.loadAnimation(this,
						R.anim.alpha);
				animation
						.setAnimationListener(new Animation.AnimationListener() {
							public void onAnimationEnd(Animation animation) {
								Intent myIntent = new Intent(EmpezarJuego.this,
										ComenzarEjercicio.class);
								myIntent.putExtra("Alumno", alumnoSeleccionado);
								myIntent.putExtra("Serie", serieSeleccionada);
								myIntent.putExtra("Ciclico", ciclico);
								myIntent.putExtra("idEjercicio",
										serieSeleccionada.getEjercicios()
												.get(0));
								finish();
								startActivity(myIntent);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
							}
						});
				v.startAnimation(animation);
			} else
				new AlertDialog.Builder(EmpezarJuego.this)
						.setTitle("No se puede comenzar el juego")
						.setPositiveButton("Aceptar", null)
						.setMessage(
								"La serie elegida no contiene ningún ejercicio")
						.show();
		} else {
			new AlertDialog.Builder(EmpezarJuego.this)
					.setTitle("No se puede comenzar el juego")
					.setPositiveButton("Aceptar", null)
					.setMessage(
							"Debe elegir un alumno y una serie para poder jugar")
					.show();
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

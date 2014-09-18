package es.ugr.reconocimiento;

import java.util.ArrayList;
import java.util.List;

import es.ugr.adaptadores.AdapterEmpezarEjercicioObjeto;
import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.utilidades.CountDownAnimation;
import es.ugr.utilidades.Globals;
import es.ugr.utilidades.Utilidades;
import es.ugr.utilidades.CountDownAnimation.CountDownListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ListView;
import android.widget.TextView;

public class ComenzarEjercicio extends Activity {

	private EjercicioDataSource eds;
	private ObjetoDataSource ods;
	private List<Objeto> lo;
	private Ejercicio ejercicio;
	private AdapterEmpezarEjercicioObjeto adapter;
	private ListView listViewObjetos;
	private TextView countdown;
	private CountDownAnimation countDownAnimation;
	private Context context;
	private Alumno oAlumno;
	private SerieEjercicios oSerie;
	private boolean bCiclico = false;
	private boolean bPrimeraEjecucion;
	private boolean bEmpezado = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comienzo_ejercicio);

		bPrimeraEjecucion = EsPrimeraEjecucion(savedInstanceState);

		context = this;
		if (!bPrimeraEjecucion)
			((Globals) getApplication()).JuegoParado = true;

		listViewObjetos = (ListView) findViewById(R.id.listViewObjetos);

		listViewObjetos.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && !bEmpezado) {
					if (!bPrimeraEjecucion)
						((Globals) getApplication()).JuegoParado = false;
					else
						primeraEjecucionJuego();
					finish();
					bEmpezado = true;
				}
				return false;
			}
		});

		eds = new EjercicioDataSource(this);
		ods = new ObjetoDataSource(this);

		ods.open();
		eds.open();

		Intent intent = getIntent();
		int idEjercicio = intent.getIntExtra("idEjercicio", -1);
		if (idEjercicio > -1) {
			lo = new ArrayList<Objeto>();
			ejercicio = eds.getEjercicios(idEjercicio);

			((TextView) findViewById(R.id.Titulo)).setText(ejercicio
					.getNombre());
			((TextView) findViewById(R.id.Descripcion)).setText(ejercicio
					.getDescripcion());
			((TextView) findViewById(R.id.Duracion)).setText(String
					.valueOf(ejercicio.getDuracion() + " Min"));

			String escenario = "Objetos en la mesa: ";
			for (int i = 0; i < ejercicio.getObjetos().size(); i++) {
				if (i == 0)
					escenario += ejercicio.getObjetos().get(i);
				else if (i < ejercicio.getObjetos().size() - 1)
					escenario += "," + ejercicio.getObjetos().get(i);
				else
					escenario += " y " + ejercicio.getObjetos().get(i) + ".";
			}

			((TextView) findViewById(R.id.Escenario)).setText(escenario);

			for (int i = 0; i < ejercicio.getObjetosReconocer().size(); i++)
				lo.add(ods.getObjeto(ejercicio.getObjetosReconocer().get(i)));
			adapter = new AdapterEmpezarEjercicioObjeto(this,
					R.layout.adapter_comienzo_ejercicio_objeto, lo);
			listViewObjetos.setAdapter(adapter);
			ods.close();
			eds.close();
			ejercicio.playSonidoDescripcion(getApplicationContext());
		}

	}

	public boolean EsPrimeraEjecucion(Bundle savedInstanceState) {
		Bundle extras;
		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if (extras != null)
				try {
					oAlumno = (Alumno) extras.getSerializable("Alumno");
					oSerie = (SerieEjercicios) extras.getSerializable("Serie");
					bCiclico = extras.getBoolean("Ciclico");
				} catch (Exception e) {
				}
		} else {
			try {
				oAlumno = (Alumno) savedInstanceState.getSerializable("Alumno");
				oSerie = (SerieEjercicios) savedInstanceState
						.getSerializable("Serie");
				bCiclico = savedInstanceState.getBoolean("Ciclico");
			} catch (Exception e) {
			}
		}
		return (oAlumno != null && oSerie != null);
	}

	public void primeraEjecucionJuego() {
		Intent myIntent = new Intent(ComenzarEjercicio.this, Juego.class);
		myIntent.putExtra("Alumno", oAlumno);
		myIntent.putExtra("Serie", oSerie);
		myIntent.putExtra("Ciclico", bCiclico);
		startActivity(myIntent);
	}

	public void onIniciaJuego(View v) {
		if (!bEmpezado) {
			countdown = (TextView) findViewById(R.id.Countdown);
			countDownAnimation = new CountDownAnimation(context, countdown, 4,
					"", -1);
			countDownAnimation.start();
			Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
					0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			countDownAnimation.setAnimation(scaleAnimation);

			countDownAnimation.setCountDownListener(new CountDownListener() {
				@Override
				public void onCountDownEnd(CountDownAnimation animation) {
					if (!bPrimeraEjecucion)
						((Globals) getApplication()).JuegoParado = false;
					else
						primeraEjecucionJuego();
					finish();
				}
			});
			bEmpezado = true;
			if (lo != null)
				Utilidades.LiberaImagenes(lo);
			if (ejercicio != null)
				ejercicio.stopSonido();
		}
	}

}
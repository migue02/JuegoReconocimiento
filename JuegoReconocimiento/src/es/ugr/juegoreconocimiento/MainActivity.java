package es.ugr.juegoreconocimiento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Sexo;
import es.ugr.reconocimiento.EmpezarJuego;
import es.ugr.reconocimiento.ReconocimientoObjeto2;
import es.ugr.utilidades.Utilidades;
import es.ugr.juegoreconocimiento.R;
import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Actividad principal. Esta actividad se puede cambiar completamente. SÃ³lo es
 * obligatorio enlazar de alguna manera con la actividad "AboutActivity"
 * 
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 */

public class MainActivity extends Activity {

	private static final String TAG = "Reconocimiento::MainActivity";
	ImageButton alumnos, series, resultados, ejercicios;
	Button reinicia;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("opencv_java");
				System.loadLibrary("nonfree");
				System.loadLibrary("juegoReconocimientoLib");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView bienv = (TextView) findViewById(R.id.textoBienv);
		ObjectAnimator.ofFloat(bienv, "rotationY", 0, 360)
				.setDuration(3 * 1000).start();

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);

		// Gestion alumnos
		alumnos = (ImageButton) findViewById(R.id.buttonAlum);
		alumnos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent alumnoIntent = new Intent(getApplicationContext(),
						GestionAlumnos.class);
				startActivity(alumnoIntent);

			}
		});

		// Resultados
		resultados = (ImageButton) findViewById(R.id.buttonEsta);
		resultados.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent resultadosIntent = new Intent(getApplicationContext(),
						Resultados.class);
				startActivity(resultadosIntent);

			}
		});

		// Gestion de series de ejercicios

		series = (ImageButton) findViewById(R.id.ButtonSerie);
		series.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent seriesIntent = new Intent(getApplicationContext(),
						SeriesEjercicios.class);
				startActivity(seriesIntent);
			}
		});

		ejercicios = (ImageButton) findViewById(R.id.buttonEj);
		ejercicios.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent ejerciciosIntent = new Intent(getApplicationContext(),
						Ejercicios.class);
				startActivity(ejerciciosIntent);
			}
		});

		reinicia = (Button) findViewById(R.id.Reinicia);
		reinicia.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlumnoDataSource ads = new AlumnoDataSource(v.getContext());
				ObjetoDataSource ods = new ObjetoDataSource(v.getContext());
				EjercicioDataSource eds = new EjercicioDataSource(v
						.getContext());
				SerieEjerciciosDataSource seds = new SerieEjerciciosDataSource(
						v.getContext());
				ResultadoDataSource rds = new ResultadoDataSource(v
						.getContext());

				// Abrir y borrar todos los datos
				ads.open();
				ods.open();
				eds.open();
				seds.open();
				rds.open();

				rds.borraTodosResultados();
				seds.eliminarTodasSeriesEjercicios();
				eds.eliminaTodosEjercicios();
				ods.eliminaTodosObjetos();
				ads.borraTodosAlumno();

				// Alumnos

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, 14);
				cal.set(Calendar.MONTH, 6);
				cal.set(Calendar.YEAR, 1989);

				Date d = cal.getTime();

				cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, 25);
				cal.set(Calendar.MONTH, 9);
				cal.set(Calendar.YEAR, 1990);

				Date d2 = cal.getTime();

				Alumno alumno1 = ads.createAlumno("Juan Manuel",
						"Lucena Morales", d, Sexo.Hombre, "");
				Alumno alumno2 = ads.createAlumno("Miguel",
						"Morales Rodríguez", d2, Sexo.Mujer, "");

				ads.close();

				// Crear objetos
				/*MatOfKeyPoint matKey = new MatOfKeyPoint();
				Mat mat = new Mat();
				ObjetoDataSource obs = new ObjetoDataSource(v.getContext());
				obs.open();
				Objeto objeto1 = obs.createObjeto("Pelota tenis",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto2 = obs.createObjeto("Pelota beisbol",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto3 = obs.createObjeto("Teléfono",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto4 = obs.createObjeto("Bolígrafo",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto5 = obs.createObjeto("Rotulador",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto6 = obs.createObjeto("Estuche",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto7 = obs.createObjeto("Lápiz",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto8 = obs.createObjeto("Vaso",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				Objeto objeto9 = obs.createObjeto("Plato",
						Utilidades.keypointsToJson(matKey),
						Utilidades.matToJson(mat), 0, 0);
				obs.close();*/

				// Crear Ejercicios
				Objeto objAngryBirds,objAdidas,objKelme,objUGR,objWindows,objApple;
				try {
					
					objAngryBirds=ods.getObjeto(10);//Angry Birds
					objAdidas=ods.getObjeto(11);//Adidas
					objKelme=ods.getObjeto(12);//Kelme
					objUGR=ods.getObjeto(13);//ugr
					objWindows=ods.getObjeto(14);//windows
					objApple=ods.getObjeto(15);//apple
					//objeto7=ods.getObjeto(16);
					//objeto8=ods.getObjeto(17);
					//objeto9=ods.getObjeto(18);
					objKelme.setNombre("Kelme");
					ods.modificaObjeto(objKelme);
				} catch (Exception e) {
					MatOfKeyPoint matKey = new MatOfKeyPoint();
					Mat mat = new Mat();
					objAngryBirds = ods.createObjeto("Pelota tenis",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					objAdidas = ods.createObjeto("Pelota beisbol",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					objKelme = ods.createObjeto("Teléfono",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					objUGR= ods.createObjeto("Bolígrafo",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					objWindows = ods.createObjeto("Rotulador",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					objApple = ods.createObjeto("Estuche",
							Utilidades.keypointsToJson(matKey),
							Utilidades.matToJson(mat), 0, 0);
					ods.close();
				}
				
				
				ArrayList<Integer> escenario = new ArrayList<Integer>();
				escenario.add((int) objAngryBirds.getId());
				escenario.add((int) objAdidas.getId());
				escenario.add((int) objKelme.getId());
				escenario.add((int) objUGR.getId());
				escenario.add((int) objWindows.getId());
				escenario.add((int) objApple.getId());
				
				ArrayList<Integer> lista = new ArrayList<Integer>();
				lista.add((int) objAdidas.getId());
				lista.add((int) objKelme.getId());
				String tp1 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar el objeto Adidas, y cuando finalice dicho ejercicios, deberá detectar el objeto Kelme");
				Ejercicio p1 = eds.createEjercicio("Adidas y kelme",
						escenario, tp1, 5, lista);
				lista.clear();
				lista.add((int) objUGR.getId());
				lista.add((int) objApple.getId());
				lista.add((int) objWindows.getId());
				String tp2 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar el objeto UGR, luego Apple, y cuando finalice, deberá detectar el objeto Windows");
				Ejercicio p2 = eds.createEjercicio("UGR, Apple y Windows", escenario,
						tp2, 6, lista);
				lista.clear();
				/*lista.add((int) objeto4.getId());
				lista.add((int) objeto5.getId());
				String tp3 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar un bolígrafo, y cuando finalice dicho ejercicios, deberá detectar un rotulador");
				eds.createEjercicio("Bolígrafo y rotulador", escenario, tp3, 7,
						lista);*/
				lista.clear();
				/*lista.add((int) objeto6.getId());
				lista.add((int) objeto7.getId());
				String tp4 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar un estuche, y cuando finalice dicho ejercicios, deberá detectar un lápiz");
				eds.createEjercicio("Estuche y lápiz", escenario, tp4, 8, lista);*/
				lista.clear();
				/*lista.add((int) objeto8.getId());
				lista.add((int) objeto9.getId());
				String tp5 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar un vaso, y cuando finalice dicho ejercicios, deberá detectar un plato");
				eds.createEjercicio("Vaso y plato", escenario, tp5, 9, lista);*/
				lista.clear();
				escenario.clear();
				eds.close();
				ods.close();

				// Crear Serie

				ArrayList<Integer> miarray = new ArrayList<Integer>();
				miarray.add(p1.getIdEjercicio());
				miarray.add(p2.getIdEjercicio());
				SerieEjercicios serie1 = seds.createSerieEjercicios("PELOTAS",
						miarray, 0, new Date());
				seds.actualizaDuracion(serie1);
				seds.close();

				// Crear Resultados

				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				Date fecha1 = cal.getTime();

				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -3);
				Date fecha2 = cal.getTime();

				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -5);
				Date fecha3 = cal.getTime();

				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -8);
				Date fecha4 = cal.getTime();

				Resultado r = new Resultado(1, alumno1.getIdAlumno(), serie1
						.getIdSerie(), fecha1, 0, 48, 39, 9, 8.1);
				Resultado r2 = new Resultado(2, alumno1.getIdAlumno(), serie1
						.getIdSerie(), fecha2, 0, 30, 18, 12, 6.0);

				Resultado r3 = new Resultado(3, alumno2.getIdAlumno(), serie1
						.getIdSerie(), fecha1, 0, 12, 9, 3, 7.5);
				Resultado r4 = new Resultado(4, alumno2.getIdAlumno(), serie1
						.getIdSerie(), fecha3, 0, 26, 23, 3, 8.8);

				Resultado r5 = new Resultado(5, alumno1.getIdAlumno(), serie1
						.getIdSerie(), fecha4, 0, 35, 28, 7, 8);
				Resultado r6 = new Resultado(6, alumno2.getIdAlumno(), serie1
						.getIdSerie(), fecha4, 0, 24, 14, 10, 5.8);

				rds.createResultado(r);
				rds.createResultado(r2);
				rds.createResultado(r3);
				rds.createResultado(r4);
				rds.createResultado(r5);
				rds.createResultado(r6);

				rds.close();

			}
		});

	}

	public void onClickAcercaDe(View v) {
		Intent acercaDeIntent = new Intent(this, AboutActivity.class);
		startActivity(acercaDeIntent);
	}

	public void onClickEmpezar(View v) {
		Intent empiezaJuego = new Intent(this, EmpezarJuego.class);
		startActivity(empiezaJuego);
	}

	public void onClickReconocimiento(View v) {
		Intent intent = new Intent(this, ReconocimientoObjeto2.class);
		startActivity(intent);
	}
	
}

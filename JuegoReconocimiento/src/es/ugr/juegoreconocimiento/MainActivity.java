 package es.ugr.juegoreconocimiento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.lista_navegacion.ListaNavegacionActivity;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.Resultado;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.objetos.TiposPropios.Sexo;
import es.ugr.reconocimiento.EmpezarJuego;
import es.ugr.utilidades.Ficheros;
import es.ugr.utilidades.FontsOverride;
import es.ugr.utilidades.Utilidades;

/**
 * Actividad principal. Esta actividad se puede cambiar completamente. SÃ³lo es
 * obligatorio enlazar de alguna manera con la actividad "AboutActivity"
 * 
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 */

public class MainActivity extends Activity {

	private static final String TAG = "Reconocimiento::MainActivity";
	ImageButton alumnos, series, resultados, ejercicios;
	Button reinicia;
	
	private MediaPlayer player;

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
				System.loadLibrary("juegoLib");
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
		FontsOverride.setDefaultFont(this, "MONOSPACE", "wd.ttf");
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
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "1");
				startActivity(intent);

			}
		});

		// Resultados
		resultados = (ImageButton) findViewById(R.id.buttonEsta);
		resultados.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "2");
				startActivity(intent);

			}
		});

		// Gestion de series de ejercicios

		series = (ImageButton) findViewById(R.id.ButtonSerie);
		series.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "4");
				startActivity(intent);
			}
		});

		ejercicios = (ImageButton) findViewById(R.id.buttonEj);
		ejercicios.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "3");
				startActivity(intent);
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
						"Lucena Morales", d, Sexo.Mujer, "");
				Alumno alumno2 = ads.createAlumno("Miguel",
						"Morales Rodríguez", d2, Sexo.Hombre, "");
 
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
				objAngryBirds=ods.getObjeto(1);
				if (objAngryBirds != null) {					
					objAdidas=ods.getObjeto(2);//Adidas
					objKelme=ods.getObjeto(3);//Kelme
					objUGR=ods.getObjeto(4);//ugr
					objWindows=ods.getObjeto(5);//windows
					objApple=ods.getObjeto(6);//apple
				}else {
					MatOfKeyPoint matKey = new MatOfKeyPoint();
					Mat mat = new Mat();
					
					objAngryBirds = ods.createObjeto("Angry Birds", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					
					objAdidas = ods.createObjeto("Adidas", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					objKelme = ods.createObjeto("Kelme", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					objUGR= ods.createObjeto("UGR", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					objWindows = ods.createObjeto("Windows", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					objApple = ods.createObjeto("Apple", "", new Date(), Utilidades.keypointsToJson(matKey), Utilidades.matToJson(mat), 0, 0, "", "", "", "");
					ods.close();
				}
				
				
				ArrayList<String> escenario = new ArrayList<String>();
				escenario.add(objAngryBirds.getNombre());
				escenario.add(objAdidas.getNombre());
				escenario.add(objKelme.getNombre());
				escenario.add(objUGR.getNombre());
				escenario.add(objWindows.getNombre());
				escenario.add(objApple.getNombre());
				
				ArrayList<String> lista = new ArrayList<String>();
				lista.add(objAdidas.getNombre());
				lista.add(objKelme.getNombre());
				String tp1 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar el objeto Adidas, y cuando finalice dicho ejercicios, deberá detectar el objeto Kelme");
				Ejercicio p1 = eds.createEjercicio("Adidas y kelme", new Date(), escenario, tp1, 5, lista, "");
                lista.clear();
				lista.add(objUGR.getNombre());
				lista.add(objApple.getNombre());
				lista.add(objWindows.getNombre());
				String tp2 = new String(
						"En este ejercicios el alumno deberá seleccionar de entre una serie de objetos, en primer lugar el objeto UGR, luego Apple, y cuando finalice, deberá detectar el objeto Windows");
				Ejercicio p2 = eds.createEjercicio("UGR, Apple y Windows", new Date(), escenario,
						tp2, 6, lista, "");
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
				
				
				Ficheros.eliminaImagenes(MainActivity.this);//Elimina contenido de la carpeta imagenes
				Ficheros.eliminaSonidos(MainActivity.this);//Elimina contenido de la carpeta sonidos				
				Ficheros.creaCarpetas(MainActivity.this);//Crea la carpeta images y sounds
				Ficheros.copyAssets(MainActivity.this);//Inicializa la carpeta sonidos desde el assets
			}
		});
		
		AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("Background.mp3");
			player = new MediaPlayer();
			player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
		    player.prepare();
		    player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}

	public void onClickAcercaDe(View v) {
		Intent acercaDeIntent = new Intent(this, AboutActivity.class);
		startActivity(acercaDeIntent);
	}

	public void onClickEmpezar(View v) {
		Intent empiezaJuego = new Intent(this, EmpezarJuego.class);
		startActivity(empiezaJuego);
	}

	public void onClickLogin(View v) {
		Intent intent = new Intent(this, LoginAdministrador.class);
		startActivity(intent);
	}
	
	public void onObjetosClick(View v) {
		Intent intent = new Intent(getApplicationContext(),
				ListaNavegacionActivity.class);
		intent.putExtra("ID", "5");
		startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		player.pause();
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		player.start();
		super.onResume();
	}
	
}

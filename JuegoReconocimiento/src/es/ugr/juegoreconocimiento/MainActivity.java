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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
		FontsOverride.setDefaultFont(this, "MONOSPACE", "infantil2.ttf");
		setContentView(R.layout.activity_main);

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);

		AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("Background.mp3");
			player = new MediaPlayer();
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			player.prepare();
			player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void onClickAcercaDe(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						AboutActivity.class);
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onClickEmpezar(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						EmpezarJuego.class);
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onClickLogin(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						LoginAdministrador.class);
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onAlumnosClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "1");
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onResultadosClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "2");
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onEjerciciosClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "3");
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onSeriesClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "4");
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onObjetosClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(getApplicationContext(),
						ListaNavegacionActivity.class);
				intent.putExtra("ID", "5");
				startActivity(intent);
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onBorrarEjerciciosObjetos(View v) {
		AlumnoDataSource ads = new AlumnoDataSource(this);
		ObjetoDataSource ods = new ObjetoDataSource(this);
		EjercicioDataSource eds = new EjercicioDataSource(this);
		SerieEjerciciosDataSource seds = new SerieEjerciciosDataSource(this);
		ResultadoDataSource rds = new ResultadoDataSource(this);

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

		ads.close();
		ods.close();
		eds.close();
		seds.close();
		rds.close();

		Ficheros.eliminaImagenes(MainActivity.this);
		Ficheros.eliminaSonidos(MainActivity.this);
		Ficheros.creaCarpetas(MainActivity.this);
		Ficheros.copyAssets(MainActivity.this);
		
		Toast.makeText(MainActivity.this, "Borrado alumnos, series y resultados", Toast.LENGTH_LONG).show();
	}

	private void ReiniciaBD() {
		boolean bDesdeCero = false;
		
		
		AlumnoDataSource ads = new AlumnoDataSource(this);
		ObjetoDataSource ods = new ObjetoDataSource(this);
		EjercicioDataSource eds = new EjercicioDataSource(this);
		SerieEjerciciosDataSource seds = new SerieEjerciciosDataSource(this);
		ResultadoDataSource rds = new ResultadoDataSource(this);

		// Abrir y borrar todos los datos
		ads.open();
		ods.open();
		eds.open();
		seds.open();
		rds.open();
		
		bDesdeCero = ods.getAllObjetos().size() == 0;
		if (bDesdeCero)
			bDesdeCero = eds.getAllEjercicios().size() == 0;

		rds.borraTodosResultados();
		seds.eliminarTodasSeriesEjercicios();
		if (bDesdeCero){
			eds.eliminaTodosEjercicios();
			ods.eliminaTodosObjetos();
		}
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

		Alumno alumno1 = ads.createAlumno("Juan Manuel", "Lucena Morales", d,
				Sexo.Mujer, "");
		Alumno alumno2 = ads.createAlumno("Miguel", "Morales Rodríguez", d2,
				Sexo.Hombre, "");

		// Crear Ejercicios
		Ejercicio p1 = null;
		if (bDesdeCero){
			ArrayList<String> lista = new ArrayList<String>();
			p1 = eds.createEjercicio("Ejercicio 1", new Date(), lista,
					"Descripcion 2", 5, lista, "");
		}

		// Crear Serie
		ArrayList<Integer> miarray = new ArrayList<Integer>();
		if (bDesdeCero)
			miarray.add(p1.getIdEjercicio());
		// miarray.add(p2.getIdEjercicio());
		SerieEjercicios serie1 = seds.createSerieEjercicios("Serie", miarray,
				0, new Date());
		seds.actualizaDuracion(serie1);

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

		Resultado r = new Resultado(1, alumno1.getIdAlumno(),
				serie1.getIdSerie(), fecha1, 0, 48, 39, 9, 8.1);
		Resultado r2 = new Resultado(2, alumno1.getIdAlumno(),
				serie1.getIdSerie(), fecha2, 0, 30, 18, 12, 6.0);

		Resultado r3 = new Resultado(3, alumno2.getIdAlumno(),
				serie1.getIdSerie(), fecha1, 0, 12, 9, 3, 7.5);
		Resultado r4 = new Resultado(4, alumno2.getIdAlumno(),
				serie1.getIdSerie(), fecha3, 0, 26, 23, 3, 8.8);

		Resultado r5 = new Resultado(5, alumno1.getIdAlumno(),
				serie1.getIdSerie(), fecha4, 0, 35, 28, 7, 8);
		Resultado r6 = new Resultado(6, alumno2.getIdAlumno(),
				serie1.getIdSerie(), fecha4, 0, 24, 14, 10, 5.8);

		rds.createResultado(r);
		rds.createResultado(r2);
		rds.createResultado(r3);
		rds.createResultado(r4);
		rds.createResultado(r5);
		rds.createResultado(r6);

		ads.close();
		ods.close();
		eds.close();
		seds.close();
		rds.close();

		Ficheros.eliminaImagenes(MainActivity.this);// Elimina contenido de la
													// carpeta imagenes
		Ficheros.eliminaSonidos(MainActivity.this);// Elimina contenido de la
													// carpeta sonidos
		Ficheros.creaCarpetas(MainActivity.this);// Crea la carpeta images y
													// sounds
		Ficheros.copyAssets(MainActivity.this);// Inicializa la carpeta sonidos
												// desde el assets
		
		if (!bDesdeCero) 
			Toast.makeText(MainActivity.this, "Borrado y creado alumnos, series y resultados, para borrar todo pulsar papelera azul", Toast.LENGTH_LONG).show();

	}

	public void onReiniciaClick(View v) {

		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				ReiniciaBD();
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void onSalirClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				salir();
			}

			public void onAnimationRepeat(Animation animation) {
				// Do nothing!
			}

			public void onAnimationStart(Animation animation) {
				// Do nothing!
			}
		});
		v.startAnimation(animation);
	}

	public void salir() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					MainActivity.this.finish();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("¿Está seguro que desea salir del juego?")
				.setPositiveButton("Sí", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			salir();
		return super.onKeyDown(keyCode, event);
	}

}

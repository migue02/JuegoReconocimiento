package es.ugr.reconocimiento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import es.ugr.juegoreconocimiento.R;
import es.ugr.activities.ResultadoSerie;
import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.Resultado;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.utilidades.Globals;
import es.ugr.utilidades.JSONParser;
import es.ugr.utilidades.Utilidades;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.reconocimiento.JuegoLibreria;

public class Juego extends Activity implements CvCameraViewListener2 {

	// //////////////////////////
	// PRUEBAS /////////////////
	// //////////////////////////
	int matcher = 1;

	private static final String TAG = "Juego::Activity";

	private CameraBridgeViewBase mOpenCvCameraView;
	private int nObjeto = -1;
	private Mat mGray, mRgba;
	private Mat aux, auxGray;
	private MatOfKeyPoint keypoints_obj = new MatOfKeyPoint();
	private Mat descriptores_obj = new Mat();
	private KeyPoint[] listaKP_obj;
	private ArrayList<Mat> matsDescriptores = new ArrayList<Mat>();
	private ArrayList<MatOfKeyPoint> matsKeyPoints = new ArrayList<MatOfKeyPoint>();
	private int[] colsArray, rowsArray;
	private TextToSpeech ttobj;

	private double hessianThreshold = 1300;
	private int nOctaves = 4, nOctaveLayers = 2;
	private boolean extended = false, upright = false;

	private Alumno oAlumno;
	private SerieEjercicios oSerie;
	private boolean bCiclico = false;

	private List<Ejercicio> lEjercicios;
	private List<Objeto> lObjetosEscenario;
	private List<Objeto> lObjetos;

	private EjercicioDataSource dsEjercicios;
	private Ejercicio oEjercicioActual;
	private Objeto oObjetoActual;
	private Objeto oObjetoReconocido;

	private ObjetoDataSource dsObjetos;

	private List<Resultado> lResultados = new ArrayList<Resultado>();
	private Resultado oResultadoActual;
	private ResultadoDataSource dsResultado;

	private boolean bEsperandoRespuesta = false;
	private boolean bJuegoIniciado = false;

	private static String acierto = "Acierto ";
	private static String error = "Error ";

	private AssetFileDescriptor afd;
	private MediaPlayer player;

	private boolean bVistaCapturar;

	private int nWidth = -1;
	private int nHeight = -1;
	private boolean bPrimeraVez = false;

	private Toast mToast = null;

	// //////////////
	// Iniciar cámara
	// //////////////
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	// /////////////////
	// Iniciar actividad
	// /////////////////
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_juego);

		ttobj = new TextToSpeech(getApplicationContext(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status != TextToSpeech.ERROR) {
							Locale locale = new Locale("spa", "ESP");
							ttobj.setLanguage(locale);
						}
					}
				});

		mToast = Toast
				.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

		Bundle extras;
		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if (extras == null) {
				AlumnoDataSource dsAlumnos = new AlumnoDataSource(Juego.this);
				dsAlumnos.open();
				SerieEjerciciosDataSource dsSeries = new SerieEjerciciosDataSource(
						Juego.this);
				dsSeries.open();
				try {
					oAlumno = dsAlumnos.getAllAlumnos().get(0);
					oSerie = dsSeries.getAllSeriesEjercicios().get(0);
				} catch (Exception e) {
					oAlumno = new Alumno();
					oSerie = new SerieEjercicios();
				}
				bCiclico = false;
				dsAlumnos.close();
				dsSeries.close();
			} else {
				try {
					oAlumno = (Alumno) extras.getSerializable("Alumno");
					oSerie = (SerieEjercicios) extras.getSerializable("Serie");
					bCiclico = extras.getBoolean("Ciclico");
				} catch (Exception e) {
					oAlumno = new Alumno();
					oSerie = new SerieEjercicios();
					bCiclico = false;
				}
				try {
					bVistaCapturar = extras.getBoolean("VistaCapturar");
				} catch (Exception e) {
					bVistaCapturar = false;
				}
			}
		} else {
			try {
				oAlumno = (Alumno) savedInstanceState.getSerializable("Alumno");
				oSerie = (SerieEjercicios) savedInstanceState
						.getSerializable("Serie");
				bCiclico = savedInstanceState.getBoolean("Ciclico");
			} catch (Exception e) {
				oAlumno = new Alumno();
				oSerie = new SerieEjercicios();
				bCiclico = false;
			}
			try {
				bVistaCapturar = savedInstanceState.getBoolean("VistaCapturar");
			} catch (Exception e) {
				bVistaCapturar = false;
			}
		}

		dsObjetos = new ObjetoDataSource(this);
		dsObjetos.open();

		if (!bVistaCapturar) { // Modo Juego

			setTitle(oAlumno.getNombre() + " - " + oSerie.getNombre());

			dsResultado = new ResultadoDataSource(this);
			dsResultado.open();
			dsEjercicios = new EjercicioDataSource(this);
			dsEjercicios.open();

			try {
				lEjercicios = dsEjercicios.getAllEjercicios(oSerie);
				oEjercicioActual = lEjercicios.get(0);
			} catch (Exception e) {
				lEjercicios = dsEjercicios.getAllEjercicios();
				if (lEjercicios.size() > 0)
					oEjercicioActual = lEjercicios.get(0);
				else
					oEjercicioActual = new Ejercicio();
			}
			((ImageView) findViewById(R.id.btnCapturar))
					.setVisibility(View.GONE);
			((Globals) getApplication()).JuegoParado = true;
			Intent descEjer = new Intent(this, ComenzarEjercicio.class);
			descEjer.putExtra("idEjercicio", oEjercicioActual.getIdEjercicio());
			startActivity(descEjer);
			JuegoLibreria.RefrescarBotones(Juego.this, bEsperandoRespuesta);
			iniciarJuego();
		} else { // Modo añadir objeto
			((Globals) getApplication()).JuegoParado = false;
			setTitle("Añadir objeto");
			((LinearLayout) findViewById(R.id.layoutBotones))
					.setVisibility(View.GONE);
			((ImageView) findViewById(R.id.btnReconocer))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutCrono))
					.setVisibility(View.GONE);
		}
		((LinearLayout) findViewById(R.id.layoutJugar))
				.setVisibility(View.GONE);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView2);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	// ///////////////////
	// Empezar a reconocer
	// ///////////////////
	private void iniciarJuego() {

		bEsperandoRespuesta = false;
		nObjeto = -1;
		bJuegoIniciado = true;

		lObjetosEscenario = dsObjetos.getAllObjetosEscenario(oEjercicioActual);
		lObjetos = dsObjetos.getAllObjetosReconocer(oEjercicioActual);

		if (lObjetos.size() > 0) {
			oObjetoActual = lObjetos.get(0);
			rellenar(false);
		} else {
			oObjetoActual = new Objeto();
		}

		oResultadoActual = new Resultado();
		oResultadoActual.setFechaRealizacion(new GregorianCalendar().getTime());
		oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
		oResultadoActual.setIdEjercicio(oSerie.getIdSerie());

		if (oObjetoActual != null) {
			mToast.setText("Buscando " + oObjetoActual.getNombre());
			oObjetoActual.playSonidoDescripcion(getApplicationContext());
			mToast.show();
		}

	}

	// ///////////////////////////////////////////////////////
	// Decidir siguiente objeto a reconocer y de que ejercicio
	// ///////////////////////////////////////////////////////
	private boolean actualizaJuego() {

		boolean lbJuegoTerminado = true;

		int lnObjetoActual = JuegoLibreria.getObjetoActual(lObjetos,
				oObjetoActual);

		if (lnObjetoActual >= 0) {

			lnObjetoActual++;

			if (lnObjetoActual < lObjetos.size()) {

				oObjetoActual = lObjetos.get(lnObjetoActual);
				lbJuegoTerminado = false;

			} else { // Era último objeto del ejercicio lnObjetoActual ==
						// lObjetos.size()

				if (lResultados == null)
					lResultados = new ArrayList<Resultado>();

				int lnEjercicioActual = JuegoLibreria.getEjercicioActual(
						lEjercicios, oEjercicioActual);

				if (lnEjercicioActual >= 0) {

					lnEjercicioActual++;

					if (lnEjercicioActual == lEjercicios.size() && bCiclico) {
						lnEjercicioActual = 0;
						oResultadoActual.setDuracion(JuegoLibreria
								.getDuracionActual(this));
						oResultadoActual.calculaPuntuacion();
						lResultados.add(oResultadoActual);
					}

					if (lnEjercicioActual < lEjercicios.size()) {
						oResultadoActual = new Resultado();
						oResultadoActual
								.setFechaRealizacion(new GregorianCalendar()
										.getTime());
						oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
						oResultadoActual.setIdEjercicio(oSerie.getIdSerie());

						oEjercicioActual = lEjercicios.get(lnEjercicioActual);

						lnObjetoActual = 0;

						lObjetosEscenario = dsObjetos
								.getAllObjetosEscenario(oEjercicioActual);
						lObjetos = dsObjetos
								.getAllObjetosReconocer(oEjercicioActual);
						oObjetoActual = lObjetos.get(lnObjetoActual);
						rellenar(false);

						lbJuegoTerminado = false;

						Intent descEjer = new Intent(this,
								ComenzarEjercicio.class);
						descEjer.putExtra("idEjercicio",
								oEjercicioActual.getIdEjercicio());
						startActivity(descEjer);
					}
				}
			}
		}

		if (lbJuegoTerminado)
			terminarJuego();
		else {
			JuegoLibreria.MostrarAnimacion(Juego.this,
					((TextView) findViewById(R.id.tvAnimacion)), "Buscando "
							+ oObjetoActual.getNombre(), null);
			oObjetoActual.playSonidoDescripcion(getApplicationContext());
		}
		return lbJuegoTerminado;

	}

	// ////////////////////////////////////////////////////////////////////////
	// Rellenar matrices de KeyPoints y Descriptors en la actividad y en nativo
	// ////////////////////////////////////////////////////////////////////////
	private void rellenar(boolean aniadir) {

		rowsArray = null;
		colsArray = null;
		matsDescriptores.clear();
		matsKeyPoints.clear();

		colsArray = new int[lObjetosEscenario.size()];
		rowsArray = new int[lObjetosEscenario.size()];

		for (int i = 0; i < lObjetosEscenario.size(); i++) {
			colsArray[i] = lObjetosEscenario.get(i).getCols();
			rowsArray[i] = lObjetosEscenario.get(i).getRows();
			matsDescriptores.add(lObjetosEscenario.get(i).matDescriptores);
			matsKeyPoints.add(lObjetosEscenario.get(i).matKeyPoints);
		}

		long[] tempAddrDesc = new long[matsDescriptores.size()];
		long[] tempAddrKeyP = new long[matsKeyPoints.size()];
		int[] tempCols = new int[colsArray.length];
		int[] tempRows = new int[rowsArray.length];

		for (int i = 0; i < matsDescriptores.size(); i++) {
			tempAddrDesc[i] = matsDescriptores.get(i).getNativeObjAddr();
			tempAddrKeyP[i] = matsKeyPoints.get(i).getNativeObjAddr();
			tempCols[i] = lObjetosEscenario.get(i).getCols();
			tempRows[i] = lObjetosEscenario.get(i).getRows();
		}

		RellenarObjetos(tempAddrDesc, tempAddrKeyP, tempCols, tempRows);

		tempAddrDesc = null;
		tempAddrKeyP = null;
		tempCols = null;
		tempRows = null;

	}

	// //////////////////////////////////////////
	// Función llamada cuando se termina un juego
	// //////////////////////////////////////////
	private void terminarJuego() {
		final Runnable runTerminaJuego = new Runnable() {
			public void run() {
				Intent intent = new Intent(Juego.this, ResultadoSerie.class);
				int[] ids = new int[lResultados.size()];
				for (int i = 0; i < lResultados.size(); i++) {
					dsResultado.createResultado(lResultados.get(i));
					ids[i] = lResultados.get(i).getIdResultado();
				}
				intent.putExtra("Resultados", ids);
				intent.putExtra("Alumno", oAlumno.getNombre());
				intent.putExtra("Serie", oSerie.getNombre());
				startActivity(intent);
				Juego.this.finish();
			}
		};
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					oResultadoActual.setDuracion(JuegoLibreria
							.getDuracionActual(Juego.this));
					oResultadoActual.calculaPuntuacion();
					lResultados.add(oResultadoActual);
					break;
				}
				JuegoLibreria.MostrarAnimacion(Juego.this,
						((TextView) Juego.this.findViewById(R.id.tvAnimacion)),
						"¡Juego terminado!", runTerminaJuego);
			}
		};

		if ((lResultados == null || lResultados.isEmpty())
				|| (bCiclico && bJuegoIniciado)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(Juego.this);
			builder.setMessage(
					"¿Desea guardar el resultado actual, a pesar de no haber finalizado la serie "
							+ oSerie.getNombre() + "?")
					.setPositiveButton("Sí", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
		}
	}

	// //////////////////////////////////////////////////////////////////
	// Se ha hecho click en terminar juego y se muestra actividad resumen
	// //////////////////////////////////////////////////////////////////
	public void onClickSalir(View v) {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.quick_alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				terminarJuego();
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		v.startAnimation(animation);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ayuda y se escucha la ayuda asociada al objeto a
	// reconocer actualmente
	// /////////////////////////////////////////////////////////////////////
	public void onAyudaClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.quick_alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				if (oObjetoActual != null)
					oObjetoActual.playSonidoAyuda(Juego.this);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		v.startAnimation(animation);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en descripción y se escucha la descripción asociada
	// al objeto a reconocer actualmente
	// /////////////////////////////////////////////////////////////////////
	public void onDescripcionClick(View v) {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.quick_alpha);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				if (oObjetoActual != null)
					oObjetoActual.playSonidoDescripcion(Juego.this);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		v.startAnimation(animation);

	}

	// ////////////////////////////////////////////////////////////////////
	// Se ha hecho click en SURF y se inicializan los valores del algoritmo
	// ////////////////////////////////////////////////////////////////////
	public void onInicializaSurf(View v) {
		/*
		 * InicializaSurf(
		 * Double.parseDouble(edthessianThreshold.getText().toString()),
		 * Integer.parseInt(edtnOctaves.getText().toString()),
		 * Integer.parseInt(edtnOctaveLayers.getText().toString()),
		 * chkExtended.isChecked(), chkUpright.isChecked());
		 */
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.valores_reconocimiento);
		dialog.setTitle("Valores del algoritmo");
		final EditText edtThreeshold = (EditText) dialog
				.findViewById(R.id.edtThreeshold);
		final EditText edtPorcentaje = (EditText) dialog
				.findViewById(R.id.edtPorcentaje);
		final EditText edtMatcher = (EditText) dialog
				.findViewById(R.id.edtMatcher);
		edtThreeshold.setText("800");
		edtPorcentaje.setText("0.2");
		edtMatcher.setText("1");
		dialog.show();
		Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
		btnAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int lMatcher = Integer
						.parseInt(edtMatcher.getText().toString());
				double lPorcentaje = Double.parseDouble(edtPorcentaje.getText()
						.toString());
				double lThreeshold = Double.parseDouble(edtThreeshold.getText()
						.toString());
				CambiarValoresAlgoritmo(lMatcher, lPorcentaje, lThreeshold);
				dialog.dismiss();
			}
		});
	}

	// //////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ver objetos a reconocer y se muestran los objetos
	// a reconocer en este ejercicio
	// //////////////////////////////////////////////////////////////////////
	public void onVerObjetosReconocer(View v) {
		JuegoLibreria.onVerObjetos(this, dsObjetos, oEjercicioActual,
				dsEjercicios, bJuegoIniciado, true);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ver objetos con los que se jugarán en el ejercicio
	// actual y se muestran todos los objetos de este ejercicio
	// ///////////////////////////////////////////////////////////////////////
	public void onVerObjetosEscenario(View v) {
		JuegoLibreria.onVerObjetos(this, dsObjetos, oEjercicioActual,
				dsEjercicios, bJuegoIniciado, false);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en acierto y se suma un acierto al resultado actual
	// /////////////////////////////////////////////////////////////////////
	public void onAciertoClick(View v) {
		if (bEsperandoRespuesta) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.quick_alpha);
			animation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					JuegoLibreria.renaudaCrono(Juego.this);
					oResultadoActual.incrementaAcierto();
					if (actualizaJuego()) {
						mToast.setText("El juego ha acabado con un resultado de "
								+ lResultados.get(lResultados.size() - 1)
										.getPuntuacion());
						mToast.show();
						bJuegoIniciado = false;
					}
					bEsperandoRespuesta = false;
					JuegoLibreria.RefrescarBotones(Juego.this,
							bEsperandoRespuesta);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
			v.startAnimation(animation);
		}
	}

	// //////////////////////////////////////////////////////////////////
	// Se ha hecho click en fallo del programa y se no se suma ni acierto
	// ni fallo al resultado actual, y se sigue buscando
	// //////////////////////////////////////////////////////////////////
	public void onErrorProgramaClick(View v) {
		if (bEsperandoRespuesta) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.quick_alpha);
			animation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					bEsperandoRespuesta = false;
					JuegoLibreria.renaudaCrono(Juego.this);
					JuegoLibreria.RefrescarBotones(Juego.this,
							bEsperandoRespuesta);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
			v.startAnimation(animation);
		}
	}

	// /////////////////////////////////////////////////////////////////
	// Se ha hecho click en fallo y se suma un fallo al resultado actual
	// /////////////////////////////////////////////////////////////////
	public void onErrorClick(View v) {
		if (bEsperandoRespuesta) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.quick_alpha);
			animation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					JuegoLibreria.renaudaCrono(Juego.this);
					oResultadoActual.incrementaFallo();
					bEsperandoRespuesta = false;
					JuegoLibreria.RefrescarBotones(Juego.this,
							bEsperandoRespuesta);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
			v.startAnimation(animation);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Se ha hecho click en siguiente objeto y se suma un fallo al resultado
	// actual
	// ////////////////////////////////////////////////////////////////////////////
	public void onSiguienteClick(View v) {
		if (bJuegoIniciado) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.quick_alpha);
			animation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					JuegoLibreria.renaudaCrono(Juego.this);
					oResultadoActual.incrementaFallo();
					actualizaJuego();
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
			v.startAnimation(animation);
		}
	}

	// ////////////////////////////////////////////////////////////////////
	// Se ha hecho click en iniciar reconocimiento y se empieza a reconocer
	// objetos
	// ////////////////////////////////////////////////////////////////////
	public void onReconocerClick(View v) {
		if (!bJuegoIniciado && mOpenCvCameraView.isEnabled()) {
			iniciarJuego();
			JuegoLibreria.iniciarCrono(this);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Se ha hecho click en capturar objeto y se muestra un diálogo para crear
	// un objeto. Este formulario contiene la imagen original y la imagen
	// procesada, con el que se podrá guardar en la base de datos o descartarlo
	// ////////////////////////////////////////////////////////////////////////
	public void onCapturarClick(View v) {

		if (!bJuegoIniciado && mOpenCvCameraView.isEnabled()) {

			auxGray = mGray.clone();
			aux = mRgba.clone();

			Imgproc.GaussianBlur(auxGray, auxGray, new Size(3, 3), 2);
			Imgproc.GaussianBlur(aux, aux, new Size(3, 3), 2);
			if (nWidth != -1 && nHeight != -1) {
				Imgproc.resize(auxGray, auxGray, new Size(nWidth, nHeight));
				Imgproc.resize(aux, aux, new Size(nWidth, nHeight));
			}
			float elapsedTime = FindFeatures(auxGray.getNativeObjAddr(),
					aux.getNativeObjAddr(),
					descriptores_obj.getNativeObjAddr(),
					keypoints_obj.getNativeObjAddr());

			mToast.setText("Tiempo = " + elapsedTime + "\n" + "KeyPoints = "
					+ keypoints_obj.size());
			mToast.show();
			if (!keypoints_obj.empty() || !descriptores_obj.empty()) {

				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.activity_dialog_objeto);
				dialog.setTitle("¿Desea guardar este objeto?");

				// set the custom dialog components - image and button
				// convert to bitmap:
				Bitmap bmModificado = Bitmap.createBitmap(aux.cols(),
						aux.rows(), Bitmap.Config.ARGB_8888);
				org.opencv.android.Utils.matToBitmap(aux, bmModificado);
				((ImageView) dialog.findViewById(R.id.imageObjeto))
						.setImageBitmap(bmModificado);

				final Bitmap bmOrigen = Bitmap.createBitmap(mRgba.cols(),
						mRgba.rows(), Bitmap.Config.ARGB_8888);
				org.opencv.android.Utils.matToBitmap(mRgba, bmOrigen);
				((ImageView) dialog.findViewById(R.id.imageObjetoOriginal))
						.setImageBitmap(bmOrigen);

				Button btnAceptar = (Button) dialog
						.findViewById(R.id.btnAceptar);
				// if button is clicked, close the custom dialog
				btnAceptar.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!((EditText) dialog.findViewById(R.id.edtNombre))
								.getText().toString().trim().equals("")) {
							String keyString, desString;
							keyString = JSONParser
									.keypointsToJson(keypoints_obj);
							desString = JSONParser.matToJson(descriptores_obj);
							String nombreObjeto = ((EditText) dialog
									.findViewById(R.id.edtNombre)).getText()
									.toString();
							Objeto objeto = new Objeto(-1, nombreObjeto,
									"descripcion", new Date(), keyString,
									desString, aux.cols(), aux.rows(),
									Juego.this.getString(R.string.pathImages)
											+ "/" + nombreObjeto + ".png", "",
									"", "");
							objeto.setImagen(bmOrigen);
							descriptores_obj.release();
							keypoints_obj.release();
							aux.release();
							auxGray.release();
							if (dsObjetos.createObjeto(objeto) == null)
								new AlertDialog.Builder(Juego.this)
										.setTitle(
												"No se ha podido insertar el objeto")
										.setPositiveButton("Aceptar", null)
										.setMessage(
												"El objeto " + nombreObjeto
														+ " ya existe").show();
							else {
								dialog.dismiss();
								JuegoLibreria.insertaEnSerie(Juego.this,
										objeto, oEjercicioActual);
							}
						} else
							new AlertDialog.Builder(Juego.this)
									.setTitle("Atención")
									.setPositiveButton("Aceptar", null)
									.setMessage(
											"Debe rellenar el campo nombre")
									.show();
					}
				});

				Button btnCancelar = (Button) dialog
						.findViewById(R.id.btnCancelar);
				// if button is clicked, close the custom dialog
				btnCancelar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						descriptores_obj.release();
						keypoints_obj.release();
						aux.release();
						auxGray.release();
					}
				});
				dialog.show();
			} else {
				mToast.setText("Es necesario capturar de nuevo el objeto");
				mToast.show();
			}
		}

	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en el frame de la cámara y se empezará a jugar o se
	// capturará un objeto depediendo de como se ha iniciado la actividad
	// /////////////////////////////////////////////////////////////////////
	public void onFrameClick(View v) {
		try {
			if (!bVistaCapturar) {
				if (!bJuegoIniciado)
					onReconocerClick(v);
			} else
				onCapturarClick(v);
		} catch (Exception e) {
		}
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (((!bJuegoIniciado || (bJuegoIniciado && nObjeto == -1)) && !bEsperandoRespuesta)
				&& !((Globals) getApplication()).JuegoParado) {
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if (bJuegoIniciado && lObjetos.size() > 0) {
				auxGray = mGray.clone();

				Imgproc.GaussianBlur(auxGray, auxGray, new Size(3, 3), 2);
				if (nWidth != -1 && nHeight != -1)
					Imgproc.resize(auxGray, auxGray, new Size(nWidth, nHeight));

				nObjeto = FindObjects(auxGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr());

				mGray = auxGray.clone();
				auxGray.release();

				if (nObjeto != -1) {
					int id = (int) lObjetosEscenario.get(nObjeto).getId();
					bEsperandoRespuesta = true;
					JuegoLibreria.pausaCrono(this);

					oObjetoReconocido = dsObjetos.getObjetoSimple(id);
					String sRespuesta = "";

					if (oObjetoReconocido.getId() == oObjetoActual.getId()) {
						sRespuesta = acierto;
						try {
							afd = getAssets().openFd("acierto.mp3");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						sRespuesta = error;
						try {
							afd = getAssets().openFd("fallo.mp3");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					final String respuesta = sRespuesta;
					this.runOnUiThread(new Runnable() {
						public void run() {
							JuegoLibreria.RefrescarBotones(Juego.this,
									bEsperandoRespuesta);
							oObjetoReconocido
									.playSonidoNombre(getApplicationContext());
							while (oObjetoReconocido.playerSonando())
								;
							try {
								player = new MediaPlayer();
								player.setDataSource(afd.getFileDescriptor(),
										afd.getStartOffset(), afd.getLength());
								player.prepare();
								player.start();

							} catch (IOException e) {
								e.printStackTrace();
							}
							ttobj.speak(respuesta, TextToSpeech.QUEUE_FLUSH,
									null);
							mToast.setText(respuesta
									+ " - encontrado el objeto "
									+ oObjetoReconocido.getNombre());
							mToast.show();
						}
					});
					nObjeto = -1;
				}
			}
		}
		return mRgba;
	}

	@Override
	public void onPause() {
		if (dsObjetos != null)
			dsObjetos.close();
		if (dsEjercicios != null)
			dsEjercicios.close();
		if (dsResultado != null)
			dsResultado.close();
		if (ttobj != null) {
			ttobj.stop();
			ttobj.shutdown();
		}
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		super.onPause();
	}

	@Override
	public void onResume() {
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
		if (dsObjetos != null)
			dsObjetos.open();
		if (dsEjercicios != null)
			dsEjercicios.open();
		if (dsResultado != null)
			dsResultado.open();
		mOpenCvCameraView.enableView();
		if (bJuegoIniciado && !bPrimeraVez) {
			JuegoLibreria.iniciarCrono(Juego.this);
			bPrimeraVez = true;
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		colsArray = null;
		rowsArray = null;
		matsDescriptores = null;
		matsKeyPoints = null;
		Utilidades.LiberaImagenes(lObjetos);
		Utilidades.LiberaImagenes(lObjetosEscenario);
		lObjetos = null;
		lObjetosEscenario = null;
		LiberaObjetos();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		aux = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
		auxGray = new Mat(height, width, CvType.CV_8UC1);
		if (width > ((Globals) getApplication()).width)
			nWidth = ((Globals) getApplication()).width;
		if (height > ((Globals) getApplication()).height)
			nHeight = ((Globals) getApplication()).height;
	}

	@Override
	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
		aux.release();
		auxGray.release();
		descriptores_obj.release();
		keypoints_obj.release();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !bVistaCapturar)
			terminarJuego();
		return super.onKeyDown(keyCode, event);
	}

	// Procesa una imagen aplicacion el algoritmo SURF
	public native float FindFeatures(long matAddrGr, long matAddrRgba,
			long matAddrDescriptores, long matAddrKeyPoints);

	// Devuelve el id del objeto encontrado por el algoritmo SURF
	public native int FindObjects(long matAddrGray, long matAddrRgba);

	// Crear todas las matrices de KeyPoints y Descriptors que se usarán para
	// encontrar objetos
	public native int RellenarObjetos(long[] descriptors, long[] keyPoints,
			int[] cols, int[] rows);

	// Libera recursos creados en la parte nativa
	public native int LiberaObjetos();

	// Inicializa los valores que usa el algoritmo SURF
	public native void InicializaSurf(double phessian, int pnOctaves,
			int pnOctaveLayers, boolean pExtended, boolean pUpright);

	// //////////////////////////
	// PRUEBAS /////////////////
	// //////////////////////////
	public native void CambiarValoresAlgoritmo(int pMatcher, double pPorcenaje,
			double pThreesHold);

}
